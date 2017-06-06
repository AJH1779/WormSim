/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wormsim.animals;

import com.wormsim.tracking.TrackedDoubleInstance;
import com.wormsim.tracking.TrackedQuantity;
import com.wormsim.tracking.TrackedQuantityInstance;
import com.wormsim.utils.Utils;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.apache.commons.math3.random.RandomGenerator;

/**
 *
 * @author ah810
 */
public class AnimalStage2Instance implements TrackedQuantityInstance {
	private static final Logger LOG = Logger.getLogger(AnimalStage2Instance.class
					.getName());

	AnimalStage2Instance(AnimalStage2 p_parent) {
		this.parent = p_parent;
		p_parent.register(this);
		this.pheromone_rates = new TrackedDoubleInstance[p_parent.pheromone_rates.length];
	}
	TrackedDoubleInstance dev_time;
	AnimalDevelopment2Instance development;
	TrackedDoubleInstance food_rate;
	final AnimalStage2 parent;
	final TrackedDoubleInstance[] pheromone_rates;

	@Override
	public void checkpoint() {
		food_rate.checkpoint();
		Stream.of(pheromone_rates).forEach((v) -> v.checkpoint());
		// development.checkpoint();
	}

	@Override
	public AnimalStage2Instance copy() {
		AnimalStage2Instance inst = new AnimalStage2Instance(parent);

		inst.dev_time = this.dev_time.copy();
		inst.food_rate = this.food_rate.copy();
		for (int i = 0; i < pheromone_rates.length; i++) {
			inst.pheromone_rates[i] = this.pheromone_rates[i].copy();
		}

		return inst;
	}

	@Override
	public void discard() {
		food_rate.discard();
		Stream.of(pheromone_rates).forEach((v) -> v.discard());
		// development.discard();
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
		final AnimalStage2Instance other = (AnimalStage2Instance) obj;
		return Objects.equals(this.parent, other.parent);
	}

	@Override
	public void evolve(RandomGenerator p_rng) {
		food_rate.evolve(p_rng);
		Stream.of(pheromone_rates).forEach((v) -> v.evolve(p_rng));
		// development.evolve(p_rng);
	}

	@Override
	public TrackedQuantity getParent() {
		return parent;
	}

	@Override
	public int hashCode() {
		return parent.hashCode();
	}

	@Override
	public void initialise(RandomGenerator p_rng) {
		food_rate.initialise(p_rng);
		Stream.of(pheromone_rates).forEach((v) -> v.initialise(p_rng));
		// development.initialise(p_rng);
	}

	@Override
	public void retain() {
		food_rate.retain();
		Stream.of(pheromone_rates).forEach((v) -> v.retain());
		// development.retain();
	}

	@Override
	public void revert() {
		food_rate.revert();
		Stream.of(pheromone_rates).forEach((v) -> v.revert());
		// development.revert();
	}

	@Override
	public String toCurrentValueString() {
		return Stream.concat(Stream.of(food_rate), Stream.of(pheromone_rates))
						.filter((v) -> v.getParent().isVisiblyTracked())
						.map((v) -> v.toCurrentValueString())
						.collect(Utils.TAB_JOINING);
	}

	@Override
	public String toString() {
		return parent.toString();
	}
}
