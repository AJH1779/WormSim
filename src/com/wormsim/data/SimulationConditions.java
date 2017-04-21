/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wormsim.data;

import com.wormsim.animals.AnimalZoo;
import com.wormsim.utils.Utils;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import org.apache.commons.math3.distribution.IntegerDistribution;
import org.apache.commons.math3.distribution.RealDistribution;

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

	public static SimulationConditions read(String str)
					throws IOException {
		RealDistribution food = null;
		HashMap<Integer, RealDistribution> pheromones = new HashMap<>();
		HashMap<String, IntegerDistribution> groups = new HashMap<>();

		if (Utils.MULTIBRACKET_VALIDITY_PATTERN.matcher(str).matches()) {
			Matcher m = Utils.SAMPLER_PATTERN.matcher(str);
			while (m.find()) {
				String match = m.group();
				String[] keyvalue = match.split("~");
				if (keyvalue[0].matches("\\s*food\\s*")) {
					food = Utils.readRealDistribution(keyvalue[1].trim());
				} else if (keyvalue[0].matches("\\s*pheromone\\[\\d+\\]\\s*")) {
					int leftbracket = keyvalue[0].indexOf('[') + 1;
					int rightbracket = keyvalue[0].indexOf(']');
					int id = 0;
					try {
						id = Integer.valueOf(keyvalue[0].substring(leftbracket,
										rightbracket));
						if (id < 0) {
							throw new IOException("Invalid pheromone reference " + id
											+ ", must be positive!");
						}
					} catch (NumberFormatException ex) {
						throw new IOException(ex);
					}
					if (pheromones.putIfAbsent(id, Utils.readRealDistribution(
									keyvalue[1].trim())) != null) {
						throw new IOException("Duplicate pheromone id " + id);
					}
				} else { // Group Distribution
					groups.put(keyvalue[0].trim(), Utils.readIntegerDistribution(
									keyvalue[1]
													.trim()));
				}
			}
		} else {
			throw new IOException(
							"Brackets are missing on simulation conditions definition.");
		}
		if (food == null || groups.isEmpty()) {
			throw new IOException("Incomplete Data! Missing food or groups.");
		}

		// Convert pheromones into array
		Optional<Integer> max = pheromones.keySet().stream().max(Integer::max);

		RealDistribution[] pheromone_arr = new RealDistribution[max.orElse(0)];
		pheromones.forEach((k, v) -> pheromone_arr[k - 1] = v);
		for (int i = 0; i < pheromone_arr.length; i++) {
			if (pheromone_arr[i] == null) {
				pheromone_arr[i] = Utils.ZERO_REAL_DISTRIBUTION;
			}
		}

		return new SimulationConditions(food, pheromone_arr, groups);
	}

	public SimulationConditions(RealDistribution p_food_dist,
															RealDistribution[] p_pheromone_dists,
															HashMap<String, IntegerDistribution> p_group_dists) {
		this.food_dist = p_food_dist;
		this.pheromone_dists = Collections.unmodifiableList(Arrays.asList(
						p_pheromone_dists));
		// TODO: Replicate the group to protect the map using clone
		@SuppressWarnings("unchecked")
		Map<String, IntegerDistribution> clone
						= (Map<String, IntegerDistribution>) p_group_dists.clone();
		this.group_dists = Collections.unmodifiableMap(clone);
	}

	public final RealDistribution food_dist;

	public final Map<String, IntegerDistribution> group_dists;

	public final List<RealDistribution> pheromone_dists;

	/**
	 * Returns a collection containing a sample of the animal groups to use.
	 *
	 * @param zoo The zoo from which to sample.
	 *
	 * @return A sampled collection of animals.
	 */
	public GroupDistribution getGroupDistribution(AnimalZoo zoo) {
		return new GroupDistribution(zoo, group_dists);
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("{").append(System.lineSeparator());
		b.append("\tfood ~ ").append(Utils.realDistributionToString(food_dist));
		b.append(System.lineSeparator());
		for (int i = 0; i < pheromone_dists.size(); i++) {
			b.append("\tpheromone[").append(i).append("] ~ ")
							.append(Utils.realDistributionToString(pheromone_dists.get(i)))
							.append(System.lineSeparator());
		}
		group_dists.forEach((k, v) -> {
			b.append("\t").append(k).append(" ~ ")
							.append(Utils.integerDistributionToString(v))
							.append(System.lineSeparator());
		});
		b.append("}").append(System.lineSeparator());
		return b.toString();
	}
}
