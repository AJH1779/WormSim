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
import com.wormsim.data.SimulationCommands;
import com.wormsim.data.SimulationConditions;
import com.wormsim.data.SimulationOptions;
import com.wormsim.data.TrackedDevelopmentFunction;
import com.wormsim.data.TrackedDouble;
import com.wormsim.simulation.Simulation;
import com.wormsim.simulation.SimulationThread;
import com.wormsim.utils.Utils;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math3.distribution.BinomialDistribution;
import org.apache.commons.math3.distribution.ConstantRealDistribution;
import org.apache.commons.math3.distribution.EnumeratedIntegerDistribution;
import org.apache.commons.math3.distribution.IntegerDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.random.RandomGenerator;

/**
 *
 * @author ah810
 */
public class LaunchFromCodeMain {
	private static final Logger LOG = Logger.getLogger(LaunchFromCodeMain.class
					.getName());

	private static AnimalZoo makeCustomAnimalZoo() {
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
		AnimalZoo.Immutable copy = zoo.copy();
		copy.stopAffectingVariance();
		return copy;
	}

	private static SimulationConditions makeCustomInitialConditions() {
		HashMap<String, IntegerDistribution> map = new HashMap<>();

		map.put("TestStrain L2", new EnumeratedIntegerDistribution(new int[]{1}));

		return new SimulationConditions(
						new NormalDistribution(10000.0, 1000.0),
						new RealDistribution[]{
							new ConstantRealDistribution(0.0)
						},
						map
		);
	}

	public static void main(String[] args)
					throws IOException {
		SimulationCommands cmds = Utils.readCommandLine(args);
		SimulationOptions ops = new SimulationOptions(cmds);

		// Change options here.
		ops.animal_zoo.set(makeCustomAnimalZoo());
		ops.assay_iteration_no.set(100);
		ops.burn_in_no.set(50000);
		ops.record_no.set(100000);
		ops.checkpoint_no.set(10000);
		ops.detailed_data.set(Boolean.TRUE);
		ops.initial_conditions.set(makeCustomInitialConditions());
		ops.walker_no.set(1024);
		ops.pheromone_no.set(1);
		ops.forced_run.set(Boolean.TRUE);

		if (ops.isMissingParameters()) {
			String msg = "Missing Parameters: " + ops.getMissingParametersList();
			LOG.log(Level.SEVERE, msg);
			System.exit(-1);
		} else {
			new Simulation(ops).run();
		}
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
		public TrackedDevelopmentFunction copy() {
			DauerBranchDevFunction1 that = new DauerBranchDevFunction1();
			for (int i = 0; i < values.length; i++) {
				that.values[i] = this.values[i].copy();
			}
			return that;
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
		public boolean stopAffectingVariance() {
			return Arrays.stream(values).reduce(false, (a, b) -> a || b
							.stopAffectingVariance(), (a, b) -> a || b);
		}

		@Override
		public String toBetweenVarianceString() {
			return Arrays.stream(values).map((v) -> v.toBetweenVarianceString())
							.collect(Utils.TAB_JOINING);
		}

		@Override
		public String toCurrentValueString() {
			return Arrays.stream(values).map((v) -> v.toCurrentValueString())
							.collect(Utils.TAB_JOINING);
		}

		@Override
		public String toHeaderString() {
			return Arrays.stream(values).map((v) -> v.toHeaderString())
							.collect(Utils.TAB_JOINING);
		}

		@Override
		public String toPotentialScaleReductionString() {
			return Arrays.stream(values).map((v) -> v
							.toPotentialScaleReductionString())
							.collect(Utils.TAB_JOINING);
		}

		@Override
		public String toVarianceString() {
			return Arrays.stream(values).map((v) -> v.toVarianceString())
							.collect(Utils.TAB_JOINING);
		}

		@Override
		public String toWithinVarianceString() {
			return Arrays.stream(values).map((v) -> v.toWithinVarianceString())
							.collect(Utils.TAB_JOINING);
		}

	}

}
