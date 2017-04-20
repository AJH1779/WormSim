/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wormsim.simulation;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.wormsim.LaunchFromFileMain;
import com.wormsim.data.SimulationOptions;
import com.wormsim.utils.Utils;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The object which handles the simulation by managing a number of walkers which
 * independently perform simulations of the model.
 *
 * @author ah810
 * @version 0.0.1
 */
public class Simulation implements Runnable {

	private static final Logger LOG = Logger.getLogger(Simulation.class
					.getSimpleName());

	/**
	 * The name of the file which stores all raw data generated over the course of
	 * the simulation. This will be a binary format file.
	 *
	 * TODO: Switch from human readable to binary.
	 *
	 * TODO: Make this file optional.
	 */
	public static final String DATA_DAT = "data.dat";
	/**
	 * The name of the file which gives human-readable brief data on the state of
	 * the system and results.
	 */
	public static final String OUT_TXT = "out.txt";

	/**
	 * Creates a new simulation object using the specified options.
	 *
	 * @param ops The options to run with
	 */
	public Simulation(@NotNull SimulationOptions ops) {
		this.options = ops;
		this.threads = new SimulationThread[options.thread_no.get()];
		this.walkers = new ArrayList<>((options.walker_no.get() * 11) / 10);
		this.rng = new Random(ops.seed.get());

		this.out_file = new File(ops.directory, OUT_TXT);
		this.data_file = new File(ops.directory, DATA_DAT);
		if (this.data_file.exists() && ops.detailed_data.get() && !ops.forced_run
						.get()) {
			LOG.log(Level.SEVERE,
							"Warning: The data.dat file already exists in the target "
							+ "directory. Delete or move it to run the program. (Located at "
							+ "{0})",
							this.data_file);
			throw new AssertionError("INVALID CODE");
		} else {
			this.data_file.delete();
		}
		if (this.out_file.exists() && !ops.forced_run.get()) {
			LOG.log(Level.SEVERE,
							"Warning: The " + OUT_TXT
							+ " file already exists in the target directory. Delete or move "
							+ "it to run the program. (Located at {0})",
							this.out_file);
			throw new RuntimeException(OUT_TXT + " file already exists!");
		}
	}
	@NotNull
	private final File data_file;
	private int iteration;
	@NotNull
	private final LinkedBlockingDeque<Walker> iteration_walkers
					= new LinkedBlockingDeque<>();
	@NotNull
	private final File out_file;
	@NotNull
	private final Random rng;
	private volatile boolean running;
	@Nullable
	private volatile Thread thread;
	@NotNull
	private final SimulationThread[] threads;
	@NotNull
	private final ArrayList<Walker> walkers;
	@NotNull
	public final SimulationOptions options;

	private void checkpoint() {
		File checkpoint_file = new File(options.directory, "checkpoint"
						+ ((iteration - options.burn_in_no.get()) / options.checkpoint_no
						.get()) + ".dat");

		// Output to the other file
		try (ObjectOutputStream out = new ObjectOutputStream(
						new BufferedOutputStream(new FileOutputStream(checkpoint_file)))) {
			// TODO: Ensure that there is a header which contains the out.txt as a
			// binary file.
			out.writeObject(options);
			for (Walker w : walkers) {
				out.writeObject(w);
			}
		} catch (IOException ex) {
			// TODO: Proper Error checking and control.
			LOG.log(Level.SEVERE, null, ex);
			setRunning(false);
		}

		// Write the output here for the different files that are important
		try (BufferedWriter out = new BufferedWriter(new FileWriter(out_file, true))) {
			out.newLine();
			out.write("Checkpoint Recorded to :" + checkpoint_file.getName());
			// TODO: Some brief details to look at.
			out.newLine();
			out.flush();
		} catch (IOException ex) {
			// TODO: Proper Error checking and control.
			Logger.getLogger(Simulation.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private void end() {
		try (BufferedWriter out = new BufferedWriter(new FileWriter(out_file, true))) {
			out.write(
							"================================================================================");
			out.newLine();
			// Append input file
			out.write("COMPLETED DATA GENERATION");
			out.newLine();
			out.write(
							"TODO: Useful summary data goes here.");
			out.newLine();
			out.write(
							"================================================================================");
			out.newLine();
			out.write("TODO: More detailed summary data goes here.");
			out.newLine();
			out.flush();
		} catch (IOException ex) {
			Logger.getLogger(Simulation.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Returns true if this iteration has reached a checkpoint and so should be
	 * recorded as a restartable place.
	 *
	 * @return If the iteration is a checkpoint
	 */
	private boolean reachedCheckpoint() {
		return options.checkpoint_no.get() > 0 && iteration % options.checkpoint_no
						.get() == 0;
	}

	/**
	 * Returns true if the iteration is the last one that has been scheduled.
	 *
	 * @return If the simulation has reached the end.
	 */
	private boolean reachedEnd() {
		return iteration >= (options.burn_in_no.get() + options.record_no.get());
	}

	/**
	 * Returns true if the data should be recorded at this iteration.
	 *
	 * @return If the iteration is a recording iteration
	 */
	private boolean reachedRecord() {
		return iteration > options.burn_in_no.get() && (iteration
						- options.burn_in_no.get()) % options.record_freq_no.get() == 0;
	}

	private void record()
					throws IOException {
		// Currently records to a simple file for the sake of getting data.
		// TODO: Record here if the otpions.getRecordDetailedData() is set to true.
		try (BufferedWriter out
						= new BufferedWriter(new FileWriter(data_file, data_file.exists()))) {
			// Records the current state of the tracked values.
			for (Walker walker : walkers) {
				out.write(walker.toString());
			}
			out.newLine();
		} catch (IOException ex) {
			LOG.log(Level.SEVERE, null, ex);
			setRunning(false);
		}
	}

	/**
	 * Resets the simulation for a new run.
	 *
	 * TODO: Modify for continuation runs.
	 */
	private void reset()
					throws IOException {
		if (out_file.exists() && !options.forced_run.get()) {
			throw new IOException(
							"The file \"" + OUT_TXT
							+ "\" already exists and would be overwritten by "
							+ "this action. Please move or delete the file before proceeding!");
		}

		walkers.clear();
		for (int i = 0; i < options.walker_no.get(); i++) {
			walkers.add(new Walker(rng.nextLong(), options.animal_zoo.get().create(
							options.pheromone_no.get())));
		}
		for (int i = 0; i < threads.length; i++) {
			threads[i] = new SimulationThread(this, iteration_walkers);
			threads[i].start();
		}
		iteration = 0;

		try (BufferedWriter out = new BufferedWriter(new FileWriter(out_file))) {
			out.write(new Scanner(LaunchFromFileMain.class.getResourceAsStream(
							"/com/wormsim/header.txt")).useDelimiter("\\Z").next()
							.replace("{authors}", Utils.AUTHORS_AS_STRING)
							.replace("{version}", Utils.VERSION)
							.replace("{reference}", Utils.REFERENCE));
			out.newLine();
			out.write(
							"================================================================================");
			out.newLine();
			// Append input file
			out.write("# input.txt");
			out.newLine();
			out.write(
							"# The following may be used as the input.txt file for repeating this simulation.");
			out.newLine();
			out.newLine();
			out.write(options.toString());
			out.write(
							"================================================================================");
			out.newLine();
			out.write("Beginning Burn-In");
			out.newLine();
			out.flush();
		} catch (IOException ex) {
			Logger.getLogger(Simulation.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Returns true if the simulation is currently running. Note that this may be
	 * slightly problematic in the case of a RuntimeException and should be
	 * altered.
	 *
	 * @return true if the thread is running.
	 */
	public boolean isRunning() {
		return running && thread != null && thread.isAlive();
	}

	// TODO: Include a method of halting the program by using isrunning.
	/**
	 * Handles the running of the simulation.
	 */
	@Override
	public void run() {
		try {
			thread = Thread.currentThread();
			setRunning(true);
			reset();
			// TODO: Is not thread safe.
			while (isRunning()) {
				// TODO: Wait for empty?
				if (iteration_walkers.isEmpty()) {
					iteration++;
					if (reachedRecord()) {
						record();
					}
					if (reachedCheckpoint()) {
						// TODO: Temporarily does nothing.
						// checkpoint();
						System.out.println("CHECKPOINT");
					}
					if (reachedEnd()) {
						end();
						setRunning(false);
					} else {
						iteration_walkers.addAll(walkers);
					}
				}
				// TODO: Handling the ingoings and outgoings of the threads.
//				for (SimulationThread t : threads) {
//					if (!t.isAlive()) {
//						isrunning = false;
//					}
//				}
			}
		} catch (IOException ex) {
			LOG.log(Level.SEVERE, "Fatal Unexpected Exception Thrown: {0}", ex);
		} finally {
			setRunning(false);
		}
	}

	/**
	 * @param running the running to set
	 */
	public void setRunning(boolean running) {
		this.running = running;
	}
}
