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
		SimulationCommands cmds = Utils.readCommandLine(args);
		SimulationOptions ops = new SimulationOptions(cmds);
		try {
			ops.readInput();
			if (ops.isMissingParameters()) {
				String msg = "Missing parameters: " + ops.getMissingParametersList();
				LOG.log(Level.SEVERE, msg);
				throw new IOException(msg);
			}

			//new Simulation(ops).run();
			throw new UnsupportedOperationException("Not Yet Implemented");
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

			System.exit(-1);
		}
		// TODO: Logger should be better used here or not used at all.
		// TODO: What is the problem and how is it fixed?
		// TODO: Exit Error Codes?

	}

}
