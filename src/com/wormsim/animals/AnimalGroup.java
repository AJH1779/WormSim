/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wormsim.animals;

import com.wormsim.simulation.SimulationThread.ConsumeInterface;
import com.wormsim.simulation.SimulationThread.DevelopmentInterface;
import java.util.logging.Logger;
import org.apache.commons.math3.random.RandomGenerator;

/**
 * Denotes a group of animals with identical qualities.
 *
 * @author ah810
 * @version 0.0.1
 */
public class AnimalGroup implements Comparable {
	private static final Logger LOG = Logger
					.getLogger(AnimalGroup.class.getName());

	/**
	 * Creates a new group of animals at the specified stage.
	 *
	 * @param animal The animal stage the creatures are at
	 * @param count  The number of animals
	 */
	public AnimalGroup(AnimalStage2Instance animal, int count) {
		this(animal, count, animal.dev_time.get());
	}

	/**
	 * Creates a new group of animals at the specified stage, overriding the
	 * development time with the specified amount.
	 *
	 * @param animal   The animal stage the creatures are at
	 * @param count    The number of animals
	 * @param dev_time The time until the next development stage
	 */
	public AnimalGroup(AnimalStage2Instance animal, int count, double dev_time) {
		this.animal = animal;
		this.count = count;
		this.dev_time_rem = dev_time;
	}
	private final AnimalStage2Instance animal;
	private final int count;
	private double dev_time_rem;

	/**
	 * Note: The natural ordering of this class foregoes the natural declaration
	 * of equals(), or something like that.
	 *
	 * @param p_o
	 *
	 * @return
	 */
	@Override
	public int compareTo(Object p_o) {
		if (p_o instanceof AnimalGroup) {
			AnimalGroup that = (AnimalGroup) p_o;
			return that.dev_time_rem > this.dev_time_rem
							? -1
							: (that.dev_time_rem < this.dev_time_rem
											? 1
											: (that.animal.hashCode() - this.animal.hashCode()));
		} else {
			throw new ClassCastException("Invalid Class: " + p_o.getClass());
		}
	}

	/**
	 * Consumes food over the specified time period.
	 *
	 * @param iface The consumer interface
	 * @param delt  The time elapsed
	 */
	public void consumeAndEmit(ConsumeInterface iface, double delt) {
		iface.eatFood(getCount() * getAnimalStage().food_rate.get()
						* delt);
		for (int j = 0; j < iface.getPheromoneNumber(); j++) {
			iface.emitPheromone(getCount()
							* getAnimalStage().pheromone_rates[j].get() * delt, j);
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
		animal.development.develop(iface, count, rng);
	}

	/**
	 * Returns the animal stage that this represents.
	 *
	 * @return The animal stage
	 */
	public AnimalStage2Instance getAnimalStage() {
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

	/*
	public void score(ScoringInterface iface) {
		animal.score(iface, count);
	}*/
}
