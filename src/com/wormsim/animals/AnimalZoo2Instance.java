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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.apache.commons.math3.random.RandomGenerator;

/**
 *
 * @author ah810
 */
public class AnimalZoo2Instance implements TrackedQuantityInstance {
	private static final Logger LOG = Logger.getLogger(AnimalZoo2Instance.class
					.getName());

	AnimalZoo2Instance(AnimalZoo2 p_parent) {
		this.parent = p_parent;
		this.developments = new HashMap<>();
		this.stages = new HashMap<>();
		this.strains = new HashMap<>();
		this.tracked_quantities = new ArrayList<>();
		p_parent.register(this);
	}
	final HashMap<AnimalStage2Instance, AnimalDevelopment2Instance> developments;
	final AnimalZoo2 parent;
	final HashMap<String, AnimalStage2Instance> stages;
	final HashMap<String, AnimalStrain2Instance> strains;
	final ArrayList<TrackedQuantityInstance> tracked_quantities;

	@Override
	public void checkpoint() {
		this.tracked_quantities.forEach((v) -> v.checkpoint());
		this.strains.forEach((k, v) -> v.checkpoint());
	}

	@Override
	public TrackedDoubleInstance copy() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void discard() {
		this.tracked_quantities.forEach((v) -> v.discard());
		this.strains.forEach((k, v) -> v.discard());
	}

	@Override
	public void evolve(RandomGenerator p_rng) {
		this.tracked_quantities.forEach((v) -> v.evolve(p_rng));
		this.strains.forEach((k, v) -> v.evolve(p_rng));
	}

	public AnimalStage2Instance getAnimalStage(String key) {
		if (!this.stages.containsKey(key)) {
			System.out.println("Input: " + key);
			stages.forEach((k, v) -> {
				System.out.println(k);
			});
			throw new AssertionError("Should match!");
		}
		return this.stages.get(key);
	}

	@Override
	public TrackedQuantity getParent() {
		return parent;
	}

	@Override
	public void initialise(RandomGenerator p_rng) {
		this.tracked_quantities.forEach((v) -> v.initialise(p_rng));
		this.strains.forEach((k, v) -> v.initialise(p_rng));
	}

	@Override
	public void retain() {
		this.tracked_quantities.stream().forEach((v) -> v.retain());
		this.strains.forEach((k, v) -> v.retain());
	}

	@Override
	public void revert() {
		this.tracked_quantities.stream().forEach((v) -> v.revert());
		this.strains.forEach((k, v) -> v.revert());
	}

	@Override
	public String toCurrentValueString() {
		return Stream.concat(tracked_quantities.stream(), strains.values().stream())
						.filter((v) -> v.getParent().isVisiblyTracked())
						.map((v) -> v.toCurrentValueString())
						.collect(Utils.TAB_JOINING);
	}

}
