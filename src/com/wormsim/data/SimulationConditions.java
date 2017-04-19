/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wormsim.data;

import com.wormsim.animals.AnimalGroup;
import com.wormsim.animals.AnimalZoo;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.logging.Logger;
import org.apache.commons.math3.distribution.ConstantRealDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;

/**
 * An object representing the initial conditions of the simulations as a
 * sampling object.
 *
 * @author ah810
 * @version 0.0.1
 */
public class SimulationConditions {

	private static final Logger LOG = Logger.getLogger(SimulationConditions.class
					.getName());

	public static String groupDistributionToString(GroupDistribution dist) {
		throw new UnsupportedOperationException("NOT YET IMPLEMENTED");
	}

	public static SimulationConditions read(String str)
					throws IOException {
		throw new UnsupportedOperationException("NOT YET IMPLEMENTED");
	}

	/**
	 * Returns a string representation of the provided distribution. TODO: Make
	 * this complete TODO: Make this compatible with custom distributions (or just
	 * more complex ones).
	 *
	 * @param dist The distribution to translate
	 *
	 * @return The distribution as a string.
	 */
	public static String realDistributionToString(RealDistribution dist) {
		if (dist instanceof ConstantRealDistribution) {
			return Double.toString(dist.getNumericalMean());
		} else if (dist instanceof UniformRealDistribution) {
			return "Uniform(" + dist.getSupportLowerBound() + "," + dist
							.getSupportUpperBound() + ")";
		} else if (dist instanceof NormalDistribution) {
			NormalDistribution dist2 = (NormalDistribution) dist;
			return "Normal(" + dist2.getMean() + "," + dist2.getStandardDeviation()
							+ ")";
		} else {
			throw new RuntimeException("Provided the wrong distribution! Gave " + dist
							.getClass().getName());
		}
	}

	// TODO: Revise the quality of these javadocs!
	/**
	 * Returns the distribution associated with the specified string. See the
	 * handbook for details of what is accepted. Or the code...
	 *
	 * @param str The string representing a distribution
	 *
	 * @return The distribution
	 */
	public static RealDistribution stringToRealDistribution(String str) {
		if (str.matches("[0-9]+(.[0-9]*)?")) { // I.E. a number
			return new ConstantRealDistribution(Double.valueOf(str));
		} else {
			int index = str.indexOf('(');
			String prefix = str.substring(0, index).toLowerCase(Locale.ROOT);
			switch (prefix) {
				case "n":
				case "norm":
				case "normal": {
					int comma_index = str.indexOf(',', index);
					return new NormalDistribution(Double.valueOf(str
									.substring(index + 1, comma_index).trim()),
									Double.valueOf(str
													.substring(comma_index + 1, str.length() - 2).trim()));
				}
				case "u":
				case "uni":
				case "uniform": {
					int comma_index = str.indexOf(',', index);
					return new UniformRealDistribution(Double.valueOf(str
									.substring(index + 1, comma_index - 1)),
									Double.valueOf(str.substring(comma_index).trim()));
				}
				default:
					throw new IllegalArgumentException(
									"Unrecognised distribution form, see handbook for details. "
									+ "Provided \"" + str + "\".");
			}
		}

	}

	/**
	 * Constructor based on the simulation options.
	 *
	 * TODO: Make this much more reasonable.
	 *
	 * @param in
	 * @param line_no
	 *
	 * @throws IOException
	 */
	public SimulationConditions(BufferedReader in, int line_no[])
					throws IOException {
		line_no[0]++;
		HashMap<String, String> data = new HashMap<>(16);
		for (String line = in.readLine(); line != null; line = in
						.readLine(), line_no[0]++) {
			if (line.contains("~")) {
				int index2 = line.indexOf('~');
				String key2 = line.substring(0, index2).trim();
				String entry2 = line.substring(index2 + 1).trim();
				data.put(key2, entry2);
			}
		}
		this.food_dist = stringToRealDistribution(data.get("food"));

		int max = data.keySet().stream().filter((str) -> str
						.startsWith("pheromone"))
						.reduce(0, (ai, b) -> {
							int bi = Integer.valueOf(b.substring(b.indexOf('[') + 1, b
											.indexOf(']')).trim());
							return Math.max(ai, bi);
						}, (ai, bi) -> Math.max(ai, bi));
		this.pheromone_dists = new RealDistribution[max];
		data.entrySet().stream().filter((e) -> e.getKey().startsWith("pheromone"))
						.forEach((e) -> {
							String key = e.getKey();
							String value = e.getValue().trim();
							int ref = Integer.valueOf(key.substring(key.indexOf('[') + 1, key
											.indexOf(']')).trim());
							this.pheromone_dists[ref - 1] = stringToRealDistribution(value);
						});

		this.group_dist = new GroupDistribution(data);
	}

	/**
	 * Creates a new simulation object based on the text from a block in
	 * "input.txt".
	 *
	 * @param data The data as a map
	 */
	public SimulationConditions(HashMap<String, String> data) {
		this.food_dist = stringToRealDistribution(data.get("food"));
		int max = data.keySet().stream().filter((str) -> str
						.matches("pheromone.*"))
						.reduce(Integer.MIN_VALUE, (ai, b) -> {
							int bi = Integer.valueOf(b.substring(b.indexOf('['), b
											.indexOf(']') - 1).trim());
							return Math.max(ai, bi);
						}, (ai, bi) -> Math.max(ai, bi));
		this.pheromone_dists = new RealDistribution[max];
		data.entrySet().stream().filter((e) -> e.getKey().matches("pheromone.*"))
						.forEach((e) -> {
							String key = e.getKey();
							String value = e.getValue().trim();
							int ref = Integer.valueOf(key.substring(key.indexOf('['), key
											.indexOf(']') - 1).trim());
							this.pheromone_dists[ref - 1] = stringToRealDistribution(value);
						});

		this.group_dist = new GroupDistribution(data);
	}

	private final RealDistribution food_dist;
	private final GroupDistribution group_dist;
	private final RealDistribution[] pheromone_dists;

	/**
	 * Provides a value for the initial food quantities by sampling a food
	 * distribution.
	 *
	 * @return
	 */
	public double sampleFoodDistribution() {
		return food_dist.sample();
	}

	/**
	 * Returns a collection containing a sample of the animal groups to use.
	 *
	 * @param zoo The zoo from which to sample.
	 *
	 * @return A sampled collection of animals.
	 */
	public Collection<AnimalGroup> sampleGroups(AnimalZoo zoo) {
		return group_dist.sample(zoo);
	}

	/**
	 * Returns a value for the initial pheromone quantities by sampling the
	 * indicated pheromone distribution.
	 *
	 * @param i The pheromone channel to sample
	 *
	 * @return
	 */
	public double samplePheromoneDistribution(int i) {
		return pheromone_dists[i].sample();
	}

	/**
	 * Writes to the provided BufferedWriter a string representation of the
	 * distributions in this object.
	 *
	 * @param out
	 *
	 * @throws IOException
	 */
	public void write(BufferedWriter out)
					throws IOException {
		out.write("{");
		out.newLine();
		out.write("\tfood ~ ");
		out.write(realDistributionToString(food_dist));
		out.newLine();
		for (int i = 0; i < pheromone_dists.length; i++) {
			out.write("\tpheromone[" + i + "] ~ ");
			out.write(realDistributionToString(food_dist));
			out.newLine();
		}
		group_dist.write(out);
		out.write("}");
	}
}
