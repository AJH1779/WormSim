/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wormsim.simulation;

import com.wormsim.animals.AnimalZoo2;
import com.wormsim.animals.AnimalZoo2Instance;
import com.wormsim.data.GroupDistribution;
import com.wormsim.tracking.TrackedCalculation;
import com.wormsim.tracking.TrackedCalculationInstance;
import com.wormsim.utils.Utils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;

/**
 * A tracked configuration of modifiable parameters which iterates according to
 * the Metropolis-Hastings algorithm.
 *
 * TODO: Serialization
 *
 * @author ah810
 * @version 0.0.1
 */
public class Walker implements Serializable {
	private static final Logger LOG = Logger.getLogger(Walker.class.getName());
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new walker using the default <code>JDKRandomGenerator</code> for
	 * the random number generator class with the specified seed.
	 *
	 * @param seed               The walker seed
	 * @param zoo                The animal zoo
	 * @param fitness            The fitness tracker
	 * @param tracked_quantities Quantities tracked in the simulation.
	 */
	public Walker(long seed, AnimalZoo2 zoo,
								TrackedCalculation fitness,
								ArrayList<TrackedCalculation> tracked_quantities) {
		this(JDKRandomGenerator.class, seed, zoo, fitness, tracked_quantities);
	}

	/**
	 * Creates a new walker using the specified class of random number generator
	 * and the provided seed. Note there is a requirement that the provided random
	 * number generator is {@link java.io.Serializable}.
	 *
	 * @param cls                The random number generator class
	 * @param seed               The seed
	 * @param zoo                The animal zoo to use
	 * @param fitness            The fitness tracker
	 * @param tracked_quantities Quantities tracked in the simulation.
	 *
	 * @throws IllegalArgumentException If the provided generator does not have a
	 *                                  empty argument constructor or the
	 *                                  constructor is illegal to access.
	 */
	public Walker(Class<? extends RandomGenerator> cls,
								long seed,
								AnimalZoo2 zoo,
								TrackedCalculation fitness,
								ArrayList<TrackedCalculation> tracked_quantities)
					throws IllegalArgumentException {
		try {
			if (!Serializable.class.isAssignableFrom(cls)) {
				throw new IllegalArgumentException(
								"The provided class must be serializable.");
			}
			this.rng = cls.newInstance();
			this.seed = seed;
			this.rng.setSeed(seed);
			this.zoo = zoo.generate(rng);
			this.fitness = fitness.generate(rng);
			this.tracked_quantities = tracked_quantities.stream()
							.map((v) -> v.generate(rng))
							.collect(Collectors.toCollection(ArrayList::new));
			this.tracked_quantities.add(this.fitness);
		} catch (InstantiationException | IllegalAccessException ex) {
			Logger.getLogger(Walker.class.getName()).log(Level.SEVERE, null, ex);
			throw new IllegalArgumentException(ex);
		}
	}

	/**
	 * For serialization - to be replaced?
	 *
	 * @param rng                The random number generator to use.
	 * @param seed               The initial seed number (unused but important for
	 *                           recording).
	 * @param zoo                The zoo to use.
	 * @param fitness            The fitness tracker
	 * @param tracked_quantities Quantities tracked in the simulation.
	 */
	protected Walker(RandomGenerator rng,
									 long seed,
									 AnimalZoo2 zoo,
									 TrackedCalculation fitness,
									 ArrayList<TrackedCalculation> tracked_quantities) {
		this.rng = rng;
		this.seed = seed;
		this.zoo = zoo.generate(rng);
		this.fitness = fitness.generate(rng);
		this.tracked_quantities = tracked_quantities.stream()
						.map((v) -> v.generate(rng))
						.collect(Collectors.toCollection(ArrayList::new));
		this.tracked_quantities.add(this.fitness);
	}
	private boolean initialised = false;
	private final long seed;
	public final RandomGenerator rng;
	public final AnimalZoo2Instance zoo;
	final TrackedCalculationInstance fitness;
	transient GroupDistribution init_group_dists;
	transient SimulationThread thread;
	// private final HashMap<String, TracedDouble> scores = new HashMap<>(16);
	final ArrayList<TrackedCalculationInstance> tracked_quantities;

	/**
	 * Performs a metropolis-hastings check.
	 */
	public void check() {
		// fitness.set(scores);
		if (fitness.getPrevious() != 0.0 && rng.nextDouble() > fitness.get()
						/ fitness.getPrevious()) {
			zoo.revert();
			tracked_quantities.stream().forEach((v) -> v.revert());
		} else {
			zoo.retain();
			tracked_quantities.stream().forEach((v) -> v.retain());
		}
	}

	/**
	 * Alters the current configuration.
	 */
	public void evolve() {
		// TODO: Evolving other characteristics?
		zoo.evolve(rng);
	}

	public void initialise() {
		zoo.initialise(rng);
		this.initialised = true;
	}

	public boolean isInitialised() {
		return this.initialised;
	}

	public String toStateString() {
		return Stream.concat(Stream.of(zoo), this.tracked_quantities.stream()).map(
						(v) -> v.toCurrentValueString()).collect(Utils.TAB_JOINING);
	}
}
