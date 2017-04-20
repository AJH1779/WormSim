/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wormsim;

import com.wormsim.data.SimulationCommands;
import com.wormsim.data.SimulationOptions;
import com.wormsim.simulation.Simulation;
import com.wormsim.utils.Utils;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is the class from which the program should be run.
 *
 * @author ah810
 * @version 0.0.3
 *
 * @see LaunchFromFileMain#main(java.lang.String[]) For the details of how to
 * launch this program.
 */
public class LaunchFromFileMain {

	private static final Logger LOG = Logger.getLogger(LaunchFromFileMain.class
					.getName());

	/**
	 * Outputs the help dialogue to the command line which outlines all of the
	 * commands, their arguments, and what they do. This is an output of the
	 * program header followed by the help text defined in header.txt and help.txt
	 * respectively.
	 *
	 * @see header.txt for the header text.
	 * @see help.txt for the full help text.
	 *
	 * @since 0.0.1
	 */
	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public static void help() {
		// TODO: Move these scanner outputs to a more consistent environment.
		// TODO: Move the files to their own package.
		System.out.println(new Scanner(LaunchFromFileMain.class.getResourceAsStream(
						"/com/wormsim/header.txt")).useDelimiter("\\Z").next()
						.replace("{authors}", Utils.AUTHORS_AS_STRING)
						.replace("{version}", Utils.VERSION)
						.replace("{reference}", Utils.REFERENCE));
		System.out.println(new Scanner(LaunchFromFileMain.class.getResourceAsStream(
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
	 * @see help.txt For the commands that are accepted on the command line.
	 *
	 * @since 0.0.1
	 */
	public static void main(String[] args)
					throws IllegalArgumentException {
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
		// WARNING: Hard Coded Parameters.
		if (data.containsKey("-h") || data.containsKey("--help")) {
			// Print out the help and then terminate, although the other arguments should
			// also be checked to see if they are relevant.
			help();
			System.exit(0); // Generally not recommended, but should be fine here.
			// TODO: Detailed information as per argument for help?
		}
		SimulationCommands cmds = new SimulationCommands(data);

		try {
			// TODO: Switch to SimulationOptions2.
			SimulationOptions ops = new SimulationOptions(cmds);
			new Simulation(ops).run();
		} catch (FileNotFoundException ex) {
			// TODO: An output for the command line that isn't scary looking, it
			// should just be informative.
			LOG.log(Level.SEVERE, "No \"input.txt\" file found!");
			File file = new File(cmds.getDirectory(), "input.txt");
			if (file.exists()) {
				// An impossible situation in theory.
				throw new AssertionError("Couldn't create file as it already exists!",
								ex);
			} else {
				// A sloppy write out.
				// TODO: Replace with a Files.copy or some similar control.
				try (InputStream in = LaunchFromFileMain.class.getResourceAsStream(
								"/com/wormsim/default_input.txt");
								OutputStream out = new BufferedOutputStream(
												new FileOutputStream(file))) {
					byte[] buffer = new byte[4096];
					while (in.read(buffer) > 0) {
						out.write(buffer);
					}
					LOG.log(Level.SEVERE, "Created input.txt.");
				} catch (IOException ex2) {
					LOG.log(Level.SEVERE, "Couldn't create file.", ex2);
				}
			}
		} catch (IOException ex) {
			// TODO: Logger should be better used here or not used at all.
			// TODO: What is the problem and how is it fixed?
			LOG.log(Level.SEVERE, null, ex);
			// TODO: Exit Error Codes?
			System.exit(-1);
		}
	}
}
