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
import com.wormsim.data.SimulationCommands;
import com.wormsim.data.SimulationConditions;
import com.wormsim.data.SimulationOptions;
import com.wormsim.simulation.Simulation;
import com.wormsim.simulation.SimulationThread;
import com.wormsim.tracking.ChangingDouble;
import com.wormsim.tracking.ConstantDouble;
import com.wormsim.tracking.TrackedCalculation;
import com.wormsim.tracking.TrackedDouble;
import com.wormsim.tracking.TrackedDoubleInstance;
import com.wormsim.utils.Utils;
import java.io.IOException;
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
	public static final int CHECKPOINT_NUMBER = 500;

	private static SimulationConditions makeCustomInitialConditions() {
		HashMap<String, IntegerDistribution> map = new HashMap<>();

		map.put("TestStrain L2", new EnumeratedIntegerDistribution(new int[]{1}));

		return new SimulationConditions(
						new ConstantRealDistribution(10000.0),
						// new NormalDistribution(1000.0, 200.0),
						new RealDistribution[]{
							new ConstantRealDistribution(0.0)
						},
						map
		);
	}

	public static void main(String[] args)
					throws IOException {
		// TODO: Move this from utils into SimulationCommands itself.
		SimulationCommands cmds = Utils.readCommandLine(args);
		SimulationOptions ops = new SimulationOptions(cmds);

		// Change options here.
		ops.checkpoint_no.set(CHECKPOINT_NUMBER);
		ops.thread_no.set(3);
		ops.assay_iteration_no.set(100);
		ops.burn_in_no.set(20000);
		ops.record_no.set(40000);
		ops.detailed_data.set(Boolean.TRUE);
		ops.walker_no.set(32);
		ops.pheromone_no.set(1);
		ops.forced_run.set(Boolean.TRUE);
		ops.initial_conditions.set(makeCustomInitialConditions());
		ops.animal_zoo.set(makeCustomAnimalZoo(ops));

		// TODO: Add in the options for additional tracked values.
		if (ops.isMissingParameters()) {
			String msg = "Missing Parameters: " + ops.getMissingParametersList();
			LOG.log(Level.SEVERE, msg);
			System.exit(-1);
		} else {
			new Simulation(ops, new TrackedCalculation("Fitness", ops) {
				@Override
				protected double added(SimulationThread.SamplingInterface p_iface,
															 AnimalGroup p_group, double p_prev_value) {
					return p_prev_value
									+ (p_group.getAnimalStage().toString().contains("Dauer")
									? p_group.getCount()
									: 0.0);
				}

				@Override
				protected double end(SimulationThread.SamplingInterface p_iface,
														 double p_prev_value) {
					return Math.pow(p_prev_value, 2);
				}

				@Override
				protected double ended(SimulationThread.SamplingInterface p_iface,
															 AnimalGroup p_group, double p_prev_value) {
					return p_prev_value;
				}

				@Override
				protected double initialise(RandomGenerator p_rng) {
					return 0.0;
				}

				@Override
				protected double removed(SimulationThread.SamplingInterface p_iface,
																 AnimalGroup p_group, double p_prev_value) {
					return p_prev_value;
				}
			}, new TrackedCalculation[]{
				new TrackedCalculation("Dauers", ops) {
					@Override
					protected double added(SimulationThread.SamplingInterface p_iface,
																 AnimalGroup p_group, double p_prev_value) {
						return p_prev_value
										+ (p_group.getAnimalStage().toString().contains("Dauer")
										? p_group.getCount()
										: 0.0);
					}

					@Override
					protected double end(SimulationThread.SamplingInterface p_iface,
															 double p_prev_value) {
						return p_prev_value;
					}

					@Override
					protected double ended(SimulationThread.SamplingInterface p_iface,
																 AnimalGroup p_group, double p_prev_value) {
						return p_prev_value;
					}

					@Override
					protected double initialise(RandomGenerator p_rng) {
						return 0.0;
					}

					@Override
					protected double removed(SimulationThread.SamplingInterface p_iface,
																	 AnimalGroup p_group, double p_prev_value) {
						return p_prev_value;
					}
				}
			}
			).run();
		}
	}

	public static AnimalZoo2 makeCustomAnimalZoo(SimulationOptions ops) {
		AnimalZoo2 zoo = new AnimalZoo2();
		AnimalStrain2 strain = new AnimalStrain2(zoo, "TestStrain");
		@SuppressWarnings("MismatchedReadAndWriteOfArray")
		AnimalStage2[] stages = {
			new AnimalStage2("L1", strain, new ConstantDouble("Food", 1.0),
			new ConstantDouble("Dev", 1.0),
			new ConstantDouble[]{new ConstantDouble("Phero1", 1.0)}),
			new AnimalStage2("L2", strain, new ConstantDouble("Food", 1.0),
			new ConstantDouble("Dev", 1.0),
			new ConstantDouble[]{new ConstantDouble("Phero1", 1.0)}),
			new AnimalStage2("L2d", strain, new ConstantDouble("Food", 1.0),
			new ConstantDouble("Dev", 1.0),
			new ConstantDouble[]{new ConstantDouble("Phero1", 1.0)}),
			new AnimalStage2("Dauer", strain, new ConstantDouble("Food", 0.0),
			new ConstantDouble("Dev", 0.0),
			new ConstantDouble[]{new ConstantDouble("Phero1", 0.0)})
		};
		@SuppressWarnings("MismatchedReadAndWriteOfArray")
		AnimalDevelopment2[] devs = {
			new DauerDevelopment(ops, stages[0], stages[1], stages[2]),
			new AnimalDevelopment2.ExplosiveLaying(stages[1],
			stages[0], new ConstantDouble("Fecundity", 100.0)),
			new AnimalDevelopment2.Linear(stages[2], stages[3]),
			new DauerScoring(stages[3])
		};
		return zoo;
	}

	public static class DauerDevelopment extends AnimalDevelopment2 {
		private static final long serialVersionUID = 1L;

		public DauerDevelopment(
						SimulationOptions ops,
						AnimalStage2 actor,
						AnimalStage2 repro,
						AnimalStage2 dauer) {
			super(actor, new ChangingDouble[]{
				new ChangingDouble("DD A", ops) {
					@Override
					protected double evolve(double p_val, RandomGenerator p_rng) {
						return p_rng.nextGaussian() * 0.1;
					}

					@Override
					protected double initialise(RandomGenerator p_rng) {
						return p_rng.nextGaussian() * 0.5 + 0.7;
					}
				},
				new ChangingDouble("DD B", ops) {
					@Override
					protected double evolve(double p_val, RandomGenerator p_rng) {
						return p_rng.nextGaussian() * 0.1;
					}

					@Override
					protected double initialise(RandomGenerator p_rng) {
						return p_rng.nextGaussian() * 2.0 + 4.4;
					}
				},
				new ChangingDouble("DD C", ops) {
					@Override
					protected double evolve(double p_val, RandomGenerator p_rng) {
						return p_rng.nextGaussian() * 0.1;
					}

					@Override
					protected double initialise(RandomGenerator p_rng) {
						return p_rng.nextGaussian() * 5.0 - 13;
					}
				},
				new ChangingDouble("DD D", ops) {
					@Override
					protected double evolve(double p_val, RandomGenerator p_rng) {
						return p_rng.nextGaussian() * 0.1;
					}

					@Override
					protected double initialise(RandomGenerator p_rng) {
						return p_rng.nextGaussian() * 3.0 - 5;
					}
				}
			}, new AnimalStage2[]{repro, dauer});
		}

		@Override
		public void apply(SimulationThread.DevelopmentInterface p_iface, int p_count,
											RandomGenerator p_rng, TrackedDoubleInstance[] p_values,
											AnimalStage2Instance[] p_stages) {
			// double prob = Utils.logistic(values[0].get());
			double prob = Utils.logistic(p_values[0].get()) / (1.0
							+ Math.abs(p_values[1].get())
							* Math.pow(p_iface.getFood(), p_values[2].get())
							* Math.pow(p_iface.getPheromone(0), p_values[3].get()));
			int count = new BinomialDistribution(p_rng, p_count, prob).sample();
			p_iface.addGroup(new AnimalGroup(p_stages[0], p_count - count));
			p_iface.addGroup(new AnimalGroup(p_stages[1], count));
		}
	}

	public static class DauerScoring extends AnimalDevelopment2 {
		public DauerScoring(AnimalStage2 actor) {
			super(actor, new TrackedDouble[0], new AnimalStage2[0]);
		}

		@Override
		public void apply(SimulationThread.DevelopmentInterface p_iface, int p_count,
											RandomGenerator p_rng, TrackedDoubleInstance[] p_values,
											AnimalStage2Instance[] p_stages) {

		}
	}

}
