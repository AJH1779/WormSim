/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wormsim.animals;

import com.wormsim.tracking.TrackedDouble;
import com.wormsim.tracking.TrackedQuantity;
import com.wormsim.utils.Utils;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.apache.commons.math3.random.RandomGenerator;

/**
 *
 * @author ah810
 */
public class AnimalStage2 extends TrackedQuantity<AnimalStage2Instance> {

	private static int ID = 0;
	private static final Logger LOG = Logger.getLogger(AnimalStage2.class
					.getName());

	// TODO: Ensure the names of the tracked doubles are properly implemented.
	public AnimalStage2(String name, AnimalStrain2 strain, TrackedDouble food_rate,
											TrackedDouble dev_time, TrackedDouble[] pheromone_rates) {
		this.name = name;
		this.strain = strain;
		this.food_rate = food_rate;
		this.dev_time = dev_time;
		this.pheromone_rates = pheromone_rates;
		this.id = ID++;
		strain.addAnimalStage(this);
	}
	TrackedDouble dev_time;
	AnimalDevelopment2 development;
	TrackedDouble food_rate;
	final int id;
	final String name;
	final TrackedDouble[] pheromone_rates;
	final AnimalStrain2 strain;

	@Override
	public void checkpoint() {
		food_rate.checkpoint();
		Stream.of(pheromone_rates).forEach((v) -> v.checkpoint());
		dev_time.checkpoint();
		development.checkpoint();
	}

	@Override
	public void discard() {
		food_rate.discard();
		Stream.of(pheromone_rates).forEach((v) -> v.discard());
		dev_time.discard();
		development.discard();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final AnimalStage2 other = (AnimalStage2) obj;
		return this.id == other.id;
	}

	@Override
	public AnimalStage2Instance generate(RandomGenerator p_rng) {
		AnimalStage2Instance inst = new AnimalStage2Instance(this);

		inst.food_rate = food_rate.generate(p_rng);
		for (int i = 0; i < pheromone_rates.length; i++) {
			inst.pheromone_rates[i] = this.pheromone_rates[i].generate(p_rng);
		}
		inst.dev_time = dev_time.generate(p_rng);

		return inst;
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public String toBetweenVarianceString() {
		return Stream.concat(Stream.concat(Stream.concat(
						Stream.of(food_rate),
						Stream.of(pheromone_rates)),
						Stream.of(dev_time)),
						Stream.of(development))
						.filter((v) -> v.isVisiblyTracked())
						.map((v) -> v.toBetweenVarianceString()).collect(
						Utils.TAB_JOINING);
	}

	@Override
	public String toEffectiveDataCountString() {
		return Stream.concat(Stream.concat(Stream.concat(
						Stream.of(food_rate),
						Stream.of(pheromone_rates)),
						Stream.of(dev_time)),
						Stream.of(development))
						.filter((v) -> v.isVisiblyTracked())
						.map((v) -> v.toEffectiveDataCountString()).collect(
						Utils.TAB_JOINING);
	}

	@Override
	public String toHeaderString() {
		return Stream.concat(Stream.concat(Stream.concat(
						Stream.of(food_rate),
						Stream.of(pheromone_rates)),
						Stream.of(dev_time)),
						Stream.of(development))
						.filter((v) -> v.isVisiblyTracked())
						.map((v) -> v.toHeaderString())
						.collect(Utils.TAB_JOINING);
	}

	@Override
	public String toMeanString() {
		return Stream.concat(Stream.concat(Stream.concat(
						Stream.of(food_rate),
						Stream.of(pheromone_rates)),
						Stream.of(dev_time)),
						Stream.of(development))
						.filter((v) -> v.isVisiblyTracked())
						.map((v) -> v.toMeanString()).collect(
						Utils.TAB_JOINING);
	}

	@Override
	public String toPotentialScaleReductionString() {
		return Stream.concat(Stream.concat(Stream.concat(
						Stream.of(food_rate),
						Stream.of(pheromone_rates)),
						Stream.of(dev_time)),
						Stream.of(development))
						.filter((v) -> v.isVisiblyTracked())
						.map((v) -> v.toPotentialScaleReductionString()).collect(
						Utils.TAB_JOINING);
	}

	@Override
	public String toString() {
		return String.join(" ", strain.name, name);
	}

	@Override
	public String toVarianceString() {
		return Stream.concat(Stream.concat(Stream.concat(
						Stream.of(food_rate),
						Stream.of(pheromone_rates)),
						Stream.of(dev_time)),
						Stream.of(development))
						.filter((v) -> v.isVisiblyTracked())
						.map((v) -> v.toVarianceString()).collect(
						Utils.TAB_JOINING);
	}

	@Override
	public String toWithinVarianceString() {
		return Stream.concat(Stream.concat(Stream.concat(
						Stream.of(food_rate),
						Stream.of(pheromone_rates)),
						Stream.of(dev_time)),
						Stream.of(development))
						.filter((v) -> v.isVisiblyTracked())
						.map((v) -> v.toWithinVarianceString()).collect(
						Utils.TAB_JOINING);
	}

	@Override
	public String toRecentBetweenVarianceString() {
		return Stream.concat(Stream.concat(Stream.concat(
						Stream.of(food_rate),
						Stream.of(pheromone_rates)),
						Stream.of(dev_time)),
						Stream.of(development))
						.filter((v) -> v.isVisiblyTracked())
						.map((v) -> v.toRecentBetweenVarianceString()).collect(
						Utils.TAB_JOINING);
	}

	@Override
	public String toRecentEffectiveDataCountString() {
		return Stream.concat(Stream.concat(Stream.concat(
						Stream.of(food_rate),
						Stream.of(pheromone_rates)),
						Stream.of(dev_time)),
						Stream.of(development))
						.filter((v) -> v.isVisiblyTracked())
						.map((v) -> v.toRecentEffectiveDataCountString()).collect(
						Utils.TAB_JOINING);
	}

	@Override
	public String toRecentMeanString() {
		return Stream.concat(Stream.concat(Stream.concat(
						Stream.of(food_rate),
						Stream.of(pheromone_rates)),
						Stream.of(dev_time)),
						Stream.of(development))
						.filter((v) -> v.isVisiblyTracked())
						.map((v) -> v.toRecentMeanString()).collect(
						Utils.TAB_JOINING);
	}

	@Override
	public String toRecentPotentialScaleReductionString() {
		return Stream.concat(Stream.concat(Stream.concat(
						Stream.of(food_rate),
						Stream.of(pheromone_rates)),
						Stream.of(dev_time)),
						Stream.of(development))
						.filter((v) -> v.isVisiblyTracked())
						.map((v) -> v.toRecentPotentialScaleReductionString()).collect(
						Utils.TAB_JOINING);
	}

	@Override
	public String toRecentVarianceString() {
		return Stream.concat(Stream.concat(Stream.concat(
						Stream.of(food_rate),
						Stream.of(pheromone_rates)),
						Stream.of(dev_time)),
						Stream.of(development))
						.filter((v) -> v.isVisiblyTracked())
						.map((v) -> v.toRecentVarianceString()).collect(
						Utils.TAB_JOINING);
	}

	@Override
	public String toRecentWithinVarianceString() {
		return Stream.concat(Stream.concat(Stream.concat(
						Stream.of(food_rate),
						Stream.of(pheromone_rates)),
						Stream.of(dev_time)),
						Stream.of(development))
						.filter((v) -> v.isVisiblyTracked())
						.map((v) -> v.toRecentWithinVarianceString()).collect(
						Utils.TAB_JOINING);
	}
}
