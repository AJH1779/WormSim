/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wormsim.animals;

import com.wormsim.simulation.SimulationThread;
import org.apache.commons.math3.random.RandomGenerator;

/**
 * An interface which denotes a sampling function for calculating some integer
 * value based on the number of members of the group developing, the current
 * state of the system, and using the provided random number generator.
 *
 * @author ah810
 * @version 0.0.1
 */
public interface DevelopmentFunction {
	/**
	 * Returns a development function which is represented by the specified
	 * string. Note this may be a bit challenging.
	 *
	 * @param arg The string representation of the function.
	 *
	 * @return The function.
	 */
	public static DevelopmentFunction interpret(String arg) {
		// TODO: interpret development function.
		throw new UnsupportedOperationException("NOT YET IMPLEMENTED");
	}

	/**
	 * Applies this function to the given arguments.
	 *
	 * @param iface The sampling interface
	 * @param count The number of animals
	 * @param rng   The random number generator
	 *
	 * @return The resulting integer
	 */
	public abstract int applyAsInt(SimulationThread.SamplingInterface iface,
																 int count,
																 RandomGenerator rng);
}
