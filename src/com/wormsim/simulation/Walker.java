/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wormsim.simulation;

import com.wormsim.animals.AnimalZoo;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Serializable;
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
	public Walker(long seed, AnimalZoo zoo) {
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
	public Walker(Class<? extends RandomGenerator> cls, long seed, AnimalZoo zoo)
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
	protected Walker(RandomGenerator rng, long seed, AnimalZoo zoo) {
		this.zoo = zoo;
		this.rng = rng;
		this.seed = seed;
	}
	private double current_fitness = 0.0;
	private double prev_fitness = 0.0;
	private final RandomGenerator rng;
	private final long seed;
	private transient SimulationThread thread;
	private final AnimalZoo zoo;

	/**
	 * Performs a metropolis-hastings check.
	 */
	public void check() {
		if (rng.nextDouble() > current_fitness / prev_fitness) {
			current_fitness = prev_fitness;

			// TODO: If any more objects contain tracked values, edit here.
			zoo.revert();
		} else {
			prev_fitness = current_fitness;
			zoo.retain();
		}
	}

	/**
	 * Called when this walker is no longer being propagated by the thread.
	 */
	public void dropThread() {
		thread = null;
	}

	/**
	 * Alters the current configuration.
	 */
	public void evolve() {
		// TODO: Evolve - based on the zoo.
		zoo.evolve(rng);
	}

	/**
	 * Returns the random number generator for this walker.
	 *
	 * @return The random number generator
	 */
	public RandomGenerator getRNG() {
		return rng;
	}

	/**
	 * Returns the seed used for this walker.
	 *
	 * @return The seed.
	 */
	public long getSeed() {
		return seed;
	}

	/**
	 * Returns the zoo that this walker is using.
	 *
	 * @return
	 */
	public AnimalZoo getZoo() {
		return zoo;
	}

	/**
	 * Sets the thread used by this walker to the provided object. Should only be
	 * called by the thread being given.
	 *
	 * @param thread The thread walking this walker.
	 */
	public void giveThread(SimulationThread thread) {
		this.thread = thread;
	}

	/**
	 * Writes the state of this walker out to the provided write as a
	 * human-readable format.
	 *
	 * @param out The writer.
	 *
	 * @throws IOException If an exception is thrown whilst writing.
	 */
	public void writeToWriter(BufferedWriter out)
					throws IOException {
		zoo.writeToWriter(out);
	}
}
