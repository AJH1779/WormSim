/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wormsim.utils;

import static com.wormsim.utils.Utils.logit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import static org.apache.commons.math3.util.FastMath.pow;
import static org.apache.commons.math3.util.FastMath.sqrt;

/**
 * An object which represents a scalar value recorded over time.
 *
 * @author ah810
 * @deprecated
 */
@SuppressWarnings("UseOfSystemOutOrSystemErr")
public class TrackedScalar {

	private static final Logger LOG = Logger.getLogger(TrackedScalar.class
					.getName());
	private static long time = 0L;

	public static double calcBetweenVarianceA(List<List<Double>> data) {
		double psi__ = data.stream().mapToDouble((arr) -> arr.stream().mapToDouble(
						(d) -> d).sum() / (arr.size())).sum() / (data.size());
		double result = data.stream().mapToDouble((arr)
						-> pow(arr.stream().mapToDouble((d) -> d).sum() / (arr.size())
										- psi__, 2) * arr.size()
		).sum() / (data.size() - 1);
		System.out.println("Between Variance A = " + result);
		return result;
	}

	public static double calcBetweenVarianceB(List<List<Double>> data) {
		double psi__ = data.stream().mapToDouble((arr) -> arr.stream().mapToDouble(
						(d) -> d / (arr.size())).sum() / (data.size())).sum();
		double result = data.stream().mapToDouble((arr)
						-> pow(arr.stream().mapToDouble((d) -> d).sum() / (arr.size())
										- psi__, 2) * arr.size() / (data.size() - 1)
		).sum();
		System.out.println("Between Variance B = " + result);
		return result;
	}

	public static double calcR(double N, double B, double W) {
		double R = sqrt((N - 1.0) / N + B / (N * W));
		System.out.println("R = " + R);
		return R;
	}

	public static double calcVar(double N, double B, double W) {
		double var = (W * (N - 1.0)) / N + B / N;
		System.out.println("var = " + var);
		return var;
	}

	public static double calcWithinVarianceA(List<List<Double>> data) {
		double result = data.stream().mapToDouble((arr) -> {
			final double psi_m = arr.stream().mapToDouble((d) -> d).sum() / (arr
							.size());
			return arr.stream().mapToDouble((d) -> pow(d - psi_m, 2)).sum() / (arr
							.size() - 1);
		}).sum() / (data.size());
		System.out.println("Within Variance A = " + result);
		return result;
	}

	public static double calcWithinVarianceB(List<List<Double>> data) {
		double result = data.stream().mapToDouble((arr) -> {
			final double psi_m = arr.stream().mapToDouble((d) -> d / (arr.size()))
							.sum();
			return arr.stream().mapToDouble((d) -> pow(d - psi_m, 2)
							/ (arr.size() - 1)).sum() / (data.size());
		}).sum();
		System.out.println("Within Variance B = " + result);
		return result;
	}

	public static void main(String[] args) {
		TrackedScalar scalar = new TrackedScalar(10, 100);
	}

	public static void main2(String[] args) {
		timerStart();
		final int walker_no = 1024;
		final int iter_no = 100;
		final List<List<Double>> data = new ArrayList<>(walker_no);
		final Random rng = new Random();
		for (int i = 0; i < walker_no; i++) {
			data.add(rng.doubles(iter_no).map((d) -> logit(d)).boxed().collect(
							Collectors.toList()));
		}
		double B, W, var, R;
		timer("Generated Data");
		W = calcWithinVarianceA(data);
		timer("Time Taken");
		B = calcBetweenVarianceA(data);
		timer("Time Taken");
		var = calcVar(iter_no, B, W);
		R = calcR(iter_no, B, W);
		timer("Time Taken");
		W = calcWithinVarianceB(data);
		timer("Time Taken");
		B = calcBetweenVarianceB(data);
		timer("Time Taken");
		var = calcVar(iter_no, B, W);
		R = calcR(iter_no, B, W);
		timer("Time Taken");
	}

	public static void timer(String msg) {
		long time2 = System.nanoTime();
		long delt = time2 - time;
		time = time2;
		System.out.println(msg + ": " + delt + " ns");
	}

	public static void timerStart() {
		time = System.nanoTime();
	}

	@SuppressWarnings("unchecked")
	public TrackedScalar(int walkers, int iterations) {
		ArrayList<Double>[] array;
		array = new ArrayList[walkers];
		for (int i = 0; i < array.length; i++) {
			array[i] = new ArrayList<>(iterations);
		}
		data = Collections.unmodifiableList(Arrays.asList(array));
	}

	private final List<List<Double>> data;

	private double calcBetweenVariance() {
		double psi__ = data.stream().mapToDouble((arr) -> arr.stream().mapToDouble(
						(d) -> d / (arr.size())).sum() / (data.size())).sum();
		double result = data.stream().mapToDouble((arr)
						-> pow(arr.stream().mapToDouble((d) -> d).sum() / (arr.size())
										- psi__, 2) * arr.size() / (data.size() - 1)
		).sum();
		return result;
	}

	private double calcWithinVariance() {
		double result = data.stream().mapToDouble((arr) -> {
			final double psi_m = arr.stream().mapToDouble((d) -> d / (arr.size()))
							.sum();
			return arr.stream().mapToDouble((d) -> pow(d - psi_m, 2)
							/ (arr.size() - 1)).sum() / (data.size());
		}).sum();
		return result;
	}
}
