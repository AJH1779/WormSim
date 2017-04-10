/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wormsim.animals;

import com.wormsim.simulation.SimulationThread.ConsumeInterface;
import com.wormsim.simulation.SimulationThread.DevelopmentInterface;
import com.wormsim.simulation.SimulationThread.ScoringInterface;
import java.util.logging.Logger;
import org.apache.commons.math3.random.RandomGenerator;

/**
 * Denotes a group of animals with identical qualities.
 *
 * @author ah810
 * @version 0.0.1
 */
public class AnimalGroup {
	private static final Logger LOG = Logger
					.getLogger(AnimalGroup.class.getName());

	/**
	 * Creates a new group of animals at the specified stage.
	 *
	 * @param animal The animal stage the creatures are at
	 * @param count  The number of animals
	 */
	public AnimalGroup(AnimalStage animal, int count) {
		this(animal, count, animal.getDevelopmentTime());
	}

	/**
	 * Creates a new group of animals at the specified stage, overriding the
	 * development time with the specified amount.
	 *
	 * @param animal   The animal stage the creatures are at
	 * @param count    The number of animals
	 * @param dev_time The time until the next development stage
	 */
	public AnimalGroup(AnimalStage animal, int count, double dev_time) {
		this.animal = animal;
		this.count = count;
		this.dev_time_rem = dev_time;
	}
	private final AnimalStage animal;
	private final int count;
	private double dev_time_rem;

	/**
	 * Consumes food over the specified time period.
	 *
	 * @param iface The consumer interface
	 * @param delt  The time elapsed
	 */
	public void consumeAndEmit(ConsumeInterface iface, double delt) {
		iface.eatFood(getCount() * getAnimalStage().getFoodConsumptionRate()
						* delt);
		for (int j = 0; j < iface.getPheromoneNumber(); j++) {
			iface.emitPheromone(getCount()
							* getAnimalStage().getPheromoneProductionRate(j) * delt, j);
		}
		dev_time_rem -= delt;
	}

	/**
	 * Develops to the next developmental stage. This will succeed even if there
	 * is still time remaining before expected development.
	 *
	 * @param iface The development interface
	 * @param rng   The random generator to use
	 */
	public void develop(DevelopmentInterface iface, RandomGenerator rng) {
		animal.getAnimalDevelopment().develop(iface, count, rng);
	}

	/**
	 * Returns the animal stage that this represents.
	 *
	 * @return The animal stage
	 */
	public AnimalStage getAnimalStage() {
		return animal;
	}

	/**
	 * Returns the number of animals in this group.
	 *
	 * @return The number of animals
	 */
	public int getCount() {
		return count;
	}

	/**
	 * Returns the time remaining before the next developmental stage.
	 *
	 * @return The time remaining before development.
	 */
	public double getDevelopmentTimeRemaining() {
		return dev_time_rem;
	}
	
	public void score(ScoringInterface iface) {
		animal.score(iface, count);
	}
}
