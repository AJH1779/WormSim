/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wormsim.animals;

import com.wormsim.data.TrackedValue.TrackedDouble;
import com.wormsim.simulation.SimulationThread.ScoringInterface;
import java.util.logging.Logger;

// TODO: Maximum total pheromone production rate and other more generalised limitations.
/**
 * Denotes a stage in the animal's life cycle.
 *
 * @author ah810
 * @version 0.0.1
 */
public final class AnimalStage {
	private static final Logger LOG = Logger
					.getLogger(AnimalStage.class.getName());

	/**
	 * Creates a new stage for the animal with the specified name and strain.
	 *
	 * @param name   The name of the stage
	 * @param strain The strain of the animal
	 */
	public AnimalStage(String name, AnimalStrain strain) {
		this.food_rate = new TrackedDouble(1.0);
		this.name = name;
		this.strain = strain;

		this.pheromone_rates = new TrackedDouble[strain.getPheromoneCount()];
		for (int i = 0; i < this.pheromone_rates.length; i++) {
			this.pheromone_rates[i] = new TrackedDouble(1.0);
		}
	}

	/**
	 * Creates a new stage cloning the provided stage but for the provided strain.
	 * All of the rate functions are copied across, either identical in the case
	 * of a standard rate function or using the <code>copy()</code> of a
	 * <code>TrackedRateFunction</code>
	 *
	 * @param strain The strain of this animal
	 * @param stage  The stage to copy
	 */
	@SuppressWarnings({"LeakingThisInConstructor", "unchecked"})
	public AnimalStage(AnimalStrain strain, AnimalStage stage) {
		this.name = stage.name;
		this.strain = strain;

		this.food_rate = stage.food_rate.copy();
		this.pheromone_rates = new TrackedDouble[strain.getPheromoneCount()];
		for (int i = 0; i < this.pheromone_rates.length; i++) {
			this.pheromone_rates[i] = stage.pheromone_rates[i].copy();
		}

		this.strain.addStage(this);
	}
	private double dev_time = 1.0;
	private AnimalDevelopment development;
	private final TrackedDouble food_rate;
	private final String name;
	private final TrackedDouble[] pheromone_rates;
	private final AnimalStrain strain;

	/**
	 * Returns the animal development decision for this stage.
	 *
	 * @return The development decision.
	 */
	public AnimalDevelopment getAnimalDevelopment() {
		return this.development;
	}

	/**
	 * Returns the time required to develop to the next development stage.
	 *
	 * @return The development time
	 */
	public double getDevelopmentTime() {
		return dev_time;
	}

	/**
	 * Returns the food consumption rate.
	 *
	 * @return The food consumption rate
	 */
	public double getFoodConsumptionRate() {
		return food_rate.get();
	}

	/**
	 * Returns the full name of the stage, equivalent to
	 * <code>strain.getName() + " " + this.getName()</code>.
	 *
	 * @return The full name of the stage.
	 */
	public String getFullName() {
		return strain.getName() + " " + getName();
	}

	/**
	 * The name of this animal stage.
	 *
	 * @return The name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the pheromone production rate of the indexed pheromone.
	 *
	 * @param ref The pheromone reference
	 *
	 * @return The pheromone production rate
	 */
	public double getPheromoneProductionRate(int ref) {
		if (ref < 0 || ref >= pheromone_rates.length) {
			return 0.0;
		} else {
			return pheromone_rates[ref].get();
		}
	}

	/**
	 * Returns the strain of the animal.
	 *
	 * @return The strain.
	 */
	public AnimalStrain getStrain() {
		return strain;
	}

	/**
	 * Called at the end of the simulation run for allocating additional score
	 * based on the composition of the final stage.
	 *
	 * By default, simply adds the number of animals at this stage to the score
	 * board.
	 *
	 * @param iface The scoring interface
	 * @param count The number in the group.
	 */
	public void score(ScoringInterface iface, int count) {
		iface.addScore(this.getFullName(), count);
		// TODO: Allow for a ScoringFunction instead?
	}

	/**
	 * Sets the animal developmental decision for this stage.
	 *
	 * @param dev The development decision.
	 */
	void setAnimalDevelopment(AnimalDevelopment dev) {
		this.development = dev;
	}

	/**
	 * Sets the time required to develop to the next development stage.
	 *
	 * @param dev_time The new development time
	 */
	void setDevelopmentTime(double dev_time) {
		this.dev_time = dev_time;
	}
}
