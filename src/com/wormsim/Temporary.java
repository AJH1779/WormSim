/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wormsim;

import com.wormsim.animals.AnimalDevelopment;
import com.wormsim.animals.AnimalStage;
import com.wormsim.animals.AnimalStrain;
import com.wormsim.animals.AnimalZoo;
import com.wormsim.data.TrackedDevelopmentFunction;
import com.wormsim.simulation.SimulationThread;
import com.wormsim.utils.Utils;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.logging.Logger;
import org.apache.commons.math3.distribution.BinomialDistribution;
import org.apache.commons.math3.random.RandomGenerator;

/**
 *
 * @author ah810
 */
public class Temporary {
	private static final Logger LOG = Logger.getLogger(Temporary.class.getName());

	/**
	 * A generic default animal zoo.
	 */
	public static final AnimalZoo CODED_ANIMAL_ZOO = newCodedAnimalZooInstance();

	private static AnimalZoo newCodedAnimalZooInstance() {
		AnimalZoo zoo = new AnimalZoo();
		AnimalStrain strain = new AnimalStrain("TestStrain", 0);
		AnimalStage[] stages = {
			new AnimalStage("L1", strain, 1.0, 1.0, 1.0),
			new AnimalStage("L2", strain, 1.0, 1.0, 1.0),
			new AnimalStage("L2d", strain, 1.0, 1.0, 1.0),
			new AnimalStage("Dauer", strain, 0.0, 0.0, 0.0)
		};
		AnimalDevelopment[] devs = {
			new AnimalDevelopment.Branching(stages[0], stages[1], stages[2],
			new DauerBranchDevFunction1()),
			new AnimalDevelopment.Laying(stages[1], null, stages[0],
			(p_iface, p_count, p_rng) -> {
				return p_count * 100;
			}),
			new AnimalDevelopment.Linear(stages[2], stages[3],
			(p_iface, p_count, p_rng) -> {
				return p_count;
			}),
			new AnimalDevelopment.Scoring(stages[3], (p_iface, p_count) -> {
				p_iface.addScore(stages[3].getFullName(), p_count);
			})
		};
		zoo.addAnimalStrain(strain);
		for (AnimalStage teststage : stages) {
			zoo.addAnimalStage(teststage);
		}
		for (AnimalDevelopment dev : devs) {
			zoo.addAnimalDevelopment(dev);
		}
		return zoo.copy();
	}

	public static void main(String[] args) {
	}

	private Temporary() {
	}

	private static class DauerBranchDevFunction1 implements
					TrackedDevelopmentFunction {
		private static final long serialVersionUID = 1L;

		DauerBranchDevFunction1() {
			for (int i = 0; i < values.length; i++) {
				values[i] = new TrackedDouble(0.0);
			}
		}
		private final TrackedDouble[] values = new TrackedDouble[1];

		@Override
		public int applyAsInt(SimulationThread.SamplingInterface p_iface,
													int p_count, RandomGenerator p_rng) {
			double prob = Utils.logistic(values[0].get());
			return new BinomialDistribution(p_rng, p_count, prob).sample();
		}

		@Override
		public TrackedDecisionFunction copy() {
			DauerBranchDevFunction1 that = new DauerBranchDevFunction1();
			for (int i = 0; i < values.length; i++) {
				that.values[i] = this.values[i].copy();
			}
			return (TrackedDecisionFunction) that;
		}

		@Override
		public void evolve(RandomGenerator p_rng) {
			for (TrackedDouble value : values) {
				value.evolve(p_rng);
			}
		}

		@Override
		public void initialise(RandomGenerator p_rng) {
			for (TrackedDouble value : values) {
				value.initialise(p_rng);
			}
		}

		@Override
		public void retain() {
			for (TrackedDouble value : values) {
				value.retain();
			}
		}

		@Override
		public void revert() {
			for (TrackedDouble value : values) {
				value.revert();
			}
		}

		@Override
		public void writeToStream(ObjectOutputStream p_out)
						throws IOException {
			for (TrackedDouble value : values) {
				value.writeToStream(p_out);
			}
		}

		@Override
		public void writeToWriter(BufferedWriter p_out)
						throws IOException {
			for (TrackedDouble value : values) {
				value.writeToWriter(p_out);
			}
		}
	}

}
