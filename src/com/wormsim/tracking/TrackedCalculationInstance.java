/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wormsim.tracking;

import com.wormsim.animals.AnimalGroup;
import com.wormsim.simulation.SimulationThread.SamplingInterface;
import java.util.logging.Logger;
import java.util.stream.DoubleStream;

/**
 *
 * @author ah810
 */
public class TrackedCalculationInstance extends ChangingDoubleInstance {
	private static final Logger LOG = Logger.getLogger(
					TrackedCalculationInstance.class.getName());

	TrackedCalculationInstance(TrackedCalculation p_parent,
														 double p_init_value, int iters) {
		super(p_parent, p_init_value);
		this.iter_data = new double[iters];
	}
	final double[] iter_data;
	int pointer = 0;

	public void added(SamplingInterface iface, AnimalGroup group) {
		iter_data[pointer] = ((TrackedCalculation) parent).added(iface, group,
						iter_data[pointer]);
	}

	/**
	 * Called at the beginning of each iteration.
	 */
	public void begin() {
		iter_data[pointer] = 0.0;
	}

	public void end(SamplingInterface iface) {
		iter_data[pointer] = ((TrackedCalculation) parent).end(iface,
						iter_data[pointer]);
		pointer++;
	}

	public void ended(SamplingInterface iface, AnimalGroup group) {
		iter_data[pointer] = ((TrackedCalculation) parent).ended(iface, group,
						iter_data[pointer]);
	}

	public void finish() {
		this.current_value = DoubleStream.of(iter_data).average().getAsDouble();
		this.pointer = 0;
	}

	public void removed(SamplingInterface iface, AnimalGroup group) {
		iter_data[pointer] = ((TrackedCalculation) parent).removed(iface, group,
						iter_data[pointer]);
	}
}
