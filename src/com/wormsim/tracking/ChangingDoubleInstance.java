/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wormsim.tracking;

import gnu.trove.iterator.TDoubleIterator;
import gnu.trove.list.array.TDoubleArrayList;
import java.util.logging.Logger;
import java.util.stream.DoubleStream;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.util.FastMath;

/**
 *
 * @author ah810
 */
public class ChangingDoubleInstance extends TrackedDoubleInstance {
	private static final Logger LOG = Logger.getLogger(
					ChangingDoubleInstance.class.getName());

	/**
	 * Should only be called by a tracked quantity.
	 *
	 * @param parent     The TrackedQuantity parent
	 * @param init_value The initial value
	 */
	protected ChangingDoubleInstance(ChangingDouble parent, double init_value) {
		super(parent);
		this.parent = parent;
		this.history = new TDoubleArrayList();
		this.mean_history = new TDoubleArrayList();
		this.sqr_mean_history = new TDoubleArrayList();

		this.current_value = init_value;
		this.previous_value = init_value;
	}
	protected double current_value;
	final TDoubleArrayList history;
	final TDoubleArrayList mean_history;
	final ChangingDouble parent;
	protected double previous_value;
	final TDoubleArrayList sqr_mean_history;

	/**
	 * Consolidates the data in memory. Should only be called during the recording
	 * phase, with burn-in checkpoints instead calling {@link #discard()}
	 *
	 * Note that the data should be output to somewhere in general if you care
	 * about it.
	 */
	@Override
	public void checkpoint() {
		assert history.size() == parent.checkpoint_size;
		this.mean_history.add(getRecentMean());
		this.sqr_mean_history.add(getRecentSquareMean());
		this.history.clear();
	}

	@Override
	public ChangingDoubleInstance copy() {
		return parent.generate(current_value);
	}

	/**
	 * Discards the progress of the current checkpoint. This should be called
	 * during the burn-in phase instead of {@link #checkpoint() checkpoint}.
	 */
	@Override
	public void discard() {
		this.history.clear();
	}

	/**
	 * Steps this quantity using the parent definition of evolving.
	 *
	 * @param p_rng The random generator.
	 */
	@Override
	public void evolve(RandomGenerator p_rng) {
		this.current_value = parent.evolve(this.current_value, p_rng);
	}

	/**
	 * Returns the current value of this instance.
	 *
	 * @return
	 */
	@Override
	public final double get() {
		return current_value;
	}

	/**
	 * Returns the mean of this tracked quantity instance. Note that this is the
	 * mean of the mean of all checkpoints.
	 *
	 * Does not include the most recent data points in the calculation if they
	 * have not yet reached a checkpoint in size.
	 *
	 * @return The checkpoint mean
	 */
	@Override
	public final double getMean() {
		return mean_history.sum() / mean_history.size();
	}

	/**
	 * Returns the mean of all the data, including the most recent and
	 * checkpoints.
	 *
	 * @return The overall mean
	 */
	@Override
	public final double getOverallMean() {
		double recent_weighting = history.size() / ((double) parent.checkpoint_count
						* parent.checkpoint_size + history.size());
		double old_weighting = 1.0 - recent_weighting;
		return getMean() * old_weighting + getRecentMean() * recent_weighting;
	}

	/**
	 * Returns the mean of the squares of all the data, including the most recent
	 * and checkpoints.
	 *
	 * @return The overall mean
	 */
	@Override
	public final double getOverallSquareMean() {
		double recent_weighting = history.size() / ((double) parent.checkpoint_count
						* parent.checkpoint_size + history.size());
		double old_weighting = 1.0 - recent_weighting;
		return getSquareMean() * old_weighting + getRecentSquareMean()
						* recent_weighting;
	}

	/**
	 * Returns the variance of all the data, including the most recent and
	 * checkpoints.
	 *
	 * @return The overall variance
	 */
	@Override
	public final double getOverallVariance() {
		// TODO: WRONG
		double recent_weighting = history.size() / ((double) parent.checkpoint_count
						* parent.checkpoint_size + history.size());
		double old_weighting = 1.0 - recent_weighting;
		return getVariance() * old_weighting + getRecentVariance()
						* recent_weighting;
	}

	/**
	 * Returns the TrackedQuantity that this is a child of.
	 *
	 * @return
	 */
	@Override
	public final TrackedDouble getParent() {
		return parent;
	}

	/**
	 * Returns the previous value of this instance.
	 *
	 * @return
	 */
	@Override
	public final double getPrevious() {
		return this.previous_value;
	}

	/**
	 * Returns the mean of the most recently collected data that has not yet
	 * formed a checkpoint.
	 *
	 * @return The recent mean
	 */
	@Override
	public final double getRecentMean() {
		return history.sum() / history.size();
	}

	/**
	 * Returns the mean of the squares of the most recently collected data that
	 * has not yet formed a checkpoint.
	 *
	 * @return The recent mean
	 */
	@Override
	public final double getRecentSquareMean() {
		final TDoubleIterator iter = history.iterator();
		final double[] total = new double[]{0.0};
		DoubleStream.generate(() -> iter.next()).peek((d) -> {
			total[0] += FastMath.pow(d, 2);
		}).anyMatch((d) -> !iter.hasNext());
		return total[0] / (history.size());
	}

	/**
	 * Returns the variance of the most recently collected data that has not yet
	 * formed a checkpoint.
	 *
	 * @return The recent variance
	 */
	@Override
	public final double getRecentVariance() {
		final double psi_m = history.sum() / history.size();
		final TDoubleIterator iter = history.iterator();
		final double[] total = new double[]{0.0};
		DoubleStream.generate(() -> iter.next()).peek((d) -> {
			total[0] += FastMath.pow(d - psi_m, 2);
		}).anyMatch((d) -> !iter.hasNext());
		return total[0] / (history.size() - 1);
	}

	/**
	 * Returns the square mean of this tracked quantity instance. Note that this
	 * is the mean of the squared values of all checkpoints.
	 *
	 * Does not include the most recent data points in the calculation if they
	 * have not yet reached a checkpoint in size.
	 *
	 * @return The checkpoint mean
	 */
	@Override
	public final double getSquareMean() {
		return sqr_mean_history.sum() / parent.checkpoint_count;
	}

	// TODO: Note the change of variance in the thesis, its in one of the black ringbound books as Unfurling as checkpoints with one loose sheet.
	/**
	 * Returns the variance of this tracked quantity instance. Note that this is
	 * the mean of the variances of all checkpoints.
	 *
	 * Does not include the most recent data points in the calculation if they
	 * have not yet reached a checkpoint in size.
	 *
	 * @return The checkpoint variance
	 */
	@Override
	public final double getVariance() {
		double psi_m = mean_history.sum() / mean_history.size();
		double coeff1 = (parent.checkpoint_size)
						/ (parent.checkpoint_count * parent.checkpoint_size - 1.0);
		double param1 = coeff1 * (sqr_mean_history.sum() / sqr_mean_history.size());
		double coeff2 = (2.0 * parent.checkpoint_size * parent.checkpoint_count
						- 1.0) / (parent.checkpoint_count * parent.checkpoint_size - 1.0);
		double param2 = coeff2 * psi_m * psi_m;
		return param1 + param2;
	}

	/**
	 * Returns this quantity to an initialising value. If this is not intended as
	 * a wild jump but instead a true reset, then follow this method with a call
	 * to <code>retain()</code>.
	 *
	 * @param p_rng The random generator.
	 */
	@Override
	public void initialise(RandomGenerator p_rng) {
		this.current_value = parent.initialise(p_rng);
	}

	/**
	 * Sets the previous value to the current value, recording the current value.
	 */
	@Override
	public void retain() {
		this.previous_value = this.current_value;
		this.history.add(current_value);
		assert history.size() < parent.checkpoint_size;
	}

	/**
	 * Sets the current value back to the previous value, recording that previous
	 * value.
	 */
	@Override
	public void revert() {
		this.current_value = this.previous_value;
		this.history.add(current_value);
		assert history.size() < parent.checkpoint_size;
	}

	@Override
	public String toCurrentValueString() {
		return Double.toString(current_value);
	}
}
