/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wormsim.data;

import com.wormsim.LaunchFromCodeMain;
import com.wormsim.Temporary;
import com.wormsim.animals.AnimalZoo2;
import com.wormsim.utils.Utils;
import static com.wormsim.utils.Utils.ASSIGNMENT_PATTERN;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * An object which represents the collection of simulation options combined from
 * the command line and the "input.txt" file.
 *
 * @author ah810
 * @version 0.0.1
 */
public final class SimulationOptions implements Serializable {

	private static final Logger LOG = Logger.getLogger(SimulationOptions.class
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
	public static final String FORCED_RUN = "forced_run";
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
	 * @param p_commands An object representing the command line arguments.
	 *
	 */
	public SimulationOptions(SimulationCommands p_commands) {
		this.settings = new NoReplaceHashMap<>();
		// Initialise the variables for quick access.
		// Additional variables can be created for access through the settings
		// object, if a hack is being employed or something like that.
		this.assay_iteration_no = new SimulationOptionSetting<>(this,
						ASSAY_ITERATION_NO, Utils::readInteger, null, (Integer i) -> i > 0);
		this.burn_in_no = new SimulationOptionSetting<>(this, BURN_IN_NO,
						Utils::readInteger, null, (i) -> i >= 0);
		this.checkpoint_no = new SimulationOptionSetting<>(this, CHECKPOINT_NO,
						Utils::readInteger, null, (i) -> i >= 0);
		this.detailed_data = new SimulationOptionSetting<>(this, DETAILED_DATA,
						Utils::readBoolean);
		this.forced_run = new SimulationOptionSetting<>(this, FORCED_RUN,
						Utils::readBoolean, Boolean.FALSE);
		this.initial_conditions = new SimulationOptionSetting<>(this,
						INITIAL_CONDITIONS, SimulationConditions::read);
		this.new_run = new SimulationOptionSetting<>(this, NEW_RUN,
						Utils::readBoolean, Boolean.TRUE);
		this.pheromone_no = new SimulationOptionSetting<>(this, PHEROMONE_NO,
						Utils::readInteger, null, (i) -> i >= 0);
		this.record_freq_no = new SimulationOptionSetting<>(this, RECORD_FREQ_NO,
						Utils::readInteger, 1, (i) -> i > 0);
		this.record_no = new SimulationOptionSetting<>(this, RECORD_NO,
						Utils::readInteger, null, (i) -> i > 0);
		this.seed = new SimulationOptionSetting<>(this, SEED, Utils::readLong,
						System.currentTimeMillis());
		this.thread_no = new SimulationOptionSetting<>(this, THREAD_NO,
						Utils::readInteger, Runtime.getRuntime().availableProcessors(),
						(i) -> i > 0L);
		this.timeout = new SimulationOptionSetting<>(this, TIMEOUT,
						Utils::readLong, 0L, (i) -> i >= 0L);
		this.walker_no = new SimulationOptionSetting<>(this, WALKER_NO,
						Utils::readInteger, null, (i) -> i > 0);

		this.directory = p_commands.getDirectory();
		this.input = new File(p_commands.getDirectory(), INPUT_TXT);
		this.cmds = p_commands;

		// Has to occur last because of the need to reference checkpoint no.
		this.animal_zoo = new SimulationOptionSetting<>(this, ANIMAL_ZOO,
						AnimalZoo2::read, LaunchFromCodeMain.makeCustomAnimalZoo(this));
	}

	// NOTE: These are made public for the sake of efficiency at the cost of security.
	// The objects themselves should be made secure.
	public final SimulationOptionSetting<AnimalZoo2> animal_zoo;
	public final SimulationOptionSetting<Integer> assay_iteration_no;
	public final SimulationOptionSetting<Integer> burn_in_no;
	public final SimulationOptionSetting<Integer> checkpoint_no;
	public final SimulationCommands cmds;
	public final SimulationOptionSetting<Boolean> detailed_data;
	public final File directory;
	public final SimulationOptionSetting<Boolean> forced_run;
	public final SimulationOptionSetting<SimulationConditions> initial_conditions;
	public final File input;
	public final SimulationOptionSetting<Boolean> new_run;
	public final SimulationOptionSetting<Integer> pheromone_no;
	public final SimulationOptionSetting<Integer> record_freq_no;
	public final SimulationOptionSetting<Integer> record_no;
	public final SimulationOptionSetting<Long> seed;
	public final HashMap<String, SimulationOptionSetting> settings;
	public final SimulationOptionSetting<Integer> thread_no;
	public final SimulationOptionSetting<Long> timeout;
	public final SimulationOptionSetting<Integer> walker_no;

	public String getMissingParametersList() {
		return settings.values().stream()
						.filter((v) -> !v.isFulfilled()).map((v) -> v.getName())
						.collect(Collectors.joining(", "));
	}

	public boolean isMissingParameters() {
		return settings.values().stream().anyMatch((v) -> !v.isFulfilled());
	}

	public void readInput()
					throws IOException {
		assert input != null;
		if (!input.exists()) {
			throw new FileNotFoundException(
							"There is no \"input.txt\" to read from!");
		}

		try (Scanner s = new Scanner(input)) {
			String str;
			while ((str = s.findWithinHorizon(ASSIGNMENT_PATTERN, 0)) != null) {
				// Should return "key = value"
				String[] keyvalue = str.split("=", 2);
				SimulationOptionSetting get = settings.get(keyvalue[0].trim());
				if (get == null) {
					throw new IOException("Invalid Key Name: \"" + keyvalue[0].trim()
									+ "\"");
				}
				get.setFromString(keyvalue[1].trim());
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		settings.values().forEach((setting) -> {
			b.append(setting.getName()).append(" = ").append(setting.get())
							.append(System.lineSeparator());
		});
		return b.toString();
	}

	/**
	 * TODO: Ensure the entries disallow changing the values inside!
	 *
	 * @param <K> The key type
	 * @param <V> The value type
	 */
	private static class NoReplaceHashMap<K, V> extends HashMap<K, V> {
		private static final long serialVersionUID = 1L;

		@Override
		public Object clone() {
			return super.clone(); //To change body of generated methods, choose Tools | Templates.
		}

		@Override
		public V put(K k, V v) {
			throw new UnsupportedOperationException("Use putIfAbsent instead!");
		}
	}
}
