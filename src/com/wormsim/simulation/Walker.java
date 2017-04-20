/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wormsim.simulation;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.wormsim.animals.AnimalZoo;
import com.wormsim.data.GroupDistribution;
import com.wormsim.data.TrackedValue.TrackedDouble;
import java.io.Serializable;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
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
	 * @param seed The walker seed
	 * @param zoo  The animal zoo
	 */
	public Walker(long seed, @NotNull AnimalZoo zoo) {
		this(JDKRandomGenerator.class, seed, zoo);
	}

	/**
	 * Creates a new walker using the specified class of random number generator
	 * and the provided seed. Note there is a requirement that the provided random
	 * number generator is {@link java.io.Serializable}.
	 *
	 * @param cls  The random number generator class
	 * @param seed The seed
	 * @param zoo  The animal zoo to use
	 *
	 * @throws IllegalArgumentException If the provided generator does not have a
	 *                                  empty argument constructor or the
	 *                                  constructor is illegal to access.
	 */
	public Walker(@NotNull Class<? extends RandomGenerator> cls,
								long seed,
								@NotNull AnimalZoo zoo)
					throws IllegalArgumentException {
		try {
			if (!Serializable.class.isAssignableFrom(cls)) {
				throw new IllegalArgumentException(
								"The provided class must be serializable.");
			}
			this.zoo = zoo;
			this.rng = cls.newInstance();
			this.seed = seed;
			this.rng.setSeed(seed);
		} catch (InstantiationException | IllegalAccessException ex) {
			Logger.getLogger(Walker.class.getName()).log(Level.SEVERE, null, ex);
			throw new IllegalArgumentException(ex);
		}
	}

	/**
	 * For serialization - to be replaced?
	 *
	 * @param rng  The random number generator to use.
	 * @param seed The initial seed number (unused but important for recording).
	 * @param zoo  The zoo to use.
	 */
	protected Walker(@NotNull RandomGenerator rng,
									 long seed,
									 @NotNull AnimalZoo zoo) {
		this.zoo = zoo;
		this.rng = rng;
		this.seed = seed;
	}
	private double current_fitness = 0.0;
	private boolean initialised = false;
	private double prev_fitness = 0.0;
	@NotNull
	private final HashMap<String, TrackedDouble> scores = new HashMap<>(16);
	private final long seed;
	@Nullable
	public transient GroupDistribution init_group_dists;
	@NotNull
	public final RandomGenerator rng;
	@Nullable
	public transient SimulationThread thread;
	@NotNull
	public final AnimalZoo zoo;

	/**
	 * Performs a metropolis-hastings check.
	 */
	public void check() {
		current_fitness = scores
						.getOrDefault("TestStrain Dauer", TrackedDouble.ZERO)
						.get();
		if (prev_fitness != 0.0 && rng.nextDouble() > current_fitness / prev_fitness) {
			current_fitness = prev_fitness;
			// TODO: If any more objects contain tracked values, edit here.
			zoo.revert();
		} else {
			prev_fitness = current_fitness;
			zoo.retain();
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

	@Override
	@NotNull
	public String toString() {
		StringBuilder b = new StringBuilder(zoo.toCurrentValueString());
		b.append(current_fitness);
		b.append(System.lineSeparator());
		return b.toString();
	}

	void recordScores(@NotNull HashMap<String, Double> p_map, double p_inv) {
		p_map.forEach((k, v) -> {
			if (scores.containsKey(k)) {
				scores.get(k).set(v * p_inv);
			} else {
				scores.put(k, new TrackedDouble(v * p_inv));
			}
		});
		scores.entrySet().stream().filter((e) -> !p_map.containsKey(e.getKey()))
						.forEach((e) -> e.getValue().set(0.0));
	}
}
