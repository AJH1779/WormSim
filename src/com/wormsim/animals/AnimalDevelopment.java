/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wormsim.animals;

import com.wormsim.data.TrackedDevelopmentFunction;
import com.wormsim.data.TrackedValue;
import com.wormsim.simulation.SimulationThread;
import java.util.stream.Stream;
import org.apache.commons.math3.random.RandomGenerator;

/**
 * An object denoting a developmental stage in the animal's life cycle.
 *
 * @author ah810
 * @version 0.0.1
 */
public abstract class AnimalDevelopment {

	/**
	 * Denotes a development from the specified animal stage.
	 *
	 * @param from The stage to develop from.
	 */
	public AnimalDevelopment(AnimalStage from) {
		this.prev = from;
	}
	private final AnimalStage prev;

	/**
	 * Creates a new animal development object that is tied to the specified zoo,
	 * throws a <code>NullPointerException</code> if the zoo does not contain the
	 * required named strains or stages.
	 *
	 * @param zoo The zoo to shift to.
	 *
	 * @return The new development.
	 *
	 * @throws NullPointerException If the zoo lacks required strains or stages.
	 */
	public abstract AnimalDevelopment changeZoo(AnimalZoo zoo)
					throws NullPointerException;

	/**
	 * Produces the groups associated with this developmental process and provides
	 * them via the <code>DevelopmentInterface</code> object to the simulation.
	 *
	 * @param iface The development interface
	 * @param count The number of animals developing
	 * @param rng   The random number generator
	 */
	public abstract void develop(SimulationThread.DevelopmentInterface iface,
															 int count, RandomGenerator rng);

	/**
	 * Returns a stream of the stages involved in this development step.
	 *
	 * @return
	 */
	public abstract Stream<AnimalStage> getInvolvedStages();

	/**
	 * Returns the animal stage the development originates from.
	 *
	 * @return
	 */
	public AnimalStage getPrevStage() {
		return prev;
	}

	public abstract Stream<TrackedValue> getTrackedValues();

	/**
	 * Denotes a developmental switch that produces two different stages, acting
	 * as a phenotypically plastic decision process.
	 */
	public static class Branching extends AnimalDevelopment {

		/**
		 * Creates a new branching stage moving from the specified stage to the
		 * target other stages. The decision function samples the number of
		 * alternative stage which are produced. The total number of animals remains
		 * unchanged after this development.
		 *
		 * @param prev     The stage emerging from
		 * @param next     The normal development stage
		 * @param alt_next The alternate development stage
		 * @param decision The decision function.
		 *
		 * @throws IllegalArgumentException If the provided stages are not of the
		 *                                  same strain.
		 */
		public Branching(AnimalStage prev, AnimalStage next, AnimalStage alt_next,
										 DevelopmentFunction decision)
						throws IllegalArgumentException {
			super(prev);
			if (prev.getStrain() != next.getStrain() || next.getStrain() != alt_next
							.getStrain()) {
				throw new IllegalArgumentException("Incompatible strains (from.strain="
								+ prev.getStrain().getName() + ", to.strain=" + next.getStrain()
								.getName() + ", alt_to.strain=" + alt_next.getStrain().getName()
								+ ")");
			}
			this.next = next;
			this.alt_next = alt_next;
			this.decision = decision;
		}
		private final AnimalStage alt_next;
		private final DevelopmentFunction decision;
		private final AnimalStage next;

		@Override
		public AnimalDevelopment.Branching changeZoo(AnimalZoo zoo) {
			return new AnimalDevelopment.Branching(
							zoo.getAnimalStage(getPrevStage().getFullName()),
							zoo.getAnimalStage(next.getFullName()),
							zoo.getAnimalStage(alt_next.getFullName()),
							(decision instanceof TrackedDevelopmentFunction
											? ((TrackedDevelopmentFunction) decision).copy()
											: decision)
			);
		}

		@Override
		public void develop(SimulationThread.DevelopmentInterface iface, int count,
												RandomGenerator rng) {
			int alt_count = decision.applyAsInt(iface.getSamplingInterface(), count,
							rng);
			int next_count = count - alt_count;
			if (next != null) {
				iface.addGroup(new AnimalGroup(next, next_count));
			}
			if (alt_next != null) {
				iface.addGroup(new AnimalGroup(alt_next, alt_count));
			}
		}

		/**
		 * Returns the alternative development stage for continuing this stage.
		 *
		 * @return The alternative development stage.
		 */
		public AnimalStage getAltStage() {
			return alt_next;
		}

		/**
		 * Returns the sampling function which denotes the number of alternative
		 * stage animals are produced at the development.
		 *
		 * @return The development function
		 */
		public DevelopmentFunction getDecisionFunction() {
			return decision;
		}

		@Override
		public Stream<AnimalStage> getInvolvedStages() {
			return Stream.of(this.getPrevStage(), next, alt_next);
		}

		/**
		 * Returns the next development stage.
		 *
		 * @return The next development stage
		 */
		public AnimalStage getNextStage() {
			return next;
		}

		@Override
		public Stream<TrackedValue> getTrackedValues() {
			if (decision instanceof TrackedValue) {
				return Stream.of((TrackedValue) decision);
			} else {
				return Stream.<TrackedValue>empty();
			}
		}

	}

	/**
	 * Denotes a development where additional animals are created with the parent
	 * continuing to a new stage.
	 */
	public static class Laying extends AnimalDevelopment {

		/**
		 * Creates a new development that denotes the laying of eggs or live
		 * progeny. The decision function samples the number of eggs which are laid.
		 *
		 * @param prev     The originating development stage
		 * @param next     The next parent stage
		 * @param egg_next The offspring stage
		 * @param decision The egg function
		 */
		public Laying(AnimalStage prev, AnimalStage next, AnimalStage egg_next,
									DevelopmentFunction decision) {
			super(prev);
			if (prev.getStrain() != egg_next.getStrain() || (next != null && next
							.getStrain() != egg_next.getStrain())) {
				throw new IllegalArgumentException("Incompatible strains (from.strain="
								+ prev.getStrain().getName() + ", to.strain=" + next.getStrain()
								.getName() + ", egg_to.strain=" + egg_next.getStrain().getName()
								+ ")");
			}
			this.next = next;
			this.egg_next = egg_next;
			this.decision = decision;
		}
		private final DevelopmentFunction decision;
		private final AnimalStage egg_next;
		private final AnimalStage next;

		@Override
		public AnimalDevelopment.Laying changeZoo(AnimalZoo zoo) {
			return new AnimalDevelopment.Laying(
							zoo.getAnimalStage(getPrevStage().getFullName()),
							next == null
											? null
											: zoo.getAnimalStage(next.getFullName()),
							zoo.getAnimalStage(egg_next.getFullName()),
							(decision instanceof TrackedDevelopmentFunction
											? ((TrackedDevelopmentFunction) decision).copy()
											: decision)
			);
		}

		@Override
		public void develop(SimulationThread.DevelopmentInterface iface, int count,
												RandomGenerator rng) {
			if (next != null) {
				iface.addGroup(new AnimalGroup(next, count));
			}
			if (egg_next != null) {
				iface.addGroup(new AnimalGroup(egg_next, decision
								.applyAsInt(iface.getSamplingInterface(), count, rng)));
			}
		}

		@Override
		public Stream<AnimalStage> getInvolvedStages() {
			return Stream.of(this.getPrevStage(), next, egg_next);
		}

		/**
		 * Returns the stage at which the progeny animals are laid or given birth
		 * to.
		 *
		 * @return The young stage
		 */
		public AnimalStage getLayStage() {
			return egg_next;
		}

		/**
		 * Sets the next development stage for the parents.
		 *
		 * @return The parent's new stage
		 */
		public AnimalStage getNextStage() {
			return next;
		}

		@Override
		public Stream<TrackedValue> getTrackedValues() {
			if (decision instanceof TrackedValue) {
				return Stream.of((TrackedValue) decision);
			} else {
				return Stream.<TrackedValue>empty();
			}
		}

	}

	/**
	 * Denotes an animal that progresses directly from this stage to another
	 * development stage.
	 */
	public static class Linear extends AnimalDevelopment {

		/**
		 * Creates a new development process that carries a group directly from its
		 * existing stage to another stage, using the specified decision function.
		 *
		 * @param from     The animal stage before
		 * @param next     The animal stage after
		 * @param decision The group size transformation function
		 */
		public Linear(AnimalStage from, AnimalStage next,
									DevelopmentFunction decision) {
			super(from);
			if (from.getStrain() != next.getStrain()) {
				throw new IllegalArgumentException("Incompatible strains (from.strain="
								+ from.getStrain().getName() + ", to.strain=" + next.getStrain()
								.getName() + ")");
			}
			this.next = next;
			this.decision = decision;
		}
		private final DevelopmentFunction decision;
		private final AnimalStage next;

		@Override
		public AnimalDevelopment.Linear changeZoo(AnimalZoo zoo) {
			return new AnimalDevelopment.Linear(
							zoo.getAnimalStage(getPrevStage().getFullName()),
							zoo.getAnimalStage(next.getFullName()),
							(decision instanceof TrackedDevelopmentFunction
											? ((TrackedDevelopmentFunction) decision).copy()
											: decision)
			);
		}

		@Override
		public void develop(SimulationThread.DevelopmentInterface iface, int count,
												RandomGenerator rng) {
			if (next != null) {
				iface.addGroup(new AnimalGroup(next, count));
			}
		}

		/**
		 * Returns the function that determines the number of a group to continue
		 * past this development stage.
		 *
		 * @return
		 */
		public DevelopmentFunction getContinuationFunction() {
			return decision;
		}

		@Override
		public Stream<AnimalStage> getInvolvedStages() {
			return Stream.of(this.getPrevStage(), next);
		}

		/**
		 * Returns the next stage of the development.
		 *
		 * @return The next stage.
		 */
		public AnimalStage getNextStage() {
			return next;
		}

		@Override
		public Stream<TrackedValue> getTrackedValues() {
			if (decision instanceof TrackedValue) {
				return Stream.of((TrackedValue) decision);
			} else {
				return Stream.<TrackedValue>empty();
			}
		}
	}

	/**
	 * An example scoring stage that does not progress the animal stage
	 * development but instead adds a time weighted score to the system.
	 */
	public static class Scoring extends AnimalDevelopment {

		/**
		 * Creates a new scoring development from which the stage does not continue
		 * but a score is added for this development stage as having been reached
		 * with some value.
		 *
		 * @param from     The stage originating from.
		 * @param decision The score making decision.
		 */
		public Scoring(AnimalStage from,
									 ScoringFunction decision) {
			super(from);
			this.decision = decision;
		}
		private final ScoringFunction decision;

		@Override
		public AnimalDevelopment.Scoring changeZoo(AnimalZoo zoo) {
			return new AnimalDevelopment.Scoring(
							zoo.getAnimalStage(getPrevStage().getFullName()),
							decision instanceof TrackedValue
											? (ScoringFunction) ((TrackedValue) decision).copy()
											: decision
			);
		}

		@Override
		public void develop(SimulationThread.DevelopmentInterface iface, int count,
												RandomGenerator rng) {
			decision.applyAsDouble(iface.getScoringInterface(), count);
		}

		@Override
		public Stream<AnimalStage> getInvolvedStages() {
			return Stream.of(this.getPrevStage());
		}

		/**
		 * Returns the function used for scoring this development.
		 *
		 * @return The scoring function.
		 */
		public ScoringFunction getScoringFunction() {
			return decision;
		}

		@Override
		public Stream<TrackedValue> getTrackedValues() {
			if (decision instanceof TrackedValue) {
				return Stream.of((TrackedValue) decision);
			} else {
				return Stream.<TrackedValue>empty();
			}
		}
	}
}
