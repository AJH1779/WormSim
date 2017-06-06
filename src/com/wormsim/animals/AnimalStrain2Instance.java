/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wormsim.animals;

import com.wormsim.tracking.TrackedQuantity;
import com.wormsim.tracking.TrackedQuantityInstance;
import com.wormsim.utils.Utils;
import java.util.HashMap;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.apache.commons.math3.random.RandomGenerator;

/**
 *
 * @author ah810
 */
public class AnimalStrain2Instance implements TrackedQuantityInstance {
	private static final Logger LOG = Logger.getLogger(AnimalStrain2Instance.class
					.getName());

	AnimalStrain2Instance(AnimalStrain2 p_parent) {
		this.parent = p_parent;
		this.stages = new HashMap<>(8);
		this.developments = new HashMap<>(8);
		p_parent.register(this);
	}
	final HashMap<AnimalStage2Instance, AnimalDevelopment2Instance> developments;
	final AnimalStrain2 parent;
	final HashMap<String, AnimalStage2Instance> stages;

	@Override
	public void checkpoint() {
		stages.values().forEach((s) -> s.checkpoint());
		developments.values().forEach((s) -> s.checkpoint());
	}

	@Override
	public AnimalStrain2Instance copy() {
		AnimalStrain2Instance inst = new AnimalStrain2Instance(parent);

		stages.forEach((k, s) -> {
			inst.stages.put(k, s.copy());
		});
		developments.forEach((k, s) -> {
			inst.developments.put(inst.stages.get(k.toString()), s.copy());
		});

		return inst;
	}

	@Override
	public void discard() {
		stages.values().forEach((s) -> s.discard());
		developments.values().forEach((s) -> s.discard());
	}

	@Override
	public void evolve(RandomGenerator p_rng) {
		stages.values().forEach((s) -> s.evolve(p_rng));
		developments.values().forEach((s) -> s.evolve(p_rng));
	}

	@Override
	public TrackedQuantity getParent() {
		return parent;
	}

	@Override
	public void initialise(RandomGenerator p_rng) {
		stages.values().forEach((s) -> s.initialise(p_rng));
		developments.values().forEach((s) -> s.initialise(p_rng));
	}

	@Override
	public void retain() {
		stages.values().forEach((s) -> s.retain());
		developments.values().forEach((s) -> s.retain());
	}

	@Override
	public void revert() {
		stages.values().forEach((s) -> s.revert());
		developments.values().forEach((s) -> s.revert());
	}

	@Override
	public String toCurrentValueString() {
		return Stream.concat(
						stages.values().stream(),
						developments.values().stream())
						.filter((v) -> v.getParent().isVisiblyTracked())
						.map((v) -> v.toCurrentValueString())
						.collect(Utils.TAB_JOINING);
	}

}
