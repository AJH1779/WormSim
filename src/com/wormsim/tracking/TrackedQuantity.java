/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wormsim.tracking;

import java.util.ArrayList;
import java.util.stream.Stream;
import org.apache.commons.math3.random.RandomGenerator;

/**
 *
 * @author ah810
 * @param <V> The class of the quantity being tracked
 */
public abstract class TrackedQuantity<V extends TrackedQuantityInstance> {

	/**
	 * Creates a new tracked quantity, ready for tracking.
	 */
	public TrackedQuantity() {
		this.all_related = new ArrayList<>();
	}
	final ArrayList<V> all_related;

	/**
	 * Called every checkpoint to ensure all the instances consolidate the data.
	 */
	public abstract void checkpoint();

	public Stream<V> dependents() {
		return all_related.stream();
	}

	/**
	 * Called every checkpoint to ensure all the instances consolidate the data.
	 */
	public abstract void discard();

	/**
	 * Creates a new tracked quantity instance that is linked to this.
	 *
	 * @param rng The random number generator to use to create the quantity.
	 *
	 * @return
	 */
	public abstract V generate(RandomGenerator rng);

	/**
	 * Returns true if this is to be tracked visibly, false otherwise.
	 *
	 * @return
	 */
	public boolean isVisiblyTracked() {
		return true;
	}

	/**
	 * Registers the provided tracked quantity to this. Returns false if it was
	 * already registered.
	 *
	 * @param inst The quantity instance
	 *
	 * @return If the provided quantity was registered, false otherwise
	 */
	public boolean register(V inst) {
		if (!all_related.contains(inst)) {
			return this.all_related.add(inst);
		} else {
			return false;
		}
	}

	/**
	 * Returns a string of the between variances of this quantity.
	 *
	 * @return
	 */
	public abstract String toBetweenVarianceString();

	/**
	 * Returns a string of the between variances of the quantities within this
	 * quantity.
	 *
	 * @return
	 */
	public abstract String toEffectiveDataCountString();

	/**
	 * Returns a string of the headers of the quantities within this quantity.
	 *
	 * @return
	 */
	public abstract String toHeaderString();

	public abstract String toMeanString();

	/**
	 * Returns a string of the potential scale reduction factor of the quantities
	 * within this quantity.
	 *
	 * @return
	 */
	public abstract String toPotentialScaleReductionString();

	/**
	 * Returns a string of the between variances of this quantity.
	 *
	 * @return
	 */
	public abstract String toRecentBetweenVarianceString();

	/**
	 * Returns a string of the between variances of the quantities within this
	 * quantity.
	 *
	 * @return
	 */
	public abstract String toRecentEffectiveDataCountString();

	public abstract String toRecentMeanString();

	/**
	 * Returns a string of the potential scale reduction factor of the quantities
	 * within this quantity.
	 *
	 * @return
	 */
	public abstract String toRecentPotentialScaleReductionString();

	/**
	 * Returns a string of the estimated variances of the quantities within this
	 * quantity.
	 *
	 * @return
	 */
	public abstract String toRecentVarianceString();

	/**
	 * Returns a string of the within variances of the quantities within this
	 * quantity.
	 *
	 * @return
	 */
	public abstract String toRecentWithinVarianceString();

	/**
	 * Returns a string of the estimated variances of the quantities within this
	 * quantity.
	 *
	 * @return
	 */
	public abstract String toVarianceString();

	/**
	 * Returns a string of the within variances of the quantities within this
	 * quantity.
	 *
	 * @return
	 */
	public abstract String toWithinVarianceString();

	/**
	 * Returns a stream of all children tracked quantity instances.
	 *
	 * @return
	 */
	protected Stream<V> stream() {
		return all_related.stream();
	}
}
