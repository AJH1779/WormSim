/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wormsim.animals;

import java.util.HashMap;
import java.util.logging.Logger;

/**
 * Defines a strain of animal.
 *
 * @author ah810
 * @version 0.0.1
 */
public class AnimalStrain {
	private static final Logger LOG = Logger.getLogger(AnimalStrain.class
					.getName());

	/**
	 * Creates a new strain of animal that has the specified name and requires at
	 * least the specified number of pheromone channels.
	 *
	 * @param name        The strain name
	 * @param phero_count The pheromone count
	 */
	public AnimalStrain(String name, int phero_count) {
		this.stages = new HashMap<>(8);
		this.name = name;
		this.phero_count = phero_count;
	}

	/**
	 * Creates a clone of the provided strain, but does not clone the stages.
	 *
	 * @param str
	 */
	public AnimalStrain(AnimalStrain str) {
		this.name = str.name;
		this.phero_count = str.phero_count;
		this.stages = new HashMap<>(str.stages.size());
	}

	private final String name;
	private final int phero_count;

	private final HashMap<String, AnimalStage> stages;

	/**
	 * Returns the name of this animal strain.
	 *
	 * @return
	 */
	public String getName() {
		return name;
	}

	// TODO: Shift the pheromone checks back to the unmodifiable options.
	/**
	 * Returns the number of pheromone channels required to use this strain.
	 *
	 * @return
	 */
	public int getPheromoneCount() {
		return phero_count;
	}

	/**
	 * Returns the stage indicated by the reference name.
	 *
	 * @param ref
	 *
	 * @return
	 *
	 * @see AnimalStage#getName() The name accepted
	 */
	public AnimalStage getStage(String ref) {
		return stages.get(ref);
	}

	/**
	 * Adds the specified stage to the animal.
	 *
	 * @param stage The stage to add
	 *
	 * @return The provided stage
	 *
	 * @throws IllegalArgumentException Thrown if the provided stage does not
	 *                                  belong to this stage.
	 */
	AnimalStage setStage(AnimalStage stage)
					throws IllegalArgumentException {
		if (stage.getStrain() != this) {
			throw new IllegalArgumentException(
							"Provided stage must belong to this strain!");
		}
		return stages.put(stage.getName(), stage);
	}
}
