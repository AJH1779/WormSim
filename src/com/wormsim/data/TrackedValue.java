/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wormsim.data;

import java.io.Serializable;
import org.apache.commons.math3.random.RandomGenerator;

/**
 * A value that may be tracked and recorded or optimised.
 *
 * @author ah810
 * @version 0.0.1
 */
public interface TrackedValue extends Serializable {

	public void initialise(RandomGenerator rng);

	/**
	 * Removes this instance as being used for calculations, so this can be used
	 * simply as a way of monitoring the value.
	 *
	 * @return
	 */
	public boolean stopAffectingVariance();

	/**
	 * Returns the current value of the tracked value as a string.
	 *
	 * @return
	 */
	public String toCurrentValueString();

	/**
	 * Returns the headers of the tracked value as a string.
	 *
	 * @return
	 */
	public String toHeaderString();

	/**
	 * Returns the between walker variances of the tracked value as a string.
	 *
	 * @return
	 */
	public String toBetweenVarianceString();

	/**
	 * Returns the within walker variances of the tracked value as a string.
	 *
	 * @return
	 */
	public String toWithinVarianceString();

	/**
	 * Returns the overall variances of the tracked value as a string.
	 *
	 * @return
	 */
	public String toVarianceString();

	/**
	 * Returns the potential scale reductions of the tracked value as a string.
	 *
	 * @return
	 */
	public String toPotentialScaleReductionString();

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
