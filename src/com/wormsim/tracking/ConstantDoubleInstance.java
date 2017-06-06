/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wormsim.tracking;

import java.util.logging.Logger;
import org.apache.commons.math3.random.RandomGenerator;

/**
 *
 * @author ah810
 */
public class ConstantDoubleInstance extends TrackedDoubleInstance {
	private static final Logger LOG = Logger.getLogger(
					ConstantDoubleInstance.class.getName());

	ConstantDoubleInstance(ConstantDouble p_const) {
		super(p_const);
	}

	@Override
	public void checkpoint() {

	}

	@Override
	public ConstantDoubleInstance copy() {
		return this;
	}

	@Override
	public void discard() {
	}

	@Override
	public void evolve(RandomGenerator p_rng) {
	}

	@Override
	public double get() {
		return ((ConstantDouble) parent).value;
	}

	@Override
	public double getMean() {
		return ((ConstantDouble) parent).value;
	}

	@Override
	public double getOverallMean() {
		return ((ConstantDouble) parent).value;
	}

	@Override
	public double getOverallSquareMean() {
		return Math.pow(((ConstantDouble) parent).value, 2);
	}

	@Override
	public double getOverallVariance() {
		return 0.0;
	}

	@Override
	public double getPrevious() {
		return ((ConstantDouble) parent).value;
	}

	@Override
	public double getRecentMean() {
		return ((ConstantDouble) parent).value;
	}

	@Override
	public double getRecentSquareMean() {
		return Math.pow(((ConstantDouble) parent).value, 2);
	}

	@Override
	public double getRecentVariance() {
		return 0.0;
	}

	@Override
	public double getSquareMean() {
		return Math.pow(((ConstantDouble) parent).value, 2);
	}

	@Override
	public double getVariance() {
		return 0.0;
	}

	@Override
	public void initialise(RandomGenerator p_rng) {

	}

	@Override
	public void retain() {

	}

	@Override
	public void revert() {

	}

}
