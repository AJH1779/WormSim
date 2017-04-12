/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wormsim.data;

import com.wormsim.animals.AnimalZoo;
import com.wormsim.utils.Utils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Optional;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * An object which represents the collection of simulation options combined from
 * the command line and the "input.txt" file.
 *
 * @author ah810
 * @version 0.0.1
 */
public final class SimulationOptions2 implements Serializable {

	private static final Logger LOG = Logger.getLogger(SimulationOptions2.class
					.getName());
	private static final long serialVersionUID = 1L;
	/**
	 * A string denoting the keyword for the animal definitions block.
	 */
	public static final String ANIMAL_ZOO = "animal_zoo";
	/**
	 * A string denoting the keyword for the assay iterations number.
	 *
	 * TODO: Improve what this means and how to check it.
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
	 * A string denoting the keyword for the timing out of the program.
	 */
	public static final String TIMEOUT = "timeout";
	/**
	 * A string denoting the keyword for the number of walkers to use.
	 */
	public static final String WALKER_NO = "walker_no";

	/**
	 * Creates a new set of options using the specified commands and inferring the
	 * directory as the directory the program was launched within.
	 *
	 * @param cmds An object representing the command line arguments.
	 *
	 * @throws java.io.IOException If there is an error reading the "input.txt"
	 *                             file.
	 */
	public SimulationOptions2(SimulationCommands cmds)
					throws IOException {
		this.directory = cmds.getDirectory();
		this.input = new File(cmds.getDirectory(), INPUT_TXT);
		this.cmds = cmds;

		readData2();
		// */
		Optional<SimulationOptionSetting> unfulfilled = settings.values().stream()
						.filter((v) -> !v.isFulfilled()).findFirst();
		if (unfulfilled.isPresent()) {
			throw new IOException("Missing parameter: " + unfulfilled.get().getName());
		}
	}

	private final SimulationOptionSetting<AnimalZoo> animal_zoo
					= new SimulationOptionSetting<>(ANIMAL_ZOO, AnimalZoo::read);
	private final SimulationOptionSetting<Integer> assay_iteration_no
					= new SimulationOptionSetting<>(ASSAY_ITERATION_NO, Utils::readInteger,
									null, (Integer i) -> i > 0);
	private final SimulationOptionSetting<Integer> burn_in_no
					= new SimulationOptionSetting<>(BURN_IN_NO, Utils::readInteger,
									null, (i) -> i >= 0);
	private final SimulationOptionSetting<Integer> checkpoint_no
					= new SimulationOptionSetting<>(CHECKPOINT_NO, Utils::readInteger,
									null, (i) -> i >= 0);
	private final SimulationCommands cmds;
	private final SimulationOptionSetting<Boolean> detailed_data
					= new SimulationOptionSetting<>(DETAILED_DATA, Utils::readBoolean);
	private final File directory;
	private final SimulationOptionSetting<Boolean> forced_run
					= new SimulationOptionSetting<>(FORCED_RUN, Utils::readBoolean,
									Boolean.FALSE);
	private final SimulationOptionSetting<SimulationConditions> initial_conditions
					= new SimulationOptionSetting<>(INITIAL_CONDITIONS,
									SimulationConditions::read);

	private final File input;
	private final SimulationOptionSetting<Boolean> new_run
					= new SimulationOptionSetting<>(FORCED_RUN, Utils::readBoolean,
									Boolean.TRUE);
	private final SimulationOptionSetting<Integer> pheromone_no
					= new SimulationOptionSetting<>(PHEROMONE_NO, Utils::readInteger,
									null, (i) -> i >= 0);
	private final SimulationOptionSetting<Integer> record_freq_no
					= new SimulationOptionSetting<>(RECORD_FREQ_NO, Utils::readInteger,
									1, (i) -> i > 0);
	private final SimulationOptionSetting<Integer> record_no
					= new SimulationOptionSetting<>(RECORD_NO, Utils::readInteger,
									null, (i) -> i > 0);
	private final SimulationOptionSetting<Long> seed
					= new SimulationOptionSetting<>(SEED, Utils::readLong,
									System.currentTimeMillis());
	private final HashMap<String, SimulationOptionSetting> settings
					= new HashMap<String, SimulationOptionSetting>() {
		private static final long serialVersionUID = 1L;

		{
			put(ANIMAL_ZOO, animal_zoo);
			put(ASSAY_ITERATION_NO, assay_iteration_no);
			put(BURN_IN_NO, burn_in_no);
			put(CHECKPOINT_NO, checkpoint_no);
			put(DETAILED_DATA, detailed_data);
			put(FORCED_RUN, forced_run);
			put(INITIAL_CONDITIONS, initial_conditions);
			put(FORCED_RUN, forced_run);
			put(PHEROMONE_NO, pheromone_no);
			put(RECORD_FREQ_NO, record_freq_no);
			put(RECORD_NO, record_no);
			put(SEED, seed);
			put(THREAD_NO, thread_no);
			put(TIMEOUT, timeout);
			put(WALKER_NO, walker_no);
		}
	};
	private final SimulationOptionSetting<Integer> thread_no
					= new SimulationOptionSetting<>(THREAD_NO, Utils::readInteger,
									Runtime.getRuntime().availableProcessors(), (i) -> i > 0);
	private final SimulationOptionSetting<Long> timeout
					= new SimulationOptionSetting<>(TIMEOUT, Utils::readLong,
									null, (i) -> i >= 0);
	private final SimulationOptionSetting<Integer> walker_no
					= new SimulationOptionSetting<>(WALKER_NO, Utils::readInteger,
									null, (i) -> i > 0);

	public void readData2()
					throws IOException {
		if (input == null) {
			throw new NullPointerException("Provided input file is a null pointer!");
		} else if (!input.exists()) {
			throw new FileNotFoundException(
							"There is no \"input.txt\" to read from!");
		}

		try (Scanner in = new Scanner(input)) {
			// Detects the names, but has to be trimmed.

		}
	}

	private static final Pattern ARGUMENT_PATTERN = Pattern.compile(
					"((?<=^)|(?<=\\v))[^#\\v]*?=([^\\{]*?\\v|[^\\{]*\\{[\\s\\S]*?\\v\\})");

	/**
	 * Outputs the data of this object in the "input.txt" file format.
	 *
	 * @param out
	 *
	 * @throws IOException
	 */
	public void write(BufferedWriter out)
					throws IOException {
		for (SimulationOptionSetting setting : settings.values()) {
			out.write(setting.getName());
			out.write(" = ");
			out.write(setting.get().toString());
		}
	}
}
