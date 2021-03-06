/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wormsim;

import com.wormsim.data.SimulationCommands;
import com.wormsim.data.SimulationOptions;
import com.wormsim.simulation.Simulation;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * This is the class from which the program should be run.
 *
 * @author ah810
 * @version 0.0.2
 *
 * @see Main#main(java.lang.String[]) For the details of how to launch this
 * program.
 */
public class Main {
	/**
	 * Cited authors should be added here.
	 */
	private static final String[] AUTHOR_ARRAY = new String[]{
		"Arthur Hills",
		"Mark Viney",
		"Simon Harvey"
	};

	private static final Logger LOG = Logger.getLogger(Main.class.getName());

	/**
	 * An immutable list of the authors of the program and theory or stuff.
	 */
	public static final List<String> AUTHORS = Collections.unmodifiableList(Arrays
					.asList(AUTHOR_ARRAY));
	/**
	 * A comma delimited list of the authors.
	 */
	public static final String AUTHORS_AS_STRING = String.join(", ", AUTHOR_ARRAY);
	/**
	 * The literature reference for use of this program.
	 *
	 * TODO: Apply a reference when it is available.
	 */
	public static final String REFERENCE = "Null";
	/**
	 * The version code of this program.
	 */
	public static final String VERSION = "0.0.2";

	/**
	 * Outputs the help dialogue to the command line which outlines all of the
	 * commands, their arguments, and what they do. This is an output of the
	 * program header followed by the help text defined in header.txt and
	 * help.txt respectively.
	 *
	 * @see header.txt for the header text.
	 * @see help.txt for the full help text.
	 */
	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public static void help() {
		System.out.println(new Scanner(Main.class.getResourceAsStream(
						"/com/wormsim/header.txt")).useDelimiter("\\Z").next()
						.replace("{authors}", AUTHORS_AS_STRING)
						.replace("{version}", VERSION)
						.replace("{reference}", REFERENCE));
		System.out.println(new Scanner(Main.class.getResourceAsStream(
						"/com/wormsim/help.txt")).useDelimiter("\\Z").next()
						.replace("{out.txt}", Simulation.OUT_TXT));
	}

	/**
	 * Called when the program is launched and processes through the command line
	 * arguments. The program stops running if one of the argument commands are
	 * for the help text.
	 * 
	 * @param args the command line arguments
	 *
	 * @throws java.io.IOException When there is a problem reading one of the
	 *                             input files.
	 *
	 * @see help.txt For the commands that are accepted on the command line.
	 */
	public static void main(String[] args)
					throws IllegalArgumentException,
								 IOException {
		// Convert the arguments into command lists
		HashMap<String, List<String>> data = new HashMap<>(args.length);
		String current_cmd = null;
		for (String arg : args) {
			if (arg.startsWith("-")) {
				current_cmd = arg;
				if (data.putIfAbsent(arg, new ArrayList<>(2)) != null) {
					throw new IllegalArgumentException("Repeated Argument: " + arg);
				}
			} else if (current_cmd != null) {
				data.get(current_cmd).add(arg);
			} else {
				throw new IllegalArgumentException("First Parameter must be Argument: "
								+ arg);
			}
		}
		// Check if any of the commands are something to act upon right now, like help.
		if (data.containsKey("-h") || data.containsKey("--help")) {
			// Print out the help and then terminate, although the other arguments should
			// also be checked to see if they are relevant.
			help();
			System.exit(0);
		}
		System.exit(-1);
		SimulationOptions ops = new SimulationOptions(new SimulationCommands(data));
		new Simulation(ops).run();
	}
}
