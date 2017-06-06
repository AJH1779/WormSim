/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wormsim.animals;

import com.wormsim.simulation.SimulationThread;

/**
 * Represents a function that accepts a scoring interface and count denoting the
 * number of animals being scored.
 *
 * @author ah810
 * @version 0.0.1
 */
@FunctionalInterface
public interface ScoringFunction {
	/**
	 * Returns a scoring function which is represented by the specified string.
	 * Note this may be a bit challenging.
	 *
	 * @param arg The string representation of the function.
	 *
	 * @return The function.
	 */
	public static ScoringFunction interpret(String arg) {
		// TODO: interpret scoring function.
		throw new UnsupportedOperationException("NOT YET IMPLEMENTED");
	}

	/**
	 * Applies this function to the given arguments.
	 *
	 * @param iface The sampling interface
	 * @param count The number of animals
	 */
	public void applyAsDouble(SimulationThread.SamplingInterface iface, int count);
}
