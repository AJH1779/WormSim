/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wormsim.data;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

/**
 * An object which represents the collection of simulation options obtained from
 * the command line. These options should not include those which would alter
 * the outcome of the simulations.
 *
 * @author ah810
 * @version 0.0.1
 */
public class SimulationCommands {
	private static final Logger LOG = Logger.getLogger(SimulationCommands.class
					.getName());

	/**
	 * Creates a new set of simulation commands from the provided map of commands.
	 * The format is command for the key including the "-" which denotes it as a
	 * command followed by a List of whitespace separated arguments.
	 *
	 * TODO: Replace Simulation Commands with a SimulationOptions2 style system?
	 *
	 * @param cmds The input commands as a map.
	 *
	 * @throws IllegalArgumentException If a command or argument was invalid
	 */
	public SimulationCommands(HashMap<String, List<String>> cmds)
					throws IllegalArgumentException {
		cmds.forEach((k, e) -> {
			switch (k) {
				case "--threads":
				case "-t":
					if (e.size() == 1) {
						try {
							thread_no = Integer.valueOf(e.get(0));
							if (thread_no <= 0) {
								throw new IllegalArgumentException(
												"Must provide a positive integer value for thread "
												+ "number, provided \"" + e.get(0) + "\".");
							}
						} catch (NumberFormatException ex) {
							throw new IllegalArgumentException(
											"Must provide a positive integer value for thread "
											+ "number, provided \"" + e.get(0) + "\".");
						}
					} else {
						throw new IllegalArgumentException(
										"Must provide one argument for number of threads, instead "
										+ "provided " + e.size() + " arguments.");
					}
					break;
				case "--newrun":
				case "-n":
					if (e.size() == 1) {
						String s = e.get(0);
						if ("true".equalsIgnoreCase(s) || "t".equalsIgnoreCase(s) || "1"
										.equalsIgnoreCase(s)) {
							newrun = 1;
						} else if ("false".equalsIgnoreCase(s) || "f".equalsIgnoreCase(s)
										|| "0".equalsIgnoreCase(s)) {
							newrun = 0;
						} else {
							throw new IllegalArgumentException(
											"Must provide a boolean value for newrun flag (either "
											+ "true/t/1 or false/1/0, case insensitive), "
											+ "provided \"" + e.get(0) + "\".");
						}
					} else {
						throw new IllegalArgumentException(
										"Must provide one argument for newrun flag, instead "
										+ "provided " + e.size() + " arguments.");
					}
					break;
				case "--timeout":
					if (e.size() == 1) {
						try {
							timer = Long.valueOf(e.get(0));
							if (timer <= 0L) {
								throw new IllegalArgumentException(
												"Must provide a positive long value for the timeout, "
												+ "provided \"" + e.get(0) + "\".");
							}
						} catch (NumberFormatException ex) {
							throw new IllegalArgumentException(
											"Must provide a positive long value for the timeout, "
											+ "provided \"" + e.get(0) + "\".", ex);
						}
					} else {
						throw new IllegalArgumentException(
										"Must provide one argument for the timeout, instead "
										+ "provided " + e.size() + " arguments.");
					}
					break;
				case "--dir":
				case "-d":
					if (e.size() == 1) {
						File f = new File(e.get(0));
						if (f.exists() && f.isDirectory()) {
							directory = f;
						} else {
							throw new IllegalArgumentException(
											"Must provide an existing directory for the working "
											+ "directory, provided \"" + e.get(0) + "\" which led to "
											+ f.getAbsolutePath() + ".");
						}
					} else {
						throw new IllegalArgumentException(
										"Must provide one argument for the working directory "
										+ "(enclose in quotes if the directory has spaces), "
										+ "instead provided " + e.size() + " arguments.");
					}
					break;
				default:
					throw new IllegalArgumentException("Unrecognised Argument: " + k);
			}
		});
	}
	private File directory = new File(".");
	private int newrun = -1; // Not selected.
	private int thread_no = Integer.MAX_VALUE;
	private long timer = -1; // Not selected.

	/**
	 * Returns the target working directory of the program.
	 *
	 * @return
	 */
	public File getDirectory() {
		return directory;
	}

	/**
	 * Returns an integer for the newrun flag. -1 indicates no change to the
	 * "input.txt" setting, 0 indicates newrun to be false, and 1 indicates newrun
	 * to be true.
	 *
	 * @return
	 */
	public int getNewRun() {
		return newrun;
	}

	/**
	 * Returns the number of threads requested to run on.
	 *
	 * @return
	 */
	public int getThreadNumber() {
		return thread_no;
	}

	/**
	 * Returns a long for the time until taking a compulsory checkpoint and
	 * terminating the program. Returning -1 results in no timeout.
	 *
	 * @return
	 */
	public long getTimeout() {
		return timer;
	}
}
