/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wormsim.animals;

import com.wormsim.tracking.TrackedQuantity;
import com.wormsim.utils.Utils;
import java.util.HashMap;
import java.util.logging.Logger;
import org.apache.commons.math3.random.RandomGenerator;

/**
 *
 * @author ah810
 */
public class AnimalStrain2 extends TrackedQuantity<AnimalStrain2Instance> {
	private static final Logger LOG = Logger.getLogger(AnimalStrain2.class
					.getName());

	public AnimalStrain2(AnimalZoo2 zoo, String name) {
		this.zoo = zoo;
		this.name = name;
		this.stages = new HashMap<>(8);
		this.developments = new HashMap<>(8);
		zoo.addAnimalStrain(this);
	}
	final HashMap<AnimalStage2, AnimalDevelopment2> developments;
	boolean modifiable = true;
	final String name;
	final HashMap<String, AnimalStage2> stages;
	final AnimalZoo2 zoo;

	@Override
	public void checkpoint() {
		stages.forEach((k, v) -> v.checkpoint());
	}

	@Override
	public void discard() {
		stages.forEach((k, v) -> v.discard());
	}

	@Override
	public AnimalStrain2Instance generate(RandomGenerator p_rng) {
		modifiable = false;
		AnimalStrain2Instance strain = new AnimalStrain2Instance(this);

		stages.forEach((k, v) -> {
			strain.stages.put(k, v.generate(p_rng));
		});
		developments.forEach((k, v) -> {
			AnimalDevelopment2Instance inst = v.generate(p_rng);

			for (int i = 0; i < inst.targets.length; i++) {
				inst.targets[i] = strain.stages.get(inst.parent.tracked_targets[i]
								.toString());
			}
			final AnimalStage2Instance key = strain.stages.get(k.toString());
			key.development = inst;

			strain.developments.put(key, inst);
		});

		return strain;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toBetweenVarianceString() {
		return stages.values().stream()
						.filter((v) -> v.isVisiblyTracked())
						.map((v) -> {
							return v.toBetweenVarianceString();
						})
						.collect(Utils.TAB_JOINING);
	}

	@Override
	public String toEffectiveDataCountString() {
		return stages.values().stream()
						.filter((v) -> v.isVisiblyTracked())
						.map((v) -> v.toEffectiveDataCountString())
						.collect(Utils.TAB_JOINING);
	}

	@Override
	public String toHeaderString() {
		return stages.values().stream()
						.filter((v) -> v.isVisiblyTracked())
						.map((v) -> v.toHeaderString())
						.collect(Utils.TAB_JOINING);
	}

	@Override
	public String toMeanString() {
		return stages.values().stream()
						.filter((v) -> v.isVisiblyTracked())
						.map((v) -> {
							return v.toMeanString();
						})
						.collect(Utils.TAB_JOINING);
	}

	@Override
	public String toPotentialScaleReductionString() {
		return stages.values().stream()
						.filter((v) -> v.isVisiblyTracked())
						.map((v) -> v.toPotentialScaleReductionString())
						.collect(Utils.TAB_JOINING);
	}

	@Override
	public String toVarianceString() {
		return stages.values().stream()
						.filter((v) -> v.isVisiblyTracked())
						.map((v) -> v.toVarianceString())
						.collect(Utils.TAB_JOINING);
	}

	@Override
	public String toWithinVarianceString() {
		return stages.values().stream()
						.filter((v) -> v.isVisiblyTracked())
						.map((v) -> v.toWithinVarianceString())
						.collect(Utils.TAB_JOINING);
	}

	@Override
	public String toRecentBetweenVarianceString() {
		return stages.values().stream()
						.filter((v) -> v.isVisiblyTracked())
						.map((v) -> {
							return v.toRecentBetweenVarianceString();
						})
						.collect(Utils.TAB_JOINING);
	}

	@Override
	public String toRecentEffectiveDataCountString() {
		return stages.values().stream()
						.filter((v) -> v.isVisiblyTracked())
						.map((v) -> v.toRecentEffectiveDataCountString())
						.collect(Utils.TAB_JOINING);
	}

	@Override
	public String toRecentMeanString() {
		return stages.values().stream()
						.filter((v) -> v.isVisiblyTracked())
						.map((v) -> {
							return v.toRecentMeanString();
						})
						.collect(Utils.TAB_JOINING);
	}

	@Override
	public String toRecentPotentialScaleReductionString() {
		return stages.values().stream()
						.filter((v) -> v.isVisiblyTracked())
						.map((v) -> v.toRecentPotentialScaleReductionString())
						.collect(Utils.TAB_JOINING);
	}

	@Override
	public String toRecentVarianceString() {
		return stages.values().stream()
						.filter((v) -> v.isVisiblyTracked())
						.map((v) -> v.toRecentVarianceString())
						.collect(Utils.TAB_JOINING);
	}

	@Override
	public String toRecentWithinVarianceString() {
		return stages.values().stream()
						.filter((v) -> v.isVisiblyTracked())
						.map((v) -> v.toRecentWithinVarianceString())
						.collect(Utils.TAB_JOINING);
	}

	boolean addAnimalStage(AnimalStage2 stage) {
		return modifiable && stages.putIfAbsent(stage.toString(), stage) == null;
	}

	boolean addDevelopmentStage(AnimalDevelopment2 dev) {
		return modifiable && developments.putIfAbsent(dev.actor, dev) == null;
	}
}
