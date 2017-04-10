/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wormsim.animals;

import com.wormsim.data.TrackedValue;
import java.util.HashMap;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * An object which houses the definitions of the various species used for a
 * simulation.
 *
 * @author ah810
 * @version 0.0.1
 */
public class AnimalZoo implements TrackedValue {
	private static final Logger LOG = Logger.getLogger(AnimalZoo.class.getName());

	/**
	 * Creates a new empty zoo. It must be done this way.
	 */
	@SuppressWarnings("CollectionWithoutInitialCapacity")
	public AnimalZoo() {
		this(0);
	}

	AnimalZoo(int pheromone_count) {
		this.pheromone_count = pheromone_count;
		this.developments = new HashMap<>();
		this.stages = new HashMap<>();
		this.strains = new HashMap<>();
		this.tracked_values = new HashMap<>();
	}
	final HashMap<AnimalStage, AnimalDevelopment> developments;
	final int pheromone_count;
	final HashMap<String, AnimalStage> stages;
	final HashMap<String, AnimalStrain> strains;
	final HashMap<String, TrackedValue> tracked_values;

	/**
	 * Adds the provided animal development to the zoo so long as a development
	 * for the same animal stage has not already been added. Returns true if
	 * added, false otherwise.
	 *
	 * @param dev The development to add.
	 *
	 * @return True if added, false otherwise.
	 */
	private boolean addAnimalDevelopment(AnimalDevelopment dev) {
		return this.developments.putIfAbsent(dev.getPrevStage(), dev) == null;
	}

	/**
	 * Adds the provided animal stage so long as the strain it belongs to has also
	 * been registered and a stage with the same full name has not already been
	 * registered. Returns true if added, false otherwise.
	 *
	 * @param stage The stage to add to the zoo.
	 *
	 * @return True if added, false otherwise.
	 *
	 * @see AnimalStage#getFullName() For the key name
	 */
	private boolean addAnimalStage(AnimalStage stage) {
		return this.strains.containsValue(stage.getStrain()) && this.stages
						.putIfAbsent(stage.getFullName(), stage) == null;
	}

	/**
	 * Adds the provided animal strain to the zoo so long as a strain with the
	 * same name has not already been registered. Returns true if added, false
	 * otherwise.
	 *
	 * @param strain The strain to add.
	 *
	 * @return True if added, false otherwise.
	 */
	private boolean addAnimalStrain(AnimalStrain strain) {
		return this.strains.putIfAbsent(strain.getName(), strain) == null;
	}

	@Override
	public AnimalZoo copy() {
		return create(this.pheromone_count);
	}

	/**
	 * Creates an instance of this animal zoo.
	 *
	 * TODO: Make this more general by requiring the rules of the simulation.
	 *
	 * @param pheromone_no The number of pheromone channels to use.
	 *
	 * @return A new animal zoo.
	 */
	public AnimalZoo create(int pheromone_no) {
		AnimalZoo zoo = new AnimalZoo();
		strains.forEach((k, v) -> {
			zoo.addAnimalStrain(new AnimalStrain(v));
		});
		stages.forEach((k, v) -> {
			zoo.addAnimalStage(new AnimalStage(zoo.strains
							.get(v.getStrain().getName()), v));
		});
		developments.forEach((k, v) -> {
			zoo.addAnimalDevelopment(v.changeZoo(zoo));
		});
		// TrackedValues need to be added.
		return zoo;
	}

	/**
	 * Returns the animal stage with the same full name as the provided key, null
	 * otherwise.
	 *
	 * Note: Is case sensitive.
	 *
	 * @param key The full name of the animal stage to retrieve
	 *
	 * @return The named animal stage, or null.
	 *
	 * @see AnimalStage#getFullName() The full name key.
	 */
	public AnimalStage getAnimalStage(String key) {
		return this.stages.get(key);
	}

	/**
	 * Returns the animal strain with the same name as the provided key, null
	 * otherwise.
	 *
	 * Note: Is case sensitive.
	 *
	 * @param key
	 *
	 * @return
	 */
	public AnimalStrain getAnimalStrain(String key) {
		return this.strains.get(key);
	}

	@Override
	public void retain() {
		this.tracked_values.values().stream().forEach((v) -> v.retain());
	}

	@Override
	public void revert() {
		this.tracked_values.values().stream().forEach((v) -> v.revert());
	}

	/**
	 * A constructor for creating animal zoo objects.
	 */
	public static class Builder extends AnimalZoo {

		/**
		 * Creates a new builder for an empty zoo.
		 */
		public Builder() {

		}

		/**
		 * Adds the animal stage with the specified full name if it has not already
		 * been added and the parent strain has already been added.
		 *
		 * Note: Is case sensitive.
		 *
		 * @param name The full stage name
		 *
		 * @return True if added, false otherwise.
		 */
		public boolean addAnimalStage(String name) {
			Optional<AnimalStrain> strain = this.strains.values().stream().filter(
							(str) -> name.startsWith(str.getName())).findFirst();
			if (strain.isPresent() && !this.stages.containsKey(name)) {
				// TODO: Make this a correct statement.
				return this.stages.putIfAbsent(name, new AnimalStage(name.substring(
								strain.get().getName().length()), strain.get())) == null;
			} else {
				return false;
			}
		}

		/**
		 * Adds the specified strain to the zoo, so long a strain with the same name
		 * hasn't already been added.
		 *
		 * @param name The name of the strain
		 *
		 * @return True if added, false otherwise.
		 */
		public boolean addAnimalStrain(String name) {
			return super.addAnimalStrain(new AnimalStrain(name, 0));
		}

		/**
		 * Adds a new animal transition using the specified string which describes
		 * it accepts the four types as detailed in <code>AnimalDevelopment</code>.
		 *
		 * @param str The string denoting the transition.
		 *
		 * @return True if the transition was successfully added, false otherwise.
		 */
		public boolean addAnimalTransition(String str) {
			int index = str.indexOf('(');
			String prefix = str.substring(0, index - 1);
			String[] args = str.substring(index, str.length() - 2).split(",");
			AnimalStage from = getAnimalStage(args[0]);
			if (from.getAnimalDevelopment() != null) {
				return false;
			}
			// TODO: Require AnimalDevelopment strings for named classes
			switch (prefix.toLowerCase()) {
				case "branching": {
					AnimalStage to = getAnimalStage(args[1]);
					AnimalStage alt_to = getAnimalStage(args[2]);
					DevelopmentFunction decision = DevelopmentFunction.interpret(args[3]);
					AnimalDevelopment dev = new AnimalDevelopment.Branching(from, to,
									alt_to, decision);
					from.setAnimalDevelopment(dev);
					return true;
				}
				case "laying": {
					AnimalStage to = getAnimalStage(args[1]);
					AnimalStage egg_to = getAnimalStage(args[2]);
					DevelopmentFunction decision = DevelopmentFunction.interpret(args[3]);
					AnimalDevelopment dev = new AnimalDevelopment.Laying(from, to, egg_to,
									decision);
					from.setAnimalDevelopment(dev);
					return true;
				}
				case "linear": {
					AnimalStage to = getAnimalStage(args[1]);
					DevelopmentFunction decision = DevelopmentFunction.interpret(args[2]);
					AnimalDevelopment dev = new AnimalDevelopment.Linear(from, to,
									decision);
					from.setAnimalDevelopment(dev);
					return true;
				}
				case "scoring": {
					ScoringFunction decision = ScoringFunction.interpret(args[1]);
					AnimalDevelopment dev = new AnimalDevelopment.Scoring(from, decision);
					from.setAnimalDevelopment(dev);
					return true;
				}
				default:
					throw new IllegalArgumentException(
									"Unrecognised development choice, see handbook for details. "
									+ "Provided \"" + str + "\".");
			}
		}
	}
}
