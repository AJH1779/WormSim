/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wormsim.data;

import com.wormsim.animals.AnimalZoo;
import com.wormsim.animals.AnimalGroup;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
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
	 * @param str The distribution as a string.
	 *
	 * @return
	 */
	public static GroupDistribution stringToGroupDistribution(String str) {
		throw new UnsupportedOperationException("NOT YET IMPLEMENTED");
	}

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
			String prefix = str.substring(0, index - 1);
			switch (prefix) {
				case "N":
				case "Norm":
				case "Normal": {
					int comma_index = str.indexOf(',', index);
					return new NormalDistribution(Double.valueOf(str
									.substring(index, comma_index - 1)),
									Double.valueOf(str.substring(comma_index).trim()));
				}
				case "U":
				case "Uni":
				case "Uniform": {
					int comma_index = str.indexOf(',', index);
					return new UniformRealDistribution(Double.valueOf(str
									.substring(index, comma_index - 1)),
									Double.valueOf(str.substring(comma_index).trim()));
				}
				default:
					throw new IllegalArgumentException(
									"Unrecognised distribution form, see handbook for details. "
									+ "Provided \"" + str + "\".");
			}
		}

	}
//
//	/**
//	 * Creates a new simulation object using the specified distributions.
//	 *
//	 * @param food_dist       The food distribution to use.
//	 * @param pheromone_dists The pheromone distributions to use.
//	 * @param group_dist      The group distribution to use.
//	 *
//	 * @deprecated Although not really as will be made useful later.
//	 */
//	public SimulationConditions(RealDistribution food_dist,
//															RealDistribution[] pheromone_dists,
//															GroupDistribution group_dist) {
//		this.food_dist = food_dist;
//		this.pheromone_dists = new RealDistribution[pheromone_dists.length];
//		this.group_dist = group_dist;
//		System.arraycopy(this.pheromone_dists, 0, pheromone_dists, 0,
//						pheromone_dists.length);
//	}
//
//	/**
//	 * Creates a new simulation object based on the names of distributions
//	 * provided.
//	 *
//	 * @param food_dist       The food distribution as a string.
//	 * @param pheromone_dists The pheromone distributions as strings.
//	 * @param group_dist      The group distribution as a string.
//	 */
//	public SimulationConditions(String food_dist, String[] pheromone_dists,
//															String group_dist) {
//		this.food_dist = stringToRealDistribution(food_dist);
//		this.pheromone_dists = new RealDistribution[pheromone_dists.length];
//		for (int i = 0; i < pheromone_dists.length; i++) {
//			this.pheromone_dists[i] = stringToRealDistribution(pheromone_dists[i]);
//		}
//		this.group_dist = stringToGroupDistribution(group_dist);
//	}

	/**
	 * Creates a new simulation object based on the text from a block in
	 * "input.txt".
	 *
	 * @param data The data as a map
	 */
	public SimulationConditions(HashMap<String, String> data) {
		this.food_dist = stringToRealDistribution(data.get("food"));
		int max = data.keySet().stream().filter((str) -> str.matches("pheromone.*"))
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
							this.pheromone_dists[ref] = stringToRealDistribution(value);
						});

		this.group_dist = stringToGroupDistribution(data.get("groups"));
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
		out.write("food ~ ");
		out.write(realDistributionToString(food_dist));
		out.newLine();
		for (int i = 0; i < pheromone_dists.length; i++) {
			out.write("pheromone[" + i + "] ~ ");
			out.write(realDistributionToString(food_dist));
			out.newLine();
		}
		// TODO: Sample Groups
		out.write("}");
	}
}
