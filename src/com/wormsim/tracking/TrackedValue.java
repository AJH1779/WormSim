/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wormsim.tracking;

import java.io.Serializable;
import org.apache.commons.math3.random.RandomGenerator;

/**
 * A value that may be tracked and recorded or optimised.
 *
 * @author ah810
 * @version 0.0.1
 */
public interface TrackedValue extends Serializable {

	/**
	 * Initialises the value using the provided random number generator.
	 *
	 * @param rng The random number generator to use.
	 */
	public void initialise(RandomGenerator rng);

	/**
	 * Provides an independent copy of this tracked value.
	 *
	 * @return An independent copy.
	 */
	public TrackedValue copy();

	/**
	 * Rejects the current value and sets the current value to the previously
	 * retained value or the initial value, whichever was most recent.
	 */
	public void revert();

	/**
	 * Retains the current value such that subsequent calls to
	 * <code>retain()</code> will return the current value.
	 */
	public void retain();

	/**
	 * Alters the value in a controlled manner.
	 *
	 * @param rng The random number generator.
	 */
	public void evolve(RandomGenerator rng);

}
