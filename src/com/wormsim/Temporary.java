/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wormsim;

import com.wormsim.animals.AnimalDevelopment2;
import com.wormsim.animals.AnimalGroup;
import com.wormsim.animals.AnimalStage2;
import com.wormsim.animals.AnimalStage2Instance;
import com.wormsim.animals.AnimalStrain2;
import com.wormsim.animals.AnimalZoo2;
import com.wormsim.simulation.SimulationThread;
import com.wormsim.tracking.ChangingDouble;
import com.wormsim.tracking.ConstantDouble;
import com.wormsim.tracking.TrackedDouble;
import com.wormsim.tracking.TrackedDoubleInstance;
import com.wormsim.utils.Utils;
import java.util.logging.Logger;
import org.apache.commons.math3.distribution.BinomialDistribution;
import org.apache.commons.math3.random.RandomGenerator;

/**
 * This is a temporary class holding data that should be moved or removed later
 * on.
 *
 * @author ah810
 */
public class Temporary {
//	private static final Logger LOG = Logger.getLogger(Temporary.class.getName());
//
//	/**
//	 * A generic default animal zoo.
//	 */
//	public static final AnimalZoo2 CODED_ANIMAL_ZOO = newCodedAnimalZooInstance();
//
//	private static AnimalZoo2 newCodedAnimalZooInstance(SimulationOptions ops) {
//		AnimalZoo2 zoo = new AnimalZoo2();
//		AnimalStrain2 strain = new AnimalStrain2(zoo, "TestStrain");
//		AnimalStage2[] stages = {
//			new AnimalStage2("L1", strain, new ConstantDouble("Food", 1.0),
//			new ConstantDouble("Dev", 1.0),
//			new ConstantDouble[]{new ConstantDouble("Phero1", 1.0)}),
//			new AnimalStage2("L2", strain, new ConstantDouble("Food", 1.0),
//			new ConstantDouble("Dev", 1.0),
//			new ConstantDouble[]{new ConstantDouble("Phero1", 1.0)}),
//			new AnimalStage2("L2d", strain, new ConstantDouble("Food", 1.0),
//			new ConstantDouble("Dev", 1.0),
//			new ConstantDouble[]{new ConstantDouble("Phero1", 1.0)}),
//			new AnimalStage2("Dauer", strain, new ConstantDouble("Food", 0.0),
//			new ConstantDouble("Dev", 0.0),
//			new ConstantDouble[]{new ConstantDouble("Phero1", 0.0)})
//		};
//		AnimalDevelopment2[] devs = {
//			new LaunchFromCodeMain.DauerDevelopment(ops, stages[0], stages[1], stages[2]),
//			new AnimalDevelopment2.ExplosiveLaying(stages[1],
//			stages[0], new ConstantDouble("Fecundity", 100.0)),
//			new AnimalDevelopment2.Linear(stages[2], stages[3]),
//			new LaunchFromCodeMain.DauerScoring(stages[3])
//		};
//		return zoo;
//	}
//
//	private Temporary() {
//	}
//
//	public static class DauerDevelopment extends AnimalDevelopment2 {
//		private static final long serialVersionUID = 1L;
//
//		public DauerDevelopment(AnimalStage2 actor,
//														AnimalStage2 repro,
//														AnimalStage2 dauer) {
//			super(actor, new ChangingDouble[]{
//				new ChangingDouble("DD A") {
//					@Override
//					protected double evolve(double p_val, RandomGenerator p_rng) {
//						return p_rng.nextGaussian() * 0.05;
//					}
//
//					@Override
//					protected double initialise(RandomGenerator p_rng) {
//						return p_rng.nextGaussian() * 3.0;
//					}
//				},
//				new ChangingDouble("DD B") {
//					@Override
//					protected double evolve(double p_val, RandomGenerator p_rng) {
//						return p_rng.nextGaussian() * 0.01;
//					}
//
//					@Override
//					protected double initialise(RandomGenerator p_rng) {
//						return p_rng.nextGaussian();
//					}
//				},
//				new ChangingDouble("DD C") {
//					@Override
//					protected double evolve(double p_val, RandomGenerator p_rng) {
//						return p_rng.nextGaussian() * 0.01;
//					}
//
//					@Override
//					protected double initialise(RandomGenerator p_rng) {
//						return p_rng.nextGaussian();
//					}
//				},
//				new ChangingDouble("DD D") {
//					@Override
//					protected double evolve(double p_val, RandomGenerator p_rng) {
//						return p_rng.nextGaussian() * 0.01;
//					}
//
//					@Override
//					protected double initialise(RandomGenerator p_rng) {
//						return p_rng.nextGaussian();
//					}
//				}
//			}, new AnimalStage2[]{repro, dauer});
//		}
//
//		@Override
//		public void apply(SimulationThread.DevelopmentInterface p_iface, int p_count,
//											RandomGenerator p_rng, TrackedDoubleInstance[] p_values,
//											AnimalStage2Instance[] p_stages) {
//			// double prob = Utils.logistic(values[0].get());
//			double prob = Utils.logistic(p_values[0].get()) / (1.0 + p_values[1].get()
//							* Math.pow(p_iface.getFood(), p_values[2].get())
//							* Math.pow(p_iface.getPheromone(0), p_values[3].get()));
//			int count = new BinomialDistribution(p_rng, p_count, prob).sample();
//			p_iface.addGroup(new AnimalGroup(p_stages[0], p_count - count));
//			p_iface.addGroup(new AnimalGroup(p_stages[1], count));
//		}
//	}
//
//	public static class DauerScoring extends AnimalDevelopment2 {
//		public DauerScoring(AnimalStage2 actor) {
//			super(actor, new TrackedDouble[0], new AnimalStage2[0]);
//		}
//
//		@Override
//		public void apply(SimulationThread.DevelopmentInterface p_iface, int p_count,
//											RandomGenerator p_rng, TrackedDoubleInstance[] p_values,
//											AnimalStage2Instance[] p_stages) {
//
//		}
//	}

}
