/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wormsim.animals;

import com.wormsim.simulation.SimulationThread;
import com.wormsim.tracking.TrackedDoubleInstance;
import com.wormsim.tracking.TrackedQuantityInstance;
import com.wormsim.utils.Utils;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.apache.commons.math3.random.RandomGenerator;

/**
 *
 * @author ah810
 */
public final class AnimalDevelopment2Instance implements TrackedQuantityInstance {
	private static final Logger LOG = Logger.getLogger(
					AnimalDevelopment2Instance.class.getName());

	/**
	 *
	 * @param p_parent
	 * @param values
	 * @param targets
	 */
	AnimalDevelopment2Instance(AnimalDevelopment2 p_parent,
														 TrackedDoubleInstance[] values,
														 AnimalStage2Instance[] targets) {
		this.parent = p_parent;
		this.values = values;
		this.targets = targets;
		p_parent.register(this);
	}
	final AnimalDevelopment2 parent;
	final AnimalStage2Instance[] targets;
	final TrackedDoubleInstance[] values;

	@Override
	public void checkpoint() {
		Stream.of(values).forEach((v) -> v.checkpoint());
	}

	@Override
	public AnimalDevelopment2Instance copy() {
		TrackedDoubleInstance[] cpy_values = new TrackedDoubleInstance[values.length];
		for (int i = 0; i < cpy_values.length; i++) {
			cpy_values[i] = values[i].copy();
		}
		AnimalStage2Instance[] cpy_targets = new AnimalStage2Instance[targets.length];
		System.arraycopy(targets, 0, cpy_targets, 0, cpy_targets.length);
		return new AnimalDevelopment2Instance(parent, cpy_values, cpy_targets);
	}

	/**
	 * Applies the function specified in the AnimalDevelopment2 object this was
	 * created from, using the parameters values carried here.
	 *
	 * @param iface The sampling interface
	 * @param count The number of animals
	 * @param rng   The random number generator
	 */
	public void develop(SimulationThread.DevelopmentInterface iface, int count,
											RandomGenerator rng) {
		parent.apply(iface, count, rng, values, targets);
	}

	@Override
	public void discard() {
		Stream.of(values).forEach((v) -> v.discard());
	}

	@Override
	public void evolve(RandomGenerator p_rng) {
		Stream.of(values).forEach((v) -> v.evolve(p_rng));
	}

	@Override
	public AnimalDevelopment2 getParent() {
		return parent;
	}

	@Override
	public void initialise(RandomGenerator p_rng) {
		Stream.of(values).forEach((v) -> v.initialise(p_rng));
	}

	@Override
	public void retain() {
		Stream.of(values).forEach((v) -> v.retain());
	}

	@Override
	public void revert() {
		Stream.of(values).forEach((v) -> v.revert());
	}

	@Override
	public String toCurrentValueString() {
		return Stream.of(values)
						.filter((v) -> v.getParent().isVisiblyTracked())
						.map((v) -> v.toCurrentValueString())
						.collect(Utils.TAB_JOINING);
	}
}
