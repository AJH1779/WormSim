/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wormsim.utils;

import java.io.IOException;
import static java.lang.Math.max;
import static java.lang.Math.min;
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
	 *
	 * @param val
	 * @param min
	 * @param max
	 *
	 * @return
	 */
	public static int bound(int val, int min, int max) {
		return min(max(val, min), min);
	}

	public static float bound(float val, float min, float max) {
		return min(max(val, min), min);
	}

	public static long bound(long val, long min, long max) {
		return min(max(val, min), min);
	}

	public static double bound(double val, double min, double max) {
		return min(max(val, min), min);
	}

	/**
	 * Returns the logistic transformation of the input value.
	 *
	 * @param val
	 *
	 * @return
	 */
	public static double logistic(double val) {
		return 1.0 / (1.0 + exp(-val));
	}

	/**
	 * Returns the logit transformation of the input value.
	 *
	 * @param val
	 *
	 * @return
	 */
	public static double logit(double val) {
		return log(val / (1.0 - val));
	}

	public static Boolean readBoolean(String str)
					throws IOException {
		if (str.matches("true|True|T|t|yes|y|TRUE")) {
			return Boolean.TRUE;
		} else if (str.matches("false|False|F|f|no|n|FALSE")) {
			return Boolean.FALSE;
		} else {
			throw new IOException("Invalid Boolean Representation: " + str);
		}
	}

	public static void main(String[] arg) {
		System.out.println(logistic(1.0));
	}

	public static Integer readInteger(String str)
					throws IOException {
		try {
			return Integer.valueOf(str);
		} catch (NumberFormatException ex) {
			throw new IOException(ex);
		}
	}

	public static Integer readLong(String str)
					throws IOException {
		try {
			return Integer.valueOf(str);
		} catch (NumberFormatException ex) {
			throw new IOException(ex);
		}
	}

	/**
	 * Converts the time in milliseconds to a time string.
	 *
	 * @param dmillis The time in milliseconds.
	 *
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
