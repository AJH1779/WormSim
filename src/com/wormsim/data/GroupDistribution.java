/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wormsim.data;

import com.wormsim.animals.AnimalGroup;
import com.wormsim.animals.AnimalStage;
import com.wormsim.animals.AnimalStage2Instance;
import com.wormsim.animals.AnimalZoo;
import com.wormsim.animals.AnimalZoo2Instance;
import com.wormsim.utils.Utils;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.logging.Logger;
import org.apache.commons.math3.distribution.IntegerDistribution;

/**
 *
 * @author ah810
 */
public final class GroupDistribution {
	private static final Logger LOG = Logger.getLogger(GroupDistribution.class
					.getName());

	GroupDistribution(AnimalZoo2Instance p_zoo,
										Map<String, IntegerDistribution> p_group_dists) {
		dists = new HashMap<>(p_group_dists.size());
		p_group_dists.forEach((k, v) -> {
			dists.put(p_zoo.getAnimalStage(k), v);
		});
	}

	private final HashMap<AnimalStage2Instance, IntegerDistribution> dists;

	public void sample(TreeSet<AnimalGroup> p_groups) {
		dists.forEach((k, v) -> {
			p_groups.add(new AnimalGroup(k, v.sample()));
		});
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		dists.entrySet().forEach((dist) -> {
			b.append("\t").append(dist.getKey().toString()).append(" ~ ").append(
							Utils.integerDistributionToString(dist.getValue()))
							.append(System.lineSeparator());
		});
		return b.toString();
	}
}
