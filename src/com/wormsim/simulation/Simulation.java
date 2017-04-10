/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wormsim.simulation;

import com.wormsim.Main;
import static com.wormsim.Main.AUTHORS_AS_STRING;
import static com.wormsim.Main.REFERENCE;
import static com.wormsim.Main.VERSION;
import com.wormsim.data.SimulationConditions;
import com.wormsim.data.SimulationOptions;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
	 * the simulation.
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
	public Simulation(SimulationOptions ops) {
		this.options = ops;
		this.threads = new SimulationThread[options.getThreadNumber()];
		this.walkers = new ArrayList<>((options.getWalkerNumber() * 11) / 10);
		this.rng = new Random(ops.getRandomSeed());

		this.out_file = new File(ops.getDirectory(), OUT_TXT);
		this.data_file = new File(ops.getDirectory(), DATA_DAT);
		if (this.data_file.exists() && ops.getRecordDetailedData()) {
			LOG.log(Level.SEVERE,
							"Warning: The data.dat file already exists in the target directory. Delete or move it to run the program. (Located at {0})",
							this.data_file);
		}
		if (this.out_file.exists() && !ops.getForcedRun()) {
			LOG.log(Level.SEVERE,
							"Warning: The " + OUT_TXT
							+ " file already exists in the target directory. Delete or move it to run the program. (Located at {0})",
							this.out_file);
			throw new RuntimeException(OUT_TXT + " file already exists!");
		}
	}
	private final File data_file;
	private volatile boolean isrunning;
	private int iteration = 0;
	private final LinkedBlockingDeque<Walker> iteration_walkers = new LinkedBlockingDeque<>();
	private final SimulationOptions options;
	private final File out_file;
	private final Random rng;
	private volatile Thread thread;
	private final SimulationThread[] threads;
	private final ArrayList<Walker> walkers;

	private void checkpoint() {
		File checkpoint_file = new File(options.getDirectory(), "checkpoint"
						+ ((iteration - options.getBurnInNumber()) / options
						.getCheckpointNumber()) + ".txt");

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
		// Output to the other file
		try (BufferedWriter out = new BufferedWriter(new FileWriter(checkpoint_file))) {
			for (Walker w : walkers) {
				w.writeToWriter(out);
			}
			out.newLine();
		} catch (IOException ex) {
			// TODO: Proper Error checking and control.
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
		return iteration % options.getCheckpointNumber() == 0;
	}

	/**
	 * Returns true if the iteration is the last one that has been scheduled.
	 *
	 * @return If the simulation has reached the end.
	 */
	private boolean reachedEnd() {
		return iteration > options.getTotalNumber();
	}

	/**
	 * Returns true if the data should be recorded at this iteration.
	 *
	 * @return If the iteration is a recording iteration
	 */
	private boolean reachedRecord() {
		return iteration > options.getBurnInNumber() && iteration % options
						.getRecordingFrequencyNumber() == 0;
	}

	private void record() {

	}

	/**
	 * Resets the simulation for a new run.
	 *
	 * TODO: Modify for continuation runs.
	 */
	private void reset()
					throws IOException {
		if (out_file.exists()) {
			throw new IOException(
							"The file \"" + OUT_TXT
							+ "\" already exists and would be overwritten by "
							+ "this action. Please move or delete the file before proceeding!");
		}

		walkers.clear();
		for (int i = 0; i < options.getWalkerNumber(); i++) {
			walkers.add(new Walker(rng.nextLong(), options.getZoo().create(options
							.getPheromoneNumber())));
		}
		for (int i = 0; i < threads.length; i++) {
			threads[i] = new SimulationThread(this, iteration_walkers);
			threads[i].start();
		}

		try (BufferedWriter out = new BufferedWriter(new FileWriter(out_file))) {
			out.write(new Scanner(Main.class.getResourceAsStream(
							"/com/wormsim/header.txt")).useDelimiter("\\Z").next()
							.replace("{authors}", AUTHORS_AS_STRING)
							.replace("{version}", VERSION)
							.replace("{reference}", REFERENCE));
			out.write(
							"====================================================================");
			out.newLine();
			// Append input file
			out.write("# input.txt");
			out.newLine();
			out.write(
							"# The following may be used as the input.txt file for repeating this");
			out.newLine();
			out.write("# simulation:");
			out.newLine();
			out.newLine();
			this.options.write(out);
			out.write(
							"====================================================================");
			out.newLine();
			out.write("Beginning Burn-In");
			out.newLine();
			out.flush();
		} catch (IOException ex) {
			Logger.getLogger(Simulation.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Returns the initial conditions of this simulation.
	 *
	 * @return the initial conditions
	 */
	public SimulationConditions getInitialConditions() {
		return options.getInitialConditions();
	}

	/**
	 * Returns the options of this simulation.
	 *
	 * @return the options
	 */
	public SimulationOptions getOptions() {
		return options;
	}

	/**
	 * Returns true if the simulation is currently running. Note that this may be
	 * slightly problematic in the case of a RuntimeException and should be
	 * altered.
	 *
	 * @return true if the thread is running.
	 */
	public boolean isRunning() {
		return isrunning && thread.isAlive();
	}

	// TODO: Include a method of halting the program by using isrunning.
	/**
	 * Handles the running of the simulation.
	 */
	@Override
	public void run() {
		try {
			thread = Thread.currentThread();
			isrunning = true;
			reset();
			while (isrunning) {
				iteration++;
				if (iteration_walkers.isEmpty()) {
					if (reachedRecord()) {
						record();
					}
					if (reachedCheckpoint()) {
						checkpoint();
					}
					if (reachedEnd()) {
						isrunning = false;
					} else {
						iteration_walkers.addAll(walkers);
					}
				}

				// TODO: Place a limited here, but one that doesn't cause an impact on efficiency by imposing strange units on time.
			}
		} catch (IOException ex) {
			LOG.log(Level.SEVERE, "Fatal Unexpected Exception Thrown: {0}", ex);
		} finally {
			isrunning = false;
		}
	}
}
