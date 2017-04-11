/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wormsim.data;

import com.wormsim.animals.AnimalZoo;
import com.wormsim.animals.AnimalGroup;
import com.wormsim.animals.AnimalStage;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Logger;
import org.apache.commons.math3.distribution.BinomialDistribution;
import org.apache.commons.math3.distribution.EnumeratedIntegerDistribution;
import org.apache.commons.math3.distribution.IntegerDistribution;
import org.apache.commons.math3.distribution.UniformIntegerDistribution;

/**
 *
 * @author ah810
 */
public class GroupDistribution {
	private static final Logger LOG = Logger.getLogger(GroupDistribution.class
					.getName());

	/**
	 * Returns a string representation of the provided distribution. TODO: Make
	 * this complete TODO: Make this compatible with custom distributions (or just
	 * more complex ones).
	 *
	 * @param dist The distribution to translate
	 *
	 * @return The distribution as a string.
	 */
	public static String realDistributionToString(IntegerDistribution dist) {
		if (dist instanceof EnumeratedIntegerDistribution) {
			return Double.toString(dist.getNumericalMean());
		} else if (dist instanceof UniformIntegerDistribution) {
			return "Uniform(" + dist.getSupportLowerBound() + "," + dist
							.getSupportUpperBound() + ")";
		} else if (dist instanceof BinomialDistribution) {
			BinomialDistribution dist2 = (BinomialDistribution) dist;
			return "Binomial(" + dist2.getNumberOfTrials() + "," + dist2
							.getProbabilityOfSuccess() + ")";
		} else {
			throw new RuntimeException("Provided the wrong distribution! Gave " + dist
							.getClass().getName());
		}
	}

	/**
	 * Returns the distribution associated with the specified string. See the
	 * handbook for details of what is accepted. Or the code...
	 *
	 * @param str The string representing a distribution
	 *
	 * @return The distribution
	 */
	public static IntegerDistribution stringToIntegerDistribution(String str) {
		if (str.matches("[0-9]+(.[0-9]*)?")) { // I.E. a number
			return new EnumeratedIntegerDistribution(new int[]{Integer.valueOf(str)});
		} else {
			int index = str.indexOf('(');
			String prefix = str.substring(0, index - 1);
			switch (prefix) {
				case "B":
				case "Binom":
				case "Binomial": {
					int comma_index = str.indexOf(',', index);
					return new BinomialDistribution(Integer.valueOf(str
									.substring(index, comma_index - 1)),
									Double.valueOf(str.substring(comma_index).trim()));
				}
				case "U":
				case "Uni":
				case "Uniform": {
					int comma_index = str.indexOf(',', index);
					return new UniformIntegerDistribution(Integer.valueOf(str
									.substring(index, comma_index - 1)),
									Integer.valueOf(str.substring(comma_index).trim()));
				}
				default: {
					throw new IllegalArgumentException(
									"Unrecognised distribution form, see handbook for details. "
									+ "Provided \"" + str + "\".");
				}
			}
		}

	}

	/**
	 * Creates a new group distribution using the provided zoo as the basis and
	 * the data structure for forming the sampling distributions. Unfeatured
	 * groups in the
	 *
	 * @param data A map of the stages to their initial distributions.
	 */
	public GroupDistribution(HashMap<String, String> data) {
		dists = new HashMap<>(data.size());
		data.forEach((k, v) -> {
			dists.put(k, stringToIntegerDistribution(v));
		});
	}
	private final HashMap<String, IntegerDistribution> dists;

	/**
	 * Provides a sample of the groups to be used taken from the provided zoo.
	 *
	 * @param zoo
	 *
	 * @return
	 */
	public Collection<AnimalGroup> sample(AnimalZoo zoo) {
		HashSet<AnimalGroup> groups = new HashSet<>(dists.size());
		dists.forEach((k, v) -> groups.add(new AnimalGroup(zoo.getAnimalStage(k), v
						.sample())));
		return groups;
	}
}
