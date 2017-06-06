/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wormsim.tracking;

import org.apache.commons.math3.random.RandomGenerator;

/**
 * Denotes a collection of values being tracked across walkers.
 *
 * @author ah810
 * @param <V>
 */
public abstract class TrackedDouble<V extends TrackedDoubleInstance>
				extends TrackedQuantity<V> {

	/**
	 * Creates a new tracked quantity using the provided size for the checkpoints.
	 *
	 * TODO: Transfer to a different type of checkpoint.
	 *
	 * @param name The name of this tracked quantity
	 */
	public TrackedDouble(String name) {
		this.name = name;
	}
	final String name;

	@Override
	public abstract V generate(RandomGenerator p_rng);

	/**
	 * Returns the mean of all data collected for this tracked double.
	 *
	 * @return
	 */
	public abstract double getMean();

	/**
	 * Returns an estimate of the variance of this class of related tracked
	 * values.
	 *
	 * @return The Between Variance.
	 */
	public abstract double getBetweenVariance();

	/**
	 * Returns the number of data points expressed so far.
	 *
	 * @return
	 */
	public abstract double getDataCount();

	/**
	 * Returns the effective weighting of the data being given.
	 *
	 * @return
	 */
	public abstract double getEffectiveDataCount();

	/**
	 * Returns the potential scale reduction factor of the tracked quantity using
	 * checkpoint data.
	 *
	 * @return
	 */
	public abstract double getPotentialScaleReduction();

	/**
	 * Returns the estimated variance of the tracked quantity using checkpoint
	 * data.
	 *
	 * @return
	 */
	public abstract double getVariance();

	/**
	 * Returns an estimate of the variance of this class of related tracked
	 * values.
	 *
	 * @return The Within Variance.
	 */
	public abstract double getWithinVariance();

	/**
	 * Returns the mean of all data collected for this tracked double.
	 *
	 * @return
	 */
	public double getRecentMean() {
		return getMean();
	}

	/**
	 * Returns an estimate of the variance of this class of related tracked
	 * values.
	 *
	 * @return The Between Variance.
	 */
	public double getRecentBetweenVariance() {
		return getBetweenVariance();
	}

	/**
	 * Returns the number of data points expressed so far.
	 *
	 * @return
	 */
	public double getRecentDataCount() {
		return getDataCount();
	}

	/**
	 * Returns the effective weighting of the data being given.
	 *
	 * @return
	 */
	public double getRecentEffectiveDataCount() {
		return getEffectiveDataCount();
	}

	/**
	 * Returns the potential scale reduction factor of the tracked quantity using
	 * checkpoint data.
	 *
	 * @return
	 */
	public double getRecentPotentialScaleReduction() {
		return getPotentialScaleReduction();
	}

	/**
	 * Returns the estimated variance of the tracked quantity using checkpoint
	 * data.
	 *
	 * @return
	 */
	public double getRecentVariance() {
		return getVariance();
	}

	/**
	 * Returns an estimate of the variance of this class of related tracked
	 * values.
	 *
	 * @return The Within Variance.
	 */
	public double getRecentWithinVariance() {
		return getWithinVariance();
	}

	@Override
	public String toBetweenVarianceString() {
		return Double.toString(this.getBetweenVariance());
	}

	@Override
	public String toEffectiveDataCountString() {
		return Double.toString(this.getEffectiveDataCount());
	}

	@Override
	public String toHeaderString() {
		return name;
	}

	@Override
	public String toPotentialScaleReductionString() {
		return Double.toString(this.getPotentialScaleReduction());
	}

	@Override
	public String toVarianceString() {
		return Double.toString(this.getVariance());
	}

	@Override
	public String toWithinVarianceString() {
		return Double.toString(this.getWithinVariance());
	}

	@Override
	public String toMeanString() {
		return Double.toString(this.getMean());
	}

	@Override
	public String toRecentBetweenVarianceString() {
		return Double.toString(this.getRecentBetweenVariance());
	}

	@Override
	public String toRecentEffectiveDataCountString() {
		return Double.toString(this.getRecentEffectiveDataCount());
	}

	@Override
	public String toRecentPotentialScaleReductionString() {
		return Double.toString(this.getRecentPotentialScaleReduction());
	}

	@Override
	public String toRecentVarianceString() {
		return Double.toString(this.getRecentVariance());
	}

	@Override
	public String toRecentWithinVarianceString() {
		return Double.toString(this.getRecentWithinVariance());
	}

	@Override
	public String toRecentMeanString() {
		return Double.toString(this.getRecentMean());
	}

	/**
	 * Returns a value evolving from the provided value using the random generator
	 * specified. This is the step function for this tracked quantity.
	 *
	 * @param val The value to shift
	 * @param rng The random generator to use.
	 *
	 * @return The stepped value.
	 */
	protected abstract double evolve(double val, RandomGenerator rng);

	/**
	 * Returns an initialising value for the tracked quantities.
	 *
	 * @param rng The random number generator to use.
	 *
	 * @return The result.
	 */
	protected abstract double initialise(RandomGenerator rng);
}
