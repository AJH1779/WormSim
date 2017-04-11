/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wormsim.utils;

import java.util.HashMap;
import org.apache.commons.math3.util.FastMath;

/**
 *
 * @author ah810
 */
public abstract class Context implements Cloneable {

	public static final BasicContext GLOBAL_CONTEXT = new BasicContext();

	static {
		GLOBAL_CONTEXT.addMethod("ln", (p) -> FastMath.log(p[0]));
		GLOBAL_CONTEXT.addMethod("log", (p) -> FastMath.log(p[0], p[1]));
		GLOBAL_CONTEXT.addMethod("log10", (p) -> FastMath.log10(p[0]));
		GLOBAL_CONTEXT.addMethod("exp", (p) -> FastMath.exp(p[0]));
		GLOBAL_CONTEXT.addMethod("logit", (p) -> Utils.logit(p[0]));
		GLOBAL_CONTEXT.addMethod("logistic", (p) -> Utils.logistic(p[0]));
		GLOBAL_CONTEXT.addMethod("e", (p) -> FastMath.E);
		GLOBAL_CONTEXT.addMethod("pi", (p) -> FastMath.PI);
	}

	public abstract double get(String ref);

	public abstract Method getMethod(String ref);

	public static interface Method {
		public double evaluate(double... params);

		public static final Method ZERO = (p) -> 0.0;
	}

	public final static class BasicContext extends Context {
		public BasicContext() {
			this.variables = new HashMap<>();
			this.methods = new HashMap<>();
		}
		private final HashMap<String, Method> methods;
		private final HashMap<String, Double> variables;

		public void addMethod(String ref, Method method) {
			methods.put(ref, method);
		}

		public void addVariable(String ref, double param) {
			variables.put(ref, param);
		}

		@Override
		public BasicContext clone() {
			try {
				return (BasicContext) super.clone();
			} catch (CloneNotSupportedException ex) {
				throw new AssertionError("Should be able to clone BasicContext.", ex);
			}
		}

		@Override
		public double get(String ref) {
			return variables.getOrDefault(ref, 0.0);
		}

		@Override
		public Method getMethod(String ref) {
			return methods.getOrDefault(ref, Method.ZERO);
		}

	}
}
