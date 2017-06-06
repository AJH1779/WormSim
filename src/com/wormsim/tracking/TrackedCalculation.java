/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wormsim.tracking;

import com.wormsim.animals.AnimalGroup;
import com.wormsim.data.SimulationOptions;
import com.wormsim.simulation.SimulationThread.SamplingInterface;
import org.apache.commons.math3.random.RandomGenerator;

public abstract class TrackedCalculation extends ChangingDouble {
	public TrackedCalculation(String p_name, SimulationOptions p_ops) {
		super(p_name, p_ops);
	}

	@Override
	public TrackedCalculationInstance generate(RandomGenerator p_rng) {
		return generate(initialise(p_rng));
	}

	@Override
	public TrackedCalculationInstance generate(double init_value) {
		init();
		return new TrackedCalculationInstance(this, init_value, this.iteration_count);
	}

	protected abstract double added(SamplingInterface iface, AnimalGroup group,
																	double prev_value);

	protected abstract double end(SamplingInterface p_iface,
																double p_prev_value);

	protected abstract double ended(SamplingInterface iface, AnimalGroup group,
																	double prev_value);

	@Override
	protected double evolve(double p_val, RandomGenerator p_rng) {
		return p_val;
	}

	protected abstract double removed(SamplingInterface iface, AnimalGroup group,
																		double prev_value);

}
