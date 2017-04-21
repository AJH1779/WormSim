/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wormsim.data;

import java.util.ArrayList;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.util.FastMath;

/**
 * A double value that may be tracked and recorded or optimised.
 */
public class TrackedDouble implements TrackedValue {
	private static final long serialVersionUID = 1L;
	public static final TrackedDouble ZERO = new TrackedDouble(0.0);

	private TrackedDouble(TrackedDouble t) {
		this.value = t.value;
		this.all_related = t.all_related;
		this.history = new ArrayList<>(t.history);
		this.spread = t.spread;
	}

	public TrackedDouble(double val) {
		this(val, 0.005);
	}

	/**
	 * Constructs a new tracked value. The constructed value does not influence
	 * variance calculations.
	 *
	 * @param val
	 * @param spread
	 */
	@SuppressWarnings(
					value = {"LeakingThisInConstructor", "CollectionWithoutInitialCapacity"})
	public TrackedDouble(double val, double spread) {
		this.history = new ArrayList<>();
		this.prev_value = this.value = val;
		this.all_related = new ArrayList<>();
		this.spread = spread;
		// this.all_related.add(this);
		// TODO: Max, Min, Std. Dev of Transition, transition type?
	}
	private final ArrayList<TrackedDouble> all_related;
	private final ArrayList<Double> history;
	private double prev_value;
	private double spread;
	private double value;

	// TODO: Ignore the burn-in after a certain point.
	/**
	 * Creates a copy of this tracked value. Note that the copied object affects
	 * the calculations of variance.
	 *
	 * @return A copy
	 */
	@Override
	public TrackedDouble copy() {
		TrackedDouble t = new TrackedDouble(this);
		all_related.add(t);
		return t;
	}

	@Override
	public void evolve(RandomGenerator rng) {
		// TODO: Make there be a more controlled treatment of this variable.
		value = rng.nextGaussian() * spread + value;
	}

	/**
	 * Returns the current value of this tracked value.
	 *
	 * @return The current value.
	 */
	public double get() {
		return value;
	}

	/**
	 * Returns an estimate of the variance of this class of related tracked
	 * values.
	 *
	 * @return The Between Variance.
	 */
	public double getBetweenVariance() {
		double psi__ = all_related.stream()
						.mapToDouble((arr) ->
										arr.history.stream()
														.mapToDouble((d) -> d / (arr.history.size()))
														.sum() / (all_related.size())).sum();
		return all_related.stream()
						.mapToDouble((TrackedDouble arr) ->
										FastMath.pow(arr.history.stream()
														.mapToDouble((d) -> d).sum()
														/ (arr.history.size()) - psi__, 2) * arr.history
										.size() / (all_related.size() - 1)).sum();
	}

	public double getMeanHistoryCount() {
		return all_related.stream().mapToDouble((t) -> t.history.size()).average()
						.getAsDouble();
	}

	/**
	 * Returns an estimate for the within variance as the mean of this class of
	 * related tracked values.
	 *
	 * @return The Within Variance.
	 */
	public double getMeanWithinVariance() {
		return all_related.stream()
						.mapToDouble((arr) -> arr.getWithinVariance() / all_related.size())
						.sum();
	}

	/**
	 * Returns the potential scale reduction of this value.
	 *
	 * TODO: Note that this uses other methods but does not store them.
	 *
	 * TODO: Reduce computational cost of repeated access of variances.
	 *
	 * @return The potential scale reduction.
	 */
	public double getPotentialScaleReduction() {
		double N = getMeanHistoryCount();
		double B = getBetweenVariance();
		double W = getMeanWithinVariance();
		return FastMath.sqrt((N - 1.0) / N + B / (N * W));
	}

	public double getSpread() {
		return spread;
	}

	/**
	 * Returns the over-estimated variance of this value.
	 *
	 * @return A variance over-estimate.
	 */
	public double getVariance() {
		double N = getMeanHistoryCount();
		double B = getBetweenVariance();
		double W = getMeanWithinVariance();
		return (W * (N - 1.0)) / N + B / N;
	}

	/**
	 * Returns an estimate of the variance of this tracked value.
	 *
	 * @return The variance.
	 */
	public double getWithinVariance() {
		final double psi_m = history.stream()
						.mapToDouble((d) -> d / (history.size())).sum();
		return history.stream()
						.mapToDouble((d) -> FastMath.pow(d - psi_m, 2) / (history.size()
										- 1)).sum();
	}

	@Override
	public void initialise(RandomGenerator p_rng) {
	}

	/**
	 * Returns true if the variance calculations are not dependent on this, false
	 * otherwise.
	 *
	 * @return
	 */
	public boolean isAffectingVariance() {
		return !this.all_related.contains(this);
	}

	@Override
	public void retain() {
		prev_value = value;
		history.add(value);
	}

	@Override
	public void revert() {
		value = prev_value;
		history.add(value);
	}

	public void set(double d) {
		this.value = d;
		retain();
	}

	public void setSpread(double p_spread) {
		this.spread = p_spread;
	}

	/**
	 * Removes this instance as being used for calculations, so this can be used
	 * simply as a way of monitoring the value.
	 *
	 * @return
	 */
	@Override
	public boolean stopAffectingVariance() {
		return this.all_related.remove(this);
	}

	@Override
	public String toBetweenVarianceString() {
		return Double.toString(this.getBetweenVariance());
	}

	@Override
	public String toCurrentValueString() {
		return Double.toString(this.value);
	}

	@Override
	public String toHeaderString() {
		return "NULL";
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
		return Double.toString(this.getMeanWithinVariance());
	}

}
