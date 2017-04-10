/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wormsim.data;

import com.wormsim.animals.DevelopmentFunction;
import com.wormsim.animals.ScoringFunction;
import java.util.ArrayList;
import org.apache.commons.math3.random.RandomGenerator;
import static org.apache.commons.math3.util.FastMath.pow;
import static org.apache.commons.math3.util.FastMath.sqrt;

/**
 * A value that may be tracked and recorded or optimised.
 *
 * @author ah810
 * @version 0.0.1
 */
public interface TrackedValue {
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

	/**
	 * A double value that may be tracked and recorded or optimised.
	 */
	public static class TrackedDouble implements TrackedValue {
		private TrackedDouble(TrackedDouble t) {
			this.value = t.value;
			this.all_related = t.all_related;
			this.history = new ArrayList<>(t.history);
		}

		/**
		 * Constructs a new tracked value. The constructed value does not influence
		 * variance calculations.
		 *
		 * @param val
		 */
		@SuppressWarnings({"LeakingThisInConstructor", "CollectionWithoutInitialCapacity"})
		public TrackedDouble(double val) {
			this.history = new ArrayList<>();
			this.prev_value = this.value = val;
			this.all_related = new ArrayList<>();
			// this.all_related.add(this);
			// TODO: Max, Min, Std. Dev of Transition, transition type?
		}
		private final ArrayList<TrackedDouble> all_related;
		private final ArrayList<Double> history;
		private double value;
		private double prev_value;

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
			value = rng.nextGaussian() + value;
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
		 * Returns an estimate of the variance of this class of tracked values.
		 *
		 * @return The variance.
		 */
		public double getBetweenVariance() {
			double psi__ = all_related.stream().mapToDouble((arr) -> arr.history
							.stream().mapToDouble((d) -> d / (arr.history.size())).sum()
							/ (all_related.size())).sum();
			return all_related.stream().mapToDouble((arr) -> pow(arr.history.stream()
							.mapToDouble((d) -> d).sum() / (arr.history.size()) - psi__, 2)
							* arr.history.size() / (all_related.size() - 1)).sum();
		}

		public double getMeanWithinVariance() {
			return all_related.stream().mapToDouble((arr) -> arr.getWithinVariance()
							/ all_related.size()).sum();
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
		public double getR() {
			double N = history.size();
			double B = getBetweenVariance();
			double W = getMeanWithinVariance();
			return sqrt((N - 1.0) / N + B / (N * W));
		}

		/**
		 * Returns the over-estimated variance of this value.
		 *
		 * @return A variance over-estimate.
		 */
		public double getVariance() {
			double N = history.size();
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
			final double psi_m = history.stream().mapToDouble((d) -> d / (history
							.size()))
							.sum();
			return history.stream().mapToDouble((d) -> pow(d - psi_m, 2)
							/ (history.size() - 1)).sum();
		}

		/**
		 * Returns true if the variance calculations are not dependent on this,
		 * false otherwise.
		 *
		 * @return
		 */
		public boolean isAffectingVariance() {
			return !this.all_related.contains(this);
		}

		@Override
		public void retain() {
			prev_value = value;
		}

		@Override
		public void revert() {
			value = prev_value;
		}

		/**
		 * Removes this instance as being used for calculations, so this can be used
		 * simply as a way of monitoring the value.
		 *
		 * @return
		 */
		public boolean stopAffectingVariance() {
			return this.all_related.remove(this);
		}
	}

	public static interface TrackedDecisionFunction extends TrackedValue, DevelopmentFunction {

		@Override
		public TrackedDecisionFunction copy();
	}

	public static interface TrackedScoringFunction extends TrackedValue, ScoringFunction {

		@Override
		public TrackedScoringFunction copy();
	}
}
