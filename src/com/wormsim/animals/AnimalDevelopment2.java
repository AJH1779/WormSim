/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wormsim.animals;

import com.wormsim.simulation.SimulationThread;
import com.wormsim.tracking.TrackedDouble;
import com.wormsim.tracking.TrackedDoubleInstance;
import com.wormsim.tracking.TrackedQuantity;
import com.wormsim.utils.Utils;
import java.util.stream.Stream;
import org.apache.commons.math3.random.RandomGenerator;

/**
 *
 * @author ah810
 */
public abstract class AnimalDevelopment2
				extends TrackedQuantity<AnimalDevelopment2Instance> {

	/**
	 * Creates a new animal development using the specified parameters.
	 *
	 * @param actor   The animal performing this development.
	 * @param values  The parameters.
	 * @param targets The affected targets.
	 */
	public AnimalDevelopment2(AnimalStage2 actor,
														TrackedDouble[] values,
														AnimalStage2[] targets) {
		this.actor = actor;
		this.tracked_values = values;
		this.tracked_targets = targets;
		if (!actor.strain.modifiable) {
			throw new IllegalStateException(
							"Attempted to modifiable an unmodifiable strain!");
		}
		actor.development = this;
		actor.strain.developments.put(actor, this);
	}
	final AnimalStage2 actor;
	final AnimalStage2[] tracked_targets;
	final TrackedDouble[] tracked_values;

	/**
	 * Applies this function to the given arguments.
	 *
	 * @param iface    The sampling interface
	 * @param count    The number of animals
	 * @param rng      The random number generator
	 * @param values   The parameterised variables to use.
	 * @param p_stages The stages to generate out of this.
	 */
	public abstract void apply(SimulationThread.DevelopmentInterface iface,
														 int count,
														 RandomGenerator rng,
														 TrackedDoubleInstance[] values,
														 AnimalStage2Instance[] p_stages);

	@Override
	public void checkpoint() {
		// Stream.of(this.tracked_targets).forEach((v) -> v.checkpoint());
		Stream.of(this.tracked_values).forEach((v) -> v.checkpoint());
	}

	@Override
	public void discard() {
		// Stream.of(this.tracked_targets).forEach((v) -> v.discard());
		Stream.of(this.tracked_values).forEach((v) -> v.discard());
	}

	@Override
	public AnimalDevelopment2Instance generate(RandomGenerator p_rng) {
		TrackedDoubleInstance[] values = new TrackedDoubleInstance[tracked_values.length];
		for (int i = 0; i < values.length; i++) {
			values[i] = tracked_values[i].generate(p_rng);
		}
		AnimalStage2Instance[] targets = new AnimalStage2Instance[tracked_targets.length];
		return new AnimalDevelopment2Instance(this, values, targets);
	}

	@Override
	public String toBetweenVarianceString() {
		return Stream.of(tracked_values)
						.filter((v) -> v.isVisiblyTracked())
						.map((v) -> v.toBetweenVarianceString())
						.collect(Utils.TAB_JOINING);
	}

	@Override
	public String toEffectiveDataCountString() {
		return Stream.of(tracked_values)
						.filter((v) -> v.isVisiblyTracked())
						.map((v) -> v.toEffectiveDataCountString())
						.collect(Utils.TAB_JOINING);
	}

	@Override
	public String toHeaderString() {
		return Stream.of(tracked_values)
						.filter((v) -> v.isVisiblyTracked())
						.map((v) -> v.toHeaderString())
						.collect(Utils.TAB_JOINING);
	}

	@Override
	public String toMeanString() {
		return Stream.of(tracked_values)
						.filter((v) -> v.isVisiblyTracked())
						.map((v) -> v.toMeanString())
						.collect(Utils.TAB_JOINING);
	}

	@Override
	public String toPotentialScaleReductionString() {
		return Stream.of(tracked_values)
						.filter((v) -> v.isVisiblyTracked())
						.map((v) -> v.toPotentialScaleReductionString())
						.collect(Utils.TAB_JOINING);
	}

	@Override
	public String toVarianceString() {
		return Stream.of(tracked_values)
						.filter((v) -> v.isVisiblyTracked())
						.map((v) -> v.toVarianceString())
						.collect(Utils.TAB_JOINING);
	}

	@Override
	public String toWithinVarianceString() {
		return Stream.of(tracked_values)
						.filter((v) -> v.isVisiblyTracked())
						.map((v) -> v.toWithinVarianceString())
						.collect(Utils.TAB_JOINING);
	}

	@Override
	public String toRecentBetweenVarianceString() {
		return Stream.of(tracked_values)
						.filter((v) -> v.isVisiblyTracked())
						.map((v) -> v.toRecentBetweenVarianceString())
						.collect(Utils.TAB_JOINING);
	}

	@Override
	public String toRecentEffectiveDataCountString() {
		return Stream.of(tracked_values)
						.filter((v) -> v.isVisiblyTracked())
						.map((v) -> v.toRecentEffectiveDataCountString())
						.collect(Utils.TAB_JOINING);
	}

	@Override
	public String toRecentMeanString() {
		return Stream.of(tracked_values)
						.filter((v) -> v.isVisiblyTracked())
						.map((v) -> v.toRecentMeanString())
						.collect(Utils.TAB_JOINING);
	}

	@Override
	public String toRecentPotentialScaleReductionString() {
		return Stream.of(tracked_values)
						.filter((v) -> v.isVisiblyTracked())
						.map((v) -> v.toRecentPotentialScaleReductionString())
						.collect(Utils.TAB_JOINING);
	}

	@Override
	public String toRecentVarianceString() {
		return Stream.of(tracked_values)
						.filter((v) -> v.isVisiblyTracked())
						.map((v) -> v.toRecentVarianceString())
						.collect(Utils.TAB_JOINING);
	}

	@Override
	public String toRecentWithinVarianceString() {
		return Stream.of(tracked_values)
						.filter((v) -> v.isVisiblyTracked())
						.map((v) -> v.toRecentWithinVarianceString())
						.collect(Utils.TAB_JOINING);
	}

	public static class ExplosiveLaying extends AnimalDevelopment2 {
		public ExplosiveLaying(AnimalStage2 actor, AnimalStage2 egg,
													 TrackedDouble rate) {
			super(actor, new TrackedDouble[]{rate}, new AnimalStage2[]{egg});
		}

		@Override
		public void apply(SimulationThread.DevelopmentInterface p_iface, int p_count,
											RandomGenerator p_rng, TrackedDoubleInstance[] p_values,
											AnimalStage2Instance[] p_stages) {
			assert p_stages[0] != null;
			if (p_stages[0] != null) {
				p_iface.addGroup(new AnimalGroup(p_stages[0], (int) p_values[0].get()));
			}
		}
	}

	public static class GradualLaying extends AnimalDevelopment2 {
		public GradualLaying(AnimalStage2 actor,
												 AnimalStage2 next_layer,
												 AnimalStage2 egg,
												 TrackedDouble rate) {
			super(actor, new TrackedDouble[]{rate},
							new AnimalStage2[]{next_layer, egg});
		}

		@Override
		public void apply(SimulationThread.DevelopmentInterface p_iface, int p_count,
											RandomGenerator p_rng, TrackedDoubleInstance[] p_values,
											AnimalStage2Instance[] p_stages) {
			if (p_stages[0] != null) {
				p_iface.addGroup(new AnimalGroup(p_stages[0], p_count));
				p_iface.addGroup(new AnimalGroup(p_stages[1], p_count
								* (int) p_values[0].get()));
			}
		}
	}

	public static class Linear extends AnimalDevelopment2 {
		public Linear(AnimalStage2 actor,
									AnimalStage2 next) {
			super(actor, new TrackedDouble[0], new AnimalStage2[]{next});
		}

		@Override
		public void apply(SimulationThread.DevelopmentInterface p_iface,
											int p_count, RandomGenerator p_rng,
											TrackedDoubleInstance[] p_values,
											AnimalStage2Instance[] p_stages) {
			if (p_stages[0] != null) {
				p_iface.addGroup(new AnimalGroup(p_stages[0], p_count));
			}
		}
	}
}
