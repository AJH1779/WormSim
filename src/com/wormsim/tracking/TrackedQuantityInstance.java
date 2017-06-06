/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wormsim.tracking;

import org.apache.commons.math3.random.RandomGenerator;

/**
 *
 * @author ah810
 */
public interface TrackedQuantityInstance {
	/**
	 * Consolidates the data in memory. Should only be called during the recording
	 * phase, with burn-in checkpoints instead calling {@link #discard()}
	 *
	 * Note that the data should be output to somewhere in general if you care
	 * about it.
	 */
	public void checkpoint();

	/**
	 * Returns a copy of the this quantity instance which should also be tracked
	 * by the parent.
	 *
	 * @return
	 */
	public TrackedQuantityInstance copy();

	/**
	 * Discards the progress of the current checkpoint. This should be called
	 * during the burn-in phase instead of {@link #checkpoint() checkpoint}.
	 */
	public void discard();

	/**
	 * Steps this quantity using the parent definition of evolving.
	 *
	 * @param p_rng The random generator.
	 */
	public void evolve(RandomGenerator p_rng);

	/**
	 * Returns this quantity to an initialising value. If this is not intended as
	 * a wild jump but instead a true reset, then follow this method with a call
	 * to <code>retain()</code>.
	 *
	 * @param p_rng The random generator.
	 */
	public void initialise(RandomGenerator p_rng);

	/**
	 * Sets the previous value to the current value, recording the current value.
	 */
	public void retain();

	/**
	 * Sets the current value back to the previous value, recording that previous
	 * value.
	 */
	public void revert();

	/**
	 * Returns the parent of this tracked quantity.
	 *
	 * @return
	 */
	public TrackedQuantity getParent();

	/**
	 * Returns the current value of this tracked quantity as a string.
	 *
	 * @return The value string
	 */
	public String toCurrentValueString();
}
