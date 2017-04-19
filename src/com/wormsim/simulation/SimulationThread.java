/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wormsim.simulation;

import com.wormsim.animals.AnimalGroup;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * An object containing a thread that iterates through the walkers contained in
 * a Simulation object. This is where the simulation itself is contained and
 * executed.
 *
 * @author ah810
 * @version 0.0.1
 */
public class SimulationThread implements Runnable {
	private static final Logger LOG = Logger.getLogger(SimulationThread.class
					.getName());

	/**
	 * Creates a new thread for the specified simulation using the provided deque
	 * of walkers to iterate through.
	 *
	 * @param sim     The simulation
	 * @param walkers The walker deque the thread will walk.
	 */
	SimulationThread(Simulation sim, LinkedBlockingDeque<Walker> walkers) {
		this.sim = sim;
		this.walkers = walkers;

		this.pheromones = new double[sim.getOptions().getPheromoneNumber()];
	}
	private final ConsumeInterface con_interface = new ConsumeInterface(this);
	private final DevelopmentInterface dev_interface = new DevelopmentInterface(
					this);
	private double food;
	private final TreeSet<AnimalGroup> groups = new TreeSet<>();
	private final double[] pheromones;
	private final SamplingInterface sampling_interface = new SamplingInterface(
					this);
	private final HashMap<String, Double> scores = new HashMap<>(16);
	private final ScoringInterface scoring_interface = new ScoringInterface(this);
	private final Simulation sim;
	private volatile Thread thread;
	private double time;
	private final LinkedBlockingDeque<Walker> walkers;

	/**
	 * Returns the simulation state of this thread back to a sample of the initial
	 * conditions.
	 */
	private void reset(Walker walker) {
		food = sim.getInitialConditions().sampleFoodDistribution();
		for (int i = 0; i < pheromones.length; i++) {
			pheromones[i] = sim.getInitialConditions().samplePheromoneDistribution(i);
		}
		groups.clear();
		groups.addAll(sim.getInitialConditions().sampleGroups(walker.getZoo()));
	}

	public boolean isAlive() {
		return thread.isAlive();
	}

	@Override
	public void run() {
		while (sim.isRunning()) {
			try {
				// WARNING: Arbitrary timeout, should this be adjusted?
				Walker walker = walkers.poll(1000, TimeUnit.MILLISECONDS);
				if (walker != null) {
					if (!walker.isInitialised()) {
						walker.initialise();
					}
					walker.giveThread(this);
					walker.evolve();
					run(walker);
					walker.check();
					walker.dropThread();
				}
			} catch (InterruptedException ex) {
				// Requires a check to see whether it should be ending the while loop.
				// Could instead try using the older threading thing from my prior
				// project. SynchroCore!
			}
		}
	}

	/**
	 * Runs the specified walker using Ordinary Monte Carlo to determine the
	 * fitness factor of the parameter configuration.
	 *
	 * @param walker The walker to run with.
	 */
	public void run(Walker walker) {
		thread = Thread.currentThread();
		scores.clear();
		for (int i = 0; i < sim.getOptions().getAssayIterationNumber(); i++) {
			reset(walker);
			time = 0.0;
			while (food > 0.0 && !groups.isEmpty()) {
				AnimalGroup group = groups.first();

				final double delt = group.getDevelopmentTimeRemaining();
				time += delt;

				groups.forEach((g) -> {
					g.consumeAndEmit(con_interface, delt);
				});

				if (food < 0.0) {
					break;
				}

				groups.remove(group);
				group.develop(dev_interface, walker.getRNG());
			}
			// Scoring with the remaining groups.
			groups.forEach((g) -> {
				g.score(scoring_interface);
			});
		}
		double inv_num = 1.0 / sim.getOptions().getAssayIterationNumber();
		walker.recordScores(scores, inv_num);
	}

	/**
	 * Starts the thread. The thread should be started this way to have consistent
	 * settings, but it does not necessarily matter.
	 */
	void start() {
		new Thread(this).start();
	}

	/**
	 * The interface accessible by animals that are progressing through time but
	 * not undergoing a developmental change. Does not provide data about the
	 * system to enforce the analytic approach taken for the time between
	 * transitions.
	 */
	public static class ConsumeInterface {

		private ConsumeInterface(SimulationThread thread) {
			this.thread = thread;
		}
		private final SimulationThread thread;

		/**
		 * Removes the specified amount of food from the system.
		 *
		 * @param delf The amount to remove.
		 */
		public void eatFood(double delf) {
			thread.food -= delf;
		}

		/**
		 * Adds the specified amount of pheromone to the system.
		 *
		 * @param delp The amount to add
		 * @param ref  The pheromone reference
		 */
		public void emitPheromone(double delp, int ref) {
			if (ref >= 0 && ref < thread.pheromones.length) {
				thread.pheromones[ref] += delp;
			}
		}

		/**
		 * Returns the number of pheromones that may be used in the system. The
		 * pheromone references allowed are 0 to (1 - the returned value).
		 *
		 * @return The number of pheromone channels
		 */
		public int getPheromoneNumber() {
			return thread.pheromones.length;
		}
	}

	/**
	 * The interface used by animal stages when the stage is undergoing a
	 * developmental change. Allows details of the system to be acquired and
	 * additions to the groups in the system to be made. Does not allow alteration
	 * of pheromone or food quantities.
	 */
	public static class DevelopmentInterface {

		private DevelopmentInterface(SimulationThread thread) {
			this.thread = thread;
		}
		private final SimulationThread thread;

		/**
		 * Adds the specified group to the system. Note this will fail if the
		 * provided group is null or contains a non-positive number of animals.
		 *
		 * @param ag The group to add
		 *
		 * @return True if the group was added.
		 */
		public boolean addGroup(AnimalGroup ag) {
			if (ag == null || ag.getCount() < 1) {
				return false;
			}
			return thread.groups.add(ag);
		}

		/**
		 * Adds the specified score to the system.
		 *
		 * @param ref The key for the score
		 * @param del The value of the score.
		 *
		 * @return True if added (always true)
		 */
		public boolean addScore(String ref, double del) {
			if (thread.scores.containsKey(ref)) {
				thread.scores.put(ref, thread.scores.get(ref) + del);
			} else {
				thread.scores.put(ref, del);
			}
			return true;
		}

		/**
		 * Returns the amount of food in the system.
		 *
		 * @return The amount of food.
		 */
		public double getFood() {
			return thread.food;
		}

		/**
		 * Returns the amount of pheromone in the system.
		 *
		 * @param ref The pheromone reference
		 *
		 * @return The amount of pheromone
		 */
		public double getPheromone(int ref) {
			if (ref < 0 || ref >= thread.pheromones.length) {
				return 0.0;
			} else {
				return thread.pheromones[ref];
			}
		}

		/**
		 * Returns the number of pheromones that may be used in the system. The
		 * pheromone references allowed are 0 to (1 - the returned value).
		 *
		 * @return The number of pheromone channels
		 */
		public int getPheromoneNumber() {
			return thread.pheromones.length;
		}

		/**
		 * Returns a more restrictive interface which only allows visible parameters
		 * of the system to be checked.
		 *
		 * @return The sampling interface.
		 */
		public SamplingInterface getSamplingInterface() {
			return thread.sampling_interface;
		}

		/**
		 * Returns a more restrictive interface which allows for scores to be
		 * manipulated and visible parameters of the system to be checked.
		 *
		 * @return The scoring interface
		 */
		public ScoringInterface getScoringInterface() {
			return thread.scoring_interface;
		}

		/**
		 * Returns the time the system has been running for.
		 *
		 * @return The run time.
		 */
		public double getTime() {
			return thread.time;
		}

	}

	/**
	 * The interface used by animal stages when the stage is undergoing a
	 * developmental change. Allows details of the system to be acquired and
	 * additions to the groups in the system to be made. Does not allow alteration
	 * of pheromone or food quantities.
	 */
	public static class SamplingInterface {

		private SamplingInterface(SimulationThread thread) {
			this.thread = thread;
		}
		private final SimulationThread thread;

		/**
		 * Returns the amount of food in the system.
		 *
		 * @return The amount of food.
		 */
		public double getFood() {
			return thread.food;
		}

		/**
		 * Returns the amount of pheromone in the system.
		 *
		 * @param ref The pheromone reference
		 *
		 * @return The amount of pheromone
		 */
		public double getPheromone(int ref) {
			if (ref < 0 || ref >= thread.pheromones.length) {
				return 0.0;
			} else {
				return thread.pheromones[ref];
			}
		}

		/**
		 * Returns the number of pheromones that may be used in the system. The
		 * pheromone references allowed are 0 to (1 - the returned value).
		 *
		 * @return The number of pheromone channels
		 */
		public int getPheromoneNumber() {
			return thread.pheromones.length;
		}

		/**
		 * Returns the time the system has been running for.
		 *
		 * @return The run time.
		 */
		public double getTime() {
			return thread.time;
		}
	}

	/**
	 * The interface used by animal groups at the end of the simulation. Allows
	 * details of the system to be acquired and alterations to scores to be made.
	 */
	public static class ScoringInterface {

		private ScoringInterface(SimulationThread thread) {
			this.thread = thread;
		}
		private final SimulationThread thread;

		/**
		 * Adds the specified score to the system.
		 *
		 * @param ref The key for the score
		 * @param del The value of the score.
		 *
		 * @return True if added (always true)
		 */
		public boolean addScore(String ref, double del) {
			if (thread.scores.containsKey(ref)) {
				thread.scores.put(ref, thread.scores.get(ref) + del);
			} else {
				thread.scores.put(ref, del);
			}
			return true;
		}

		/**
		 * Returns the amount of food in the system.
		 *
		 * @return The amount of food.
		 */
		public double getFood() {
			return thread.food;
		}

		/**
		 * Returns the amount of pheromone in the system.
		 *
		 * @param ref The pheromone reference
		 *
		 * @return The amount of pheromone
		 */
		public double getPheromone(int ref) {
			if (ref < 0 || ref >= thread.pheromones.length) {
				return 0.0;
			} else {
				return thread.pheromones[ref];
			}
		}

		/**
		 * Returns the number of pheromones that may be used in the system. The
		 * pheromone references allowed are 0 to (1 - the returned value).
		 *
		 * @return The number of pheromone channels
		 */
		public int getPheromoneNumber() {
			return thread.pheromones.length;
		}

		/**
		 * Returns a more restrictive interface which only allows visible parameters
		 * of the system to be checked.
		 *
		 * @return The sampling interface.
		 */
		public SamplingInterface getSamplingInterface() {
			return thread.sampling_interface;
		}

		/**
		 * Returns the time the system has been running for.
		 *
		 * @return The run time.
		 */
		public double getTime() {
			return thread.time;
		}

	}
}
