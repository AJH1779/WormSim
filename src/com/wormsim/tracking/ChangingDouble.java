/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wormsim.tracking;

import com.wormsim.data.SimulationOptions;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.util.FastMath;

/**
 *
 * @author ah810
 */
public abstract class ChangingDouble extends TrackedDouble<ChangingDoubleInstance> {

	/**
	 * Creates a new tracked quantity using the provided size for the checkpoints.
	 *
	 * TODO: Transfer to a different type of checkpoint.
	 *
	 * @param name The name of this tracked quantity
	 * @param ops  The options that the checkpoint size will be taken from.
	 */
	public ChangingDouble(String name, SimulationOptions ops) {
		super(name);
		this.ops = ops;
	}
	@SuppressWarnings("PackageVisibleField")
	protected int checkpoint_count = 0;
	protected int checkpoint_size = -1;
	protected int iteration_count = -1;
	protected final SimulationOptions ops;

	/**
	 * Called every checkpoint to ensure all the instances consolidate the data.
	 */
	@Override
	public final void checkpoint() {
		checkpoint_count++;
		all_related.stream().forEach((f) -> f.checkpoint());
	}

	/**
	 * Called every checkpoint to ensure all the instances consolidate the data.
	 */
	@Override
	public final void discard() {
		all_related.stream().forEach((f) -> f.discard());
	}

	/**
	 * Creates a new tracked quantity instance that is linked to this.
	 *
	 * @param rng The random number generator to use to create the quantity.
	 *
	 * @return
	 */
	@Override
	public ChangingDoubleInstance generate(RandomGenerator rng) {
		return generate(initialise(rng));
	}

	/**
	 * Creates a new tracked quantity instance that is linked to this.
	 *
	 * @param init_value The initial value for the instanced value.
	 *
	 * @return
	 */
	public ChangingDoubleInstance generate(double init_value) {
		assert checkpoint_count == 0;
		init();
		ChangingDoubleInstance inst = new ChangingDoubleInstance(this, init_value);
		// this.all_related.add(inst);
		return inst;
	}

	/**
	 * Returns an estimate of the variance of this class of related tracked
	 * values.
	 *
	 * @return The Between Variance.
	 */
	@Override
	public final double getBetweenVariance() {
		double psi__ = all_related.stream().mapToDouble((p) -> p.getMean()
						/ all_related.size()).sum();
		return all_related.stream().mapToDouble((p) -> FastMath.pow(p.getMean()
						- psi__, 2)).average().getAsDouble() * checkpoint_count
						* checkpoint_size;
	}

	/**
	 * Returns the number of data points expressed so far.
	 *
	 * @return
	 */
	@Override
	public final double getDataCount() {
		return checkpoint_count * checkpoint_size;
	}

	/**
	 * Returns the effective weighting of the data being given.
	 *
	 * @return
	 */
	@Override
	public final double getEffectiveDataCount() {
		double n = checkpoint_count * checkpoint_size;
		double b = getBetweenVariance();
		double w = getWithinVariance();
		// TODO: Ignore sqrt.
		return all_related.size() * (((n - 1.0) * w) / b + 1.0);
	}

	@Override
	public final double getMean() {
		return all_related.stream().mapToDouble((p) -> p.getMean()).average()
						.getAsDouble();
	}

	/**
	 * Returns the potential scale reduction factor of the tracked quantity using
	 * checkpoint data.
	 *
	 * @return
	 */
	@Override
	public final double getPotentialScaleReduction() {
		double n = checkpoint_count * checkpoint_size;
		double b = getBetweenVariance();
		double w = getWithinVariance();
		// TODO: Ignore sqrt.
		return FastMath.sqrt((n - 1.0) / n + b / (n * w));
	}

	/**
	 * Returns an estimate of the variance of this class of related tracked
	 * values.
	 *
	 * @return The Between Variance.
	 */
	@Override
	public final double getRecentBetweenVariance() {
		double psi__ = all_related.stream().mapToDouble((p) -> p.getRecentMean()
						/ all_related.size()).sum();
		return all_related.stream().mapToDouble((p) -> FastMath.pow(p
						.getRecentMean()
						- psi__, 2)).average().getAsDouble() * getRecentDataCount();
	}

	/**
	 * Returns the number of data points expressed so far.
	 *
	 * @return
	 */
	@Override
	public final double getRecentDataCount() {
		return all_related.get(0).history.size();
	}

	/**
	 * Returns the effective weighting of the data being given.
	 *
	 * @return
	 */
	@Override
	public final double getRecentEffectiveDataCount() {
		double n = getRecentDataCount();
		double b = getRecentBetweenVariance();
		double w = getRecentWithinVariance();
		// TODO: Ignore sqrt.
		return all_related.size() * (((n - 1.0) * w) / b + 1.0);
	}

	@Override
	public final double getRecentMean() {
		return all_related.stream().mapToDouble((p) -> p.getRecentMean())
						.average().getAsDouble();
	}

	/**
	 * Returns the potential scale reduction factor of the tracked quantity using
	 * checkpoint data.
	 *
	 * @return
	 */
	@Override
	public final double getRecentPotentialScaleReduction() {
		double n = getRecentDataCount();
		double b = getRecentBetweenVariance();
		double w = getRecentWithinVariance();
		// TODO: Ignore sqrt.
		return FastMath.sqrt((n - 1.0) / n + b / (n * w));
	}

	/**
	 * Returns the estimated variance of the tracked quantity using checkpoint
	 * data.
	 *
	 * @return
	 */
	@Override
	public final double getRecentVariance() {
		double n = getRecentDataCount();
		double b = getRecentBetweenVariance();
		double w = getRecentWithinVariance();
		return (w * (n - 1.0)) / n + b / n;
	}

	/**
	 * Returns an estimate of the variance of this class of related tracked
	 * values.
	 *
	 * @return The Within Variance.
	 */
	@Override
	public final double getRecentWithinVariance() {
		return all_related.stream().mapToDouble((arr) -> arr.getRecentVariance())
						.average().getAsDouble();
	}

	/**
	 * Returns the estimated variance of the tracked quantity using checkpoint
	 * data.
	 *
	 * @return
	 */
	@Override
	public final double getVariance() {
		double n = checkpoint_count * checkpoint_size;
		double b = getBetweenVariance();
		double w = getWithinVariance();
		return (w * (n - 1.0)) / n + b / n;
	}

	/**
	 * Returns an estimate of the variance of this class of related tracked
	 * values.
	 *
	 * @return The Within Variance.
	 */
	@Override
	public final double getWithinVariance() {
		return all_related.stream().mapToDouble((arr) -> arr.getVariance())
						.average().getAsDouble();
	}

	public void init() {
		if (this.checkpoint_size == -1) {
			this.checkpoint_size = ops.checkpoint_no.get();
		}
		if (this.iteration_count == -1) {
			this.iteration_count = ops.assay_iteration_no.get();
		}
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
	@Override
	protected abstract double evolve(double val, RandomGenerator rng);

	/**
	 * Returns an initialising value for the tracked quantities.
	 *
	 * @param rng The random number generator to use.
	 *
	 * @return The result.
	 */
	@Override
	protected abstract double initialise(RandomGenerator rng);
}
