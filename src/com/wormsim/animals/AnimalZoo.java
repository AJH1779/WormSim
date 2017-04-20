/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wormsim.animals;

import com.sun.istack.internal.NotNull;
import com.wormsim.data.TrackedValue;
import com.wormsim.utils.Context;
import com.wormsim.utils.StringFormula;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.logging.Logger;
import org.apache.commons.math3.random.RandomGenerator;

/**
 * An object which houses the definitions of the various species used for a
 * simulation.
 *
 * @author ah810
 * @version 0.0.1
 */
public class AnimalZoo implements TrackedValue {
	private static final Logger LOG = Logger.getLogger(AnimalZoo.class.getName());
	private static final long serialVersionUID = 1L;

	@NotNull
	public static AnimalZoo read(String p_str)
					throws IOException {
		throw new UnsupportedOperationException("NYI");
//		if (Utils.VALIDITY_PATTERN.matcher(str).matches()) {
//			AnimalZoo.Builder zoo = new AnimalZoo.Builder();
//
//			BasicContext context = Context.GLOBAL_CONTEXT.push();
//
//			// TODO: Ensure the {} are cropped off, currently this allows illegal formatting.
//			Matcher m = Utils.LINE_PATTERN.matcher(str);
//			while (m.find()) {
//				String mat = m.group();
//				if (Utils.STRAIN_PATTERN.matcher(mat).matches()) {
//					// Add the strain in.
//					// Get the key
//					Matcher m_skey = Utils.STRAIN_KEY_PATTERN.matcher(mat);
//					assert m_skey.find();
//					String strain_key = m_skey.group().trim();
//					zoo.addAnimalStrain(strain_key);
//					Matcher m_stage = Utils.STAGE_PATTERN.matcher(mat);
//					while (m_stage.find()) {
//						String stagestr = m_stage.group();
//						zoo.addAnimalStage(strain_key, stage_name);
//					}
//				} else {
//					// Must be equality.
//					// TODO: Interpret the value and use it for the context.
//				}
//			}
//
//			// TODO: Edit AnimalStage for pheromone number to be applicable here.
//			return zoo.create(0);
//		} else {
//			throw new IOException("Invalid format, must be enclosed within curly"
//							+ " brackets ({}).");
//		}
	}

	/**
	 * Creates a new empty zoo. It must be done this way.
	 */
	public AnimalZoo() {
		this(0);
	}

	/**
	 * Constructor for local use really.
	 *
	 * TODO: Modify this?
	 *
	 * @param p_pheromone_no
	 */
	AnimalZoo(int p_pheromone_no) {
		this.pheromone_count = p_pheromone_no;
		this.developments = new HashMap<>();
		this.stages = new HashMap<>();
		this.strains = new HashMap<>();
		this.tracked_values = new ArrayList<>();
	}
	final HashMap<AnimalStage, AnimalDevelopment> developments;
	final int pheromone_count;
	final HashMap<String, AnimalStage> stages;
	final HashMap<String, AnimalStrain> strains;
	final ArrayList<TrackedValue> tracked_values;

	/**
	 * Adds the provided animal development to the zoo so long as a development
	 * for the same animal stage has not already been added. Returns true if
	 * added, false otherwise.
	 *
	 * @param p_dev The development to add.
	 *
	 * @return True if added, false otherwise.
	 */
	public boolean addAnimalDevelopment(@NotNull AnimalDevelopment p_dev) {
		boolean flag = p_dev.getInvolvedStages().allMatch((s) -> s == null
						|| this.stages.containsValue(s)) && this.developments.putIfAbsent(
						p_dev.getPrevStage(), p_dev) == null;
		if (flag) {
			p_dev.getPrevStage().setAnimalDevelopment(p_dev);
			p_dev.getTrackedValues().forEach((v) -> tracked_values.add(v));
		} else {
			throw new AssertionError("Temporary Error, needs better handling.");
		}
		return flag;
	}

	/**
	 * Adds the provided animal stage so long as the strain it belongs to has also
	 * been registered and a stage with the same full name has not already been
	 * registered. Returns true if added, false otherwise.
	 *
	 * @param p_stage The stage to add to the zoo.
	 *
	 * @return True if added, false otherwise.
	 *
	 * @see AnimalStage#getFullName() For the key name
	 */
	public boolean addAnimalStage(@NotNull AnimalStage p_stage) {
		boolean flag = this.strains.containsValue(p_stage.getStrain())
						&& this.stages
										.putIfAbsent(p_stage.getFullName(), p_stage) == null;
		if (flag) {
			p_stage.getStrain().addStage(p_stage);
		}
		return flag;
	}

	/**
	 * Adds the provided animal strain to the zoo so long as a strain with the
	 * same name has not already been registered. Returns true if added, false
	 * otherwise.
	 *
	 * @param p_strain The strain to add.
	 *
	 * @return True if added, false otherwise.
	 */
	public boolean addAnimalStrain(@NotNull AnimalStrain p_strain) {
		return this.strains.putIfAbsent(p_strain.getName(), p_strain) == null;
	}

	@Override
	@NotNull
	public AnimalZoo.Immutable copy() {
		return create(this.pheromone_count);
	}

	/**
	 * Creates an instance of this animal zoo.
	 *
	 * TODO: Make this more general by requiring the rules of the simulation.
	 *
	 * @param p_pheromone_no The number of pheromone channels to use.
	 *
	 * @return A new animal zoo.
	 */
	@NotNull
	public AnimalZoo.Immutable create(int p_pheromone_no) {
		AnimalZoo.Immutable zoo = new AnimalZoo.Immutable();
		strains.forEach((k, v) -> {
			zoo.strains.put(k, new AnimalStrain(v, p_pheromone_no));
		});
		stages.forEach((k, v) -> {
			zoo.stages.put(k,
							new AnimalStage(zoo.strains.get(v.getStrain().getName()), v));

		});
		developments.forEach((k, v) -> {
			AnimalDevelopment dev = v.changeZoo(zoo);
			zoo.developments.put(dev.getPrevStage(), dev);
			dev.getPrevStage().setAnimalDevelopment(dev);
			dev.getTrackedValues().forEach((a) -> zoo.tracked_values.add(a));
		});
		return zoo;
	}

	@Override
	public void evolve(@NotNull RandomGenerator p_rng) {
		this.tracked_values.forEach((v) -> v.evolve(p_rng));
	}

	/**
	 * Returns the animal stage with the same full name as the provided key, null
	 * otherwise.
	 *
	 * Note: Is case sensitive.
	 *
	 * @param p_key The full name of the animal stage to retrieve
	 *
	 * @return The named animal stage, or null.
	 *
	 * @see AnimalStage#getFullName() The full name key.
	 */
	@NotNull
	public AnimalStage getAnimalStage(@NotNull String p_key) {
		return this.stages.get(p_key);
	}

	/**
	 * Returns the animal strain with the same name as the provided key, null
	 * otherwise.
	 *
	 * Note: Is case sensitive.
	 *
	 * @param p_key
	 *
	 * @return
	 */
	@NotNull
	public AnimalStrain getAnimalStrain(@NotNull String p_key) {
		return this.strains.get(p_key);
	}

	@Override
	public void initialise(@NotNull RandomGenerator p_rng) {
		this.tracked_values.stream().forEach((v) -> v.initialise(p_rng));
	}

	@Override
	public void retain() {
		this.tracked_values.stream().forEach((v) -> v.retain());
	}

	@Override
	public void revert() {
		this.tracked_values.stream().forEach((v) -> v.revert());
	}

	@Override
	public void writeToStream(@NotNull ObjectOutputStream p_out)
					throws IOException {
		for (TrackedValue v : tracked_values) {
			v.writeToStream(p_out);
		}
	}

	@Override
	public void writeToWriter(@NotNull BufferedWriter p_out)
					throws IOException {
		for (TrackedValue v : tracked_values) {
			v.writeToWriter(p_out);
		}
	}

	@Override
	public String toString() {
		return "ANIMAL ZOO NYI";
	}

	@Override
	public String toCurrentValueString() {
		StringBuilder b = new StringBuilder();
		tracked_values.forEach((v) -> {
			b.append(v.toCurrentValueString());
		});
		return b.toString();
	}

	@Override
	public String toHeaderString() {
		StringBuilder b = new StringBuilder();
		tracked_values.forEach((v) -> {
			b.append(v.toHeaderString());
		});
		return b.toString();
	}

	/**
	 * A constructor for creating animal zoo objects.
	 */
	public static final class Builder extends AnimalZoo {
		private static final long serialVersionUID = 1L;

		/**
		 * Creates a new builder for an empty zoo.
		 */
		public Builder() {

		}

		/**
		 *
		 * @param in
		 * @param line_no
		 *
		 * @throws IOException
		 */
		public Builder(BufferedReader in, int[] line_no)
						throws IOException {
			Context.BasicContext context = Context.GLOBAL_CONTEXT.clone();
			line_no[0]++;
			for (String line = in.readLine(); line != null; line = in
							.readLine().trim(), line_no[0]++) {
				// Ignore comment lines.
				if (line.startsWith("#")) {
					continue;
				}
				// Filter based on pre-defined keywords.
				if (line.startsWith("strain ")) {
					if (!line.endsWith("{")) {
						throw new IOException(
										"Unable to read line, does not end in {: " + line);
					}
					String strain = line.substring(7, line.length() - 2)
									.trim();
					addAnimalStrain(strain);
					for (line = in.readLine(); line != null; line = in
									.readLine().trim(), line_no[0]++) {
						if (line.startsWith("#")) {
							continue;
						}
						if (line.startsWith("stage ")) {
							addAnimalStage(strain, line.substring(6).trim());
						} else if (line.startsWith("dev ")) {
							addAnimalTransition(line.substring(4).trim());
						} else if (!line.isEmpty()) {
							throw new IOException("Could not interpret the line: "
											+ line);
						}
						if (line.endsWith("}")) {
							break;
						}
					}
				} else if (line.startsWith("}")) {
					break;
				} else if (line.contains("=")) {
					// Define a constant
					int equals = line.indexOf('=');
					context.addVariable(line.substring(0, equals - 1), StringFormula
									.evaluate(line.substring(equals), context));
				} else if (line.contains("~")) {
					throw new IOException("Distributions are not yet implemented: "
									+ line);
				} else if (!line.isEmpty()) {
					throw new IOException("Could not interpret the line: "
									+ line);
				}
				if (line == null) {
					throw new IOException(
									"Reached end of file prematurely within strain "
									+ "block!");
				}
				if (line.endsWith("}")) {
					break;
				}
			}
		}

		/**
		 * Adds the animal stage for the specified strain and the specified name of
		 * the stage.
		 *
		 * Note: Is case sensitive.
		 *
		 * @param p_strain The name of the strain
		 * @param p_name   The name of the stage
		 *
		 * @return True if added, false otherwise.
		 */
		public boolean addAnimalStage(String p_strain, String p_name) {
			Optional<AnimalStrain> strain = this.strains.values().stream().filter(
							(str) -> str.getName().equals(p_strain)).findFirst();
			if (strain.isPresent() && !this.stages.containsKey(p_name)) {
				// TODO: Make this a correct statement.
				return this.stages.putIfAbsent(p_name, new AnimalStage(p_name.substring(
								strain.get().getName().length()), strain.get())) == null;
			} else {
				return false;
			}
		}

		/**
		 * Adds the specified strain to the zoo, so long a strain with the same name
		 * hasn't already been added.
		 *
		 * @param p_name The name of the strain
		 *
		 * @return True if added, false otherwise.
		 */
		public boolean addAnimalStrain(String p_name) {
			return super.addAnimalStrain(new AnimalStrain(p_name, 0));
		}

		/**
		 * Adds a new animal transition using the specified string which describes
		 * it accepts the four types as detailed in <code>AnimalDevelopment</code>.
		 *
		 * @param p_str The string denoting the transition.
		 *
		 * @return True if the transition was successfully added, false otherwise.
		 */
		public boolean addAnimalTransition(String p_str) {
			int index = p_str.indexOf('(');
			String prefix = p_str.substring(0, index - 1);
			String[] args = p_str.substring(index, p_str.length() - 2).split(",");
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
									+ "Provided \"" + p_str + "\".");
			}
		}
	}

	/**
	 * An implementation of the AnimalZoo that does not allow modification of the
	 * animal strains and stages registered to it.
	 *
	 * TODO: Enforce registration of animal strain and stage relations through the
	 * zoo rather than separately.
	 */
	public static class Immutable extends AnimalZoo {
		private static final long serialVersionUID = 1L;

		Immutable() {

		}

		/**
		 * Throws <code>UnsupportedOperationException</code>.
		 *
		 * @return
		 */
		@Override
		public boolean addAnimalDevelopment(AnimalDevelopment dev) {
			throw new UnsupportedOperationException(
							"Cannot alter an immutable animal zoo.");
		}

		/**
		 * Throws <code>UnsupportedOperationException</code>.
		 *
		 * @return
		 */
		@Override
		public boolean addAnimalStage(AnimalStage p_stage) {
			throw new UnsupportedOperationException(
							"Cannot alter an immutable animal zoo.");
		}

		/**
		 * Throws <code>UnsupportedOperationException</code>.
		 *
		 * @return
		 */
		@Override
		public boolean addAnimalStrain(AnimalStrain p_strain) {
			throw new UnsupportedOperationException(
							"Cannot alter an immutable animal zoo.");
		}
	}
}
