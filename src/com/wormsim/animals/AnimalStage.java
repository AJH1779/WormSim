/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wormsim.animals;

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

	private static int ID = 0;

	/**
	 * Creates a new stage for the animal with the specified name and strain.
	 *
	 * @param name   The name of the stage
	 * @param strain The strain of the animal
	 */
	public AnimalStage(String name, AnimalStrain strain) {
		this.food_rate = 1.0;
		this.name = name;
		this.strain = strain;
		this.id = ID++;

		this.pheromone_rates = new double[strain.getPheromoneCount()];

		this.dev_time = 1.0;
	}

	public AnimalStage(String p_name, AnimalStrain p_strain, double p_dev_time,
										 double food_rate) {
		this.food_rate = food_rate;
		this.name = p_name;
		this.strain = p_strain;
		this.id = ID++;

		this.pheromone_rates = new double[p_strain.getPheromoneCount()];

		this.dev_time = p_dev_time;
	}

	public AnimalStage(String name, AnimalStrain strain, double p_dev_time,
										 double food_rate, double pheromone_rates) {
		this.food_rate = food_rate;
		this.name = name;
		this.strain = strain;
		this.id = ID++;

		this.pheromone_rates = new double[strain.getPheromoneCount()];
		for (int i = 0; i < this.pheromone_rates.length; i++) {
			this.pheromone_rates[i] = pheromone_rates;
		}
		this.dev_time = p_dev_time;
	}

	public AnimalStage(String name, AnimalStrain strain, double p_dev_time,
										 double food_rate, double[] pheromone_rates) {
		this.food_rate = food_rate;
		this.name = name;
		this.strain = strain;
		this.id = ID++;

		this.pheromone_rates = new double[strain.getPheromoneCount()];
		for (int i = 0; i < this.pheromone_rates.length; i++) {
			this.pheromone_rates[i] = pheromone_rates.length < i
							? pheromone_rates[i]
							: 0.0;
		}
		this.dev_time = p_dev_time;
	}
	public final int id;

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

		this.food_rate = stage.food_rate;
		this.pheromone_rates = new double[strain.getPheromoneCount()];
		System.arraycopy(stage.pheromone_rates, 0, this.pheromone_rates, 0,
						stage.pheromone_rates.length);

		this.strain.addStage(this);

		this.id = ID++;
		this.dev_time = stage.dev_time;
	}
	// TODO: Make a Tracked Value or make it a container of tracked values
	// as won't be able to fulfil the default copy.
	private final double dev_time;
	private AnimalDevelopment development;
	private final double food_rate;
	private final String name;
	private final double[] pheromone_rates;
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
		return food_rate;
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
			return pheromone_rates[ref];
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

	@Override
	public boolean equals(Object obj) {
		return obj instanceof AnimalStage && ((AnimalStage) obj).id == id;
	}

	@Override
	public int hashCode() {
		return id;
	}
}
