/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wormsim.data;

import com.wormsim.animals.AnimalZoo;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.function.IntPredicate;
import java.util.function.LongPredicate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An object which represents the collection of simulation options combined from
 * the command line and the "input.txt" file.
 *
 * @author ah810
 * @version 0.0.1
 */
public class SimulationOptions {

	private static final Logger LOG = Logger.getLogger(SimulationOptions.class
					.getName());
	/**
	 * A string denoting the keyword for the animal definitions block.
	 */
	public static final String ANIMAL_ZOO = "animal_zoo";
	/**
	 * A string denoting the keyword for the assay iterations number.
	 */
	public static final String ASSAY_ITERATION_NO = "assay_iteration_no";
	/**
	 * A string denoting the keyword for the number of iterations to burn before
	 * recording.
	 */
	public static final String BURN_IN_NO = "burn_in_iteration_no";
	/**
	 * A string denoting the keyword for the checkpoint number.
	 */
	public static final String CHECKPOINT_NO = "checkpoint_no";
	/**
	 * A string denoting the keyword for detailed data output.
	 */
	public static final String DETAILED_DATA = "detailed_data";
	/**
	 * A string denoting the keyword for the force run flag.
	 */
	public static final String FORCED_RUN = "force_run";
	/**
	 * A string denoting the keyword for the initial conditions block.
	 */
	public static final String INITIAL_CONDITIONS = "initial_conditions";
	/**
	 * A string denoting the name of the input file.
	 */
	public static final String INPUT_TXT = "input.txt";
	/**
	 * A string denoting the keyword for the new run flag.
	 */
	public static final String NEW_RUN = "new_run";
	/**
	 * A string denoting the keyword for the number of pheromones.
	 */
	public static final String PHEROMONE_NO = "pheromone_no";
	/**
	 * A string denoting the keyword for the frequency of recordings to use.
	 */
	public static final String RECORD_FREQ_NO = "record_freq_no";
	/**
	 * A string denoting the keyword for the number of iterations to record for.
	 */
	public static final String RECORD_NO = "recording_iteration_no";
	/**
	 * A string denoting the keyword for the random seed.
	 */
	public static final String SEED = "seed";
	/**
	 * A string denoting the keyword for the number of threads to run.
	 */
	public static final String THREAD_NO = "thread_no";
	/**
	 * A string denoting the keyword for the number of walkers to use.
	 */
	public static final String WALKER_NO = "walker_no";

	/**
	 * Reads the file as an "input.txt" file format and throws an
	 * IllegalArgumentException whenever it reads something that it fails to
	 * understand.
	 *
	 * @param input The "input.txt" file.
	 *
	 * @return A map of the contained data.
	 *
	 * @throws java.io.IOException If there is an error reading the file.
	 */
	public static HashMap<String, Object> readInput(File input)
					throws IOException {
		if (input == null) {
			throw new NullPointerException("Provided input file is a null pointer!");
		} else if (!input.exists()) {
			throw new FileNotFoundException(
							"There is no \"input.txt\" to read from!");
		}

		HashMap<String, Object> data = new HashMap<>(64);

		int line_no = 1;
		try (BufferedReader in = new BufferedReader(new FileReader(input))) {
			for (String line = in.readLine(); line != null; line = in.readLine(), line_no++) {
				if (line.matches("\\s*#.*")) {
					continue;
				}
				int index = line.indexOf('=');
				if (index == -1) {
					continue;
				}
				String key = line.substring(0, index - 1).trim().toLowerCase(Locale
								.getDefault());
				String entry = line.substring(index, line.indexOf('#')).trim();
				switch (key) {
					case THREAD_NO:
					case WALKER_NO:
					case BURN_IN_NO:
					case RECORD_NO:
					case PHEROMONE_NO:
					case CHECKPOINT_NO: {
						data.put(key, readInteger(key, entry, (i) -> i > 0));
						break;
					}
					case SEED: {
						data.put(key, readLong(key, entry, (i) -> i > 0));
						break;
					}
					case INITIAL_CONDITIONS: {
						// In this case there is more to it
						line_no++;
						HashMap<String, String> data2 = new HashMap<>(16);
						for (String line2 = in.readLine(); line != null; line = in
										.readLine(), line_no++) {
							if (line2.contains("~")) {
								int index2 = line2.indexOf('~');
								String key2 = line2.substring(0, index2 - 1).trim().toLowerCase(
												Locale.getDefault());
								String entry2 = line2.substring(index2).trim();
								data2.put(key2, entry2);
							}
						}
						data.put(key, new SimulationConditions(data2));
						break;
					}
					case ANIMAL_ZOO: {
						// In this case there is more to it
						line_no++;
						AnimalZoo.Builder zoo = new AnimalZoo.Builder();
						for (String line2 = in.readLine().trim(); line != null; line = in
										.readLine().trim(), line_no++) {
							if (line2.startsWith("strain ")) {
								zoo.addAnimalStrain(line2.substring(6).trim());
							} else if (line2.startsWith("stage ")) {

							} else {

							}
						}
						data.put(key, zoo);
						break;
					}
				}
			}
		} catch (IOException ex) {
			throw new IOException(
							"Unable to read from the specified \"input.txt\"! Error when attempting to read line "
							+ line_no + ".", ex);
		}

		return data;
	}

	/**
	 * Reads the integer value indicated by the value at the key address. NOTE:
	 * the key is provided for debugging purposes.
	 *
	 * @param key       The key of the data entry
	 * @param value     The string representation of the data at the key.
	 * @param condition The condition under which that data is accepted, or null
	 *                  if it is accepted anyway.
	 *
	 * @return True if the data was an integer and fulfilled the condition or the
	 *         condition was null.
	 *
	 * @throws java.io.IOException If the data is not an integer or does not
	 *                             fulfil the condition.
	 */
	public static Integer readInteger(String key, String value,
																		IntPredicate condition)
					throws IOException {
		Integer i;
		try {
			i = Integer.valueOf(value.trim());
		} catch (NumberFormatException ex) {
			throw new IOException("Must provide an integer value for "
							+ key + ", provided \"" + value + "\".", ex);
		}
		if (condition == null || condition.test(i)) {
			return i;
		} else {
			throw new IOException("Integer provided for " + key + " is "
							+ "invalid, see the handbook for guidance. Provided \"" + value
							+ "\".");
		}
	}

	/**
	 * Reads the long value indicated by the value at the key address. NOTE: the
	 * key is provided for debugging purposes.
	 *
	 * @param key       The key of the data entry
	 * @param value     The string representation of the data at the key.
	 * @param condition The condition under which that data is accepted, or null
	 *                  if it is accepted anyway.
	 *
	 * @return True if the data was a long and fulfilled the condition or the
	 *         condition was null.
	 *
	 * @throws java.io.IOException If the data is not an long or does not fulfil
	 *                             the condition.
	 */
	public static Long readLong(String key, String value, LongPredicate condition)
					throws IOException {
		Long i;
		try {
			i = Long.valueOf(value.trim());
		} catch (NumberFormatException ex) {
			throw new IOException("Must provide a long value for "
							+ key + ", provided \"" + value + "\".", ex);
		}
		if (condition == null || condition.test(i)) {
			return i;
		} else {
			throw new IOException("Long provided for " + key + " is "
							+ "invalid, see the handbook for guidance. Provided \"" + value
							+ "\".");
		}
	}

	/**
	 * Creates a new set of options using the specified commands and inferring the
	 * directory as the directory the program was launched within.
	 *
	 * @param cmds An object representing the command line arguments.
	 *
	 * @throws java.io.IOException If there is an error reading the "input.txt"
	 *                             file.
	 */
	public SimulationOptions(SimulationCommands cmds)
					throws IOException {
		this.input = new File(cmds.getDirectory(), INPUT_TXT);
		HashMap<String, Object> data = readInput(input);
		this.cmds = cmds;
		int temp_thread_no = cmds.getThreadNumber() <= 0
						? (data.containsKey(THREAD_NO)
						? (Integer) data.get(THREAD_NO)
						: Integer.MAX_VALUE)
						: cmds.getThreadNumber();
		this.thread_no = Math.min(temp_thread_no,
						Runtime.getRuntime().availableProcessors());
		if (temp_thread_no != this.thread_no) {
			LOG.log(Level.WARNING,
							"Requested thread number was higher than available cores or unspecified, using {0} cores instead.",
							this.thread_no);
		}
		this.walker_no = (Integer) data.get(WALKER_NO);
		this.seed = data.containsKey(SEED)
						? (Long) data.get(SEED)
						: System.nanoTime();

		// TODO: Need something better than the get routine so that it can be properly recorded as something going wrong.
		this.burn_in_no = (Integer) data.get(BURN_IN_NO);
		this.record_no = (Integer) data.get(RECORD_NO);
		this.pheromone_no = (Integer) data.get(PHEROMONE_NO);
		this.checkpoint_no = (Integer) data.get(CHECKPOINT_NO);
		this.assay_iteration_no = (Integer) data.get(ASSAY_ITERATION_NO);
		this.initial_conditions = (SimulationConditions) data
						.get(INITIAL_CONDITIONS);

		this.directory = cmds.getDirectory();

		this.forced_run = (Boolean) data.getOrDefault(FORCED_RUN, Boolean.FALSE);
		this.detailed_data = (Boolean) data
						.getOrDefault(DETAILED_DATA, Boolean.TRUE);
		this.new_run = cmds.getThreadNumber() == -1
						? (Boolean) data.get(NEW_RUN)
						: (cmds.getThreadNumber() != 0);
		this.timeout = cmds.getTimeout() < 0L
						? Long.MAX_VALUE
						: System.currentTimeMillis() + cmds.getTimeout();
		this.record_freq_no = (Integer) data.getOrDefault(RECORD_FREQ_NO, 1);
		this.zoo = ((AnimalZoo) data.get(ANIMAL_ZOO)).create(
						this.pheromone_no);
	}
	private final int assay_iteration_no;
	private final int burn_in_no;
	private final int checkpoint_no;
	private final SimulationCommands cmds;
	private final boolean detailed_data;
	private final File directory;
	private final boolean forced_run;
	private final SimulationConditions initial_conditions;
	private final File input;
	private final boolean new_run;
	private final int pheromone_no;
	private final int record_freq_no;
	private final int record_no;
	private final long seed;
	private final int thread_no;
	private final long timeout;
	private final int walker_no;
	private final AnimalZoo zoo;

	/**
	 * Returns the number of iterations which are to be used in approximating the
	 * fitness factor.
	 *
	 * @return
	 */
	public int getAssayIterationNumber() {
		return this.assay_iteration_no;
	}

	/**
	 * Returns the number of iterations that will be used for burn-in where no
	 * data is recorded initially.
	 *
	 * @return
	 */
	public int getBurnInNumber() {
		return this.burn_in_no;
	}

	/**
	 * Returns the number of iterations to conduct before making a checkpoint.
	 *
	 * @return
	 */
	public int getCheckpointNumber() {
		return checkpoint_no;
	}

	/**
	 * Returns the working directory of this simulation.
	 *
	 * @return
	 */
	public File getDirectory() {
		return directory;
	}

	/**
	 * Returns true if the simulation is to be run even when files are to be
	 * overwritten. Note that it does not necessarily remove all files so it is
	 * best to only use this when debugging.
	 *
	 * @return
	 */
	public boolean getForcedRun() {
		return forced_run;
	}

	/**
	 * Returns the object representing the initial conditions of the simulation.
	 *
	 * @return
	 */
	public SimulationConditions getInitialConditions() {
		return initial_conditions;
	}

	/**
	 * Returns true if the simulation is to be run from the start, or false if it
	 * is to be resumed using an appropriate "checkpoint#.data" or "out.data"
	 * renamed to "in.data".
	 *
	 * @return
	 */
	public boolean getNewRun() {
		return new_run;
	}

	/**
	 * Returns the number of pheromone channels that are to be run with.
	 *
	 * @return
	 */
	public int getPheromoneNumber() {
		return pheromone_no;
	}

	/**
	 * Returns the seed used to begin this simulation.
	 *
	 * @return
	 */
	public Long getRandomSeed() {
		return seed;
	}

	public boolean getRecordDetailedData() {
		return this.detailed_data;
	}

	/**
	 * Returns the frequency of iterations that will be used for data recording.
	 *
	 * @return
	 */
	public int getRecordingFrequencyNumber() {
		return record_freq_no;
	}

	/**
	 * Returns the number of iterations that will be used for data recording.
	 *
	 * @return
	 */
	public int getRecordingNumber() {
		return record_no;
	}

	/**
	 * Returns the number of threads this simulation is to be run with.
	 *
	 * @return
	 */
	public int getThreadNumber() {
		return thread_no;
	}

	/**
	 * Returns the time that the program should shutdown at in milliseconds.
	 *
	 * @return
	 */
	public long getTimeoutTime() {
		return timeout;
	}

	/**
	 * Returns the total number of iterations that will be run, including burn-in
	 * and recording.
	 *
	 * @return
	 */
	public int getTotalNumber() {
		return burn_in_no + record_no;
	}

	/**
	 * Returns the number of walkers this simulation is to run with.
	 *
	 * @return
	 */
	public int getWalkerNumber() {
		return walker_no;
	}

	/**
	 * Returns the animal zoo being used for this simulation.
	 *
	 * @return
	 */
	public AnimalZoo getZoo() {
		return zoo;
	}

	/**
	 * Outputs the data of this object in the "input.txt" file format.
	 *
	 * @param out
	 *
	 * @throws IOException
	 */
	public void write(BufferedWriter out)
					throws IOException {
		// TODO: Include explanations of parameters and make prettier in the ordering
		// Also will end up including many more parameters.
		out.write(SEED);
		out.write("=");
		out.write(Long.toString(seed));
		out.newLine();
		out.write(THREAD_NO);
		out.write("=");
		out.write(Integer.toString(thread_no));
		out.newLine();
		out.write(WALKER_NO);
		out.write("=");
		out.write(Integer.toString(walker_no));
		out.newLine();
		out.write(BURN_IN_NO);
		out.write("=");
		out.write(Integer.toString(burn_in_no));
		out.newLine();
		out.write(RECORD_NO);
		out.write("=");
		out.write(Integer.toString(record_no));
		out.newLine();
		out.write(RECORD_FREQ_NO);
		out.write("=");
		out.write(Integer.toString(record_freq_no));
		out.newLine();
		out.write(PHEROMONE_NO);
		out.write("=");
		out.write(Integer.toString(pheromone_no));
		out.newLine();
		out.write(CHECKPOINT_NO);
		out.write("=");
		out.write(Integer.toString(checkpoint_no));
		out.newLine();
		out.write(INITIAL_CONDITIONS);
		out.write("=");
		initial_conditions.write(out);
		out.newLine();
		out.write(FORCED_RUN);
		out.write("=");
		out.write(Boolean.toString(forced_run));
		out.newLine();
		out.write(NEW_RUN);
		out.write("=");
		out.write(Boolean.toString(new_run));
		out.newLine();
	}
}
