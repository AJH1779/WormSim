/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wormsim.tracking;

import org.apache.commons.math3.random.RandomGenerator;

public class ConstantDouble extends TrackedDouble<ConstantDoubleInstance> {
	public ConstantDouble(String p_name, double value) {
		super(p_name);
		this.value = value;
		this.inst = new ConstantDoubleInstance(this);
	}
	final ConstantDoubleInstance inst;
	final double value;

	@Override
	public void checkpoint() {
	}

	@Override
	public void discard() {

	}

	@Override
	public ConstantDoubleInstance generate(RandomGenerator p_rng) {
		return inst;
	}

	@Override
	public double getBetweenVariance() {
		return 0.0;
	}

	@Override
	public double getDataCount() {
		return Double.POSITIVE_INFINITY;
	}

	@Override
	public double getEffectiveDataCount() {
		return Double.POSITIVE_INFINITY;
	}

	@Override
	public double getMean() {
		return value;
	}

	@Override
	public double getPotentialScaleReduction() {
		return 0.0;
	}

	@Override
	public double getVariance() {
		return 0.0;
	}

	@Override
	public double getWithinVariance() {
		return 0.0;
	}

	/**
	 * Returns true if this is to be tracked visibly, false otherwise.
	 *
	 * @return
	 */
	@Override
	public boolean isVisiblyTracked() {
		return false;
	}

	@Override
	protected double evolve(double p_val, RandomGenerator p_rng) {
		return value;
	}

	@Override
	protected double initialise(RandomGenerator p_rng) {
		return value;
	}

}
