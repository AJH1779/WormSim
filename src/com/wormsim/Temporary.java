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
import java.util.logging.Logger;

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
			new AnimalStage("L1", strain),
			new AnimalStage("L2", strain),
			new AnimalStage("L2d", strain),
			new AnimalStage("Dauer", strain)
		};
		AnimalDevelopment[] devs = {
			new AnimalDevelopment.Branching(stages[0], stages[1], stages[2],
			(p_iface, p_count, p_rng) -> {
				return p_count / 2;
			}),
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

	private Temporary() {
	}

}
