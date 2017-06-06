/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wormsim.animals;

import com.wormsim.tracking.TrackedQuantity;
import com.wormsim.utils.Utils;
import java.io.IOException;
import java.util.HashMap;
import org.apache.commons.math3.random.RandomGenerator;

/**
 *
 * @author ah810
 */
public class AnimalZoo2 extends TrackedQuantity<AnimalZoo2Instance> {

	public static AnimalZoo2 read(String p_str)
					throws IOException {
		throw new UnsupportedOperationException("NYI");
	}

	/**
	 * Creates a new zoo with the specified number of pheromones.
	 *
	 * @param pheromone_no
	 */
	public AnimalZoo2() {
		this.strains = new HashMap<>();
	}
	boolean modifiable = true;
	final HashMap<String, AnimalStrain2> strains;

	@Override
	public void checkpoint() {
		strains.forEach((k, v) -> v.checkpoint());
	}

	@Override
	public void discard() {
		strains.forEach((k, v) -> v.discard());
	}

	@Override
	public AnimalZoo2Instance generate(RandomGenerator p_rng) {
		modifiable = false;
		AnimalZoo2Instance zoo = new AnimalZoo2Instance(this);

		strains.forEach((k, v) -> {
			final AnimalStrain2Instance inst = v.generate(p_rng);
			zoo.strains.put(k, inst);
			zoo.stages.putAll(inst.stages);
			zoo.developments.putAll(inst.developments);
		});

		return zoo;
	}

	@Override
	public String toBetweenVarianceString() {
		return strains.entrySet().stream()
						.map((e) -> e.getValue().toBetweenVarianceString())
						.collect(Utils.TAB_JOINING);
	}

	@Override
	public String toEffectiveDataCountString() {
		return strains.entrySet().stream()
						.map((e) -> e.getValue().toEffectiveDataCountString())
						.collect(Utils.TAB_JOINING);
	}

	@Override
	public String toHeaderString() {
		return strains.entrySet().stream()
						.map((e) -> e.getValue().toHeaderString())
						.collect(Utils.TAB_JOINING);
	}

	@Override
	public String toPotentialScaleReductionString() {
		return strains.entrySet().stream()
						.map((e) -> e.getValue().toPotentialScaleReductionString())
						.collect(Utils.TAB_JOINING);
	}

	@Override
	public String toVarianceString() {
		return strains.entrySet().stream()
						.map((e) -> e.getValue().toVarianceString())
						.collect(Utils.TAB_JOINING);
	}

	@Override
	public String toWithinVarianceString() {
		return strains.entrySet().stream()
						.map((e) -> e.getValue().toWithinVarianceString())
						.collect(Utils.TAB_JOINING);
	}

	@Override
	public String toMeanString() {
		return strains.entrySet().stream()
						.map((e) -> e.getValue().toMeanString())
						.collect(Utils.TAB_JOINING);
	}

	@Override
	public String toRecentBetweenVarianceString() {
		return strains.entrySet().stream()
						.map((e) -> e.getValue().toRecentBetweenVarianceString())
						.collect(Utils.TAB_JOINING);
	}

	@Override
	public String toRecentEffectiveDataCountString() {
		return strains.entrySet().stream()
						.map((e) -> e.getValue().toRecentEffectiveDataCountString())
						.collect(Utils.TAB_JOINING);
	}

	@Override
	public String toRecentPotentialScaleReductionString() {
		return strains.entrySet().stream()
						.map((e) -> e.getValue().toRecentPotentialScaleReductionString())
						.collect(Utils.TAB_JOINING);
	}

	@Override
	public String toRecentVarianceString() {
		return strains.entrySet().stream()
						.map((e) -> e.getValue().toRecentVarianceString())
						.collect(Utils.TAB_JOINING);
	}

	@Override
	public String toRecentWithinVarianceString() {
		return strains.entrySet().stream()
						.map((e) -> e.getValue().toRecentWithinVarianceString())
						.collect(Utils.TAB_JOINING);
	}

	@Override
	public String toRecentMeanString() {
		return strains.entrySet().stream()
						.map((e) -> e.getValue().toRecentMeanString())
						.collect(Utils.TAB_JOINING);
	}

	/**
	 * Adds the provided animal strain to the zoo so long as a strain with the
	 * same name has not already been registered. Returns true if added, false
	 * otherwise.
	 *
	 * @param p_strain The strain to add.
	 *
	 * @return True if added, false otherwise.
	 */
	boolean addAnimalStrain(AnimalStrain2 p_strain) {
		assert modifiable;
		return modifiable && strains.putIfAbsent(p_strain.name, p_strain)
						== null;
	}

}
