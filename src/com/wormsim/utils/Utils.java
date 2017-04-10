/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wormsim.utils;

import java.util.logging.Logger;
import static org.apache.commons.math3.util.FastMath.exp;
import static org.apache.commons.math3.util.FastMath.log;

/**
 *
 * @author ah810
 */
public class Utils {

	private static final Logger LOG = Logger.getLogger(Utils.class.getName());


	/**
	 * Returns the logistic transformation of the input value.
	 * @param val
	 * @return 
	 */
	public static double logistic(double val) {
		return 1.0 / (1.0 + exp(-val));
	}
	
	/**
	 * Returns the logit transformation of the input value.
	 * @param val
	 * @return 
	 */
	public static double logit(double val) {
		return log(val / (1.0 - val));
	}
	
	/**
	 * Converts the time in milliseconds to a time string.
	 * @param dmillis The time in milliseconds.
	 * @return The time as a string.
	 */
	public static String toTime(long dmillis) {
		// TODO: Choose a better format.
		return String.format("%d days, %d hours, %d mins, %d secs, %d millis",
						(int) (dmillis / (1000 * 60 * 60 * 24)),
						(int) (dmillis / (1000 * 60 * 60) % 24),
						(int) (dmillis / (1000 * 60) % 60),
						(int) (dmillis / (1000) % 60),
						(int) (dmillis % 1000));
	}
	
	private Utils() {
	}
}
