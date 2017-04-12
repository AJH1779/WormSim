/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wormsim.utils;

import com.wormsim.utils.Context.BasicContext;
import static java.lang.Math.max;
import static java.lang.Math.min;
import java.util.HashMap;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author ah810
 */
public class StringFormula {
	private static final Logger LOG = Logger.getLogger(StringFormula.class
					.getName());
	public static final Pattern ADD_PATTERN = Pattern.compile(
					"((?<=^|\\/|\\*|-|\\+)[^+\\-*\\/]*\\+[^+\\-*\\/]*(?=$|\\/|\\*|-|\\+))");
	public static final Pattern AND_PATTERN = Pattern.compile(
					"((?<=^|\\||\\&)[^|&]*\\&[^|&]*(?=$|\\||\\&))");
	public static final Pattern BRACKET_PATTERN = Pattern.compile("\\([^()]*\\)");
	public static final Pattern DIVIDE_PATTERN = Pattern.compile(
					"((?<=^|\\/|\\*|-|\\+)[^+\\-*\\/]*\\/[^+\\-*\\/]*(?=$|\\/|\\*|-|\\+))");
	public static final Pattern METHOD_PATTERN = Pattern.compile(
					"[a-zA-Z0-9_.]+\\s*\\([^()]*\\)");
	public static final Pattern MULTIPLY_PATTERN = Pattern.compile(
					"((?<=^|\\/|\\*|-|\\+)[^+\\-*\\/]*\\*[^+\\-*\\/]*(?=$|\\/|\\*|-|\\+))");
	public static final Pattern OR_PATTERN = Pattern.compile(
					"((?<=^|\\||\\&)[^|&]*\\|[^|&]*(?=$|\\||\\&))");
	public static final Pattern SUBTRACT_PATTERN = Pattern.compile(
					"((?<=^|\\/|\\*|-|\\+)[^+\\-*\\/]*\\-[^+\\-*\\/]*(?=$|\\/|\\*|-|\\+))");

	private static boolean takeAdd(String[] formula,
																 HashMap<String, Formula> in_funcs) {
		Matcher m = ADD_PATTERN.matcher(formula[0]);
		boolean flag = false;
		while (m.find()) {
			flag = true;
			String match = m.group();
			String key = "#" + in_funcs.size();
			int opindex = match.indexOf('+');
			Formula left = makeFormula(match.substring(0, opindex).trim(), in_funcs);
			Formula right = makeFormula(match.substring(opindex + 1).trim(), in_funcs);
			in_funcs.put(key, new Add(left, right));
			formula[0] = Pattern.compile(match, Pattern.LITERAL).matcher(formula[0])
							.replaceFirst(key);
		}
		return flag;
	}

	private static boolean takeAnd(String[] formula,
																 HashMap<String, Formula> in_funcs) {
		Matcher m = AND_PATTERN.matcher(formula[0]);
		boolean flag = false;
		while (m.find()) {
			flag = true;
			String match = m.group();
			String key = "#" + in_funcs.size();
			int opindex = match.indexOf('&');
			Formula left = makeFormula(match.substring(0, opindex).trim(), in_funcs);
			Formula right = makeFormula(match.substring(opindex + 1).trim(), in_funcs);
			in_funcs.put(key, new And(left, right));
			formula[0] = Pattern.compile(match, Pattern.LITERAL).matcher(formula[0])
							.replaceFirst(key);
		}
		return flag;
	}

	private static boolean takeBrackets(String[] formula,
																			HashMap<String, Formula> in_funcs) {
		Matcher m = BRACKET_PATTERN.matcher(formula[0]);
		boolean flag = false;
		while (m.find()) {
			flag = true;
			String match = m.group();
			String key = "#" + in_funcs.size();
			// System.out.println("Method " + key + " = " + match);
			in_funcs.put(key, makeFormula(match.substring(1, match.length() - 1),
							in_funcs));
			formula[0] = Pattern.compile(match, Pattern.LITERAL).matcher(formula[0])
							.replaceFirst(key);
		}
		return flag;
	}

	private static boolean takeDivide(String[] formula,
																		HashMap<String, Formula> in_funcs) {
		Matcher m = DIVIDE_PATTERN.matcher(formula[0]);
		boolean flag = false;
		while (m.find()) {
			flag = true;
			String match = m.group();
			String key = "#" + in_funcs.size();
			int opindex = match.indexOf('/');
			Formula left = makeFormula(match.substring(0, opindex).trim(), in_funcs);
			Formula right = makeFormula(match.substring(opindex + 1).trim(), in_funcs);
			in_funcs.put(key, new Divide(left, right));
			formula[0] = Pattern.compile(match, Pattern.LITERAL).matcher(formula[0])
							.replaceFirst(key);
		}
		return flag;
	}

	private static boolean takeMethods(String[] formula,
																		 HashMap<String, Formula> in_funcs) {
		Matcher m = METHOD_PATTERN.matcher(formula[0]);
		boolean flag = false;
		while (m.find()) {
			flag = true;
			String match = m.group();
			String key = "#" + in_funcs.size();
			// System.out.println("Method " + key + " = " + match);
			int first_bracket = match.indexOf('(');
			String name = match.substring(0, first_bracket).trim();
			String[] args = match.substring(first_bracket + 1, match.length() - 1)
							.split("\\s*(,|;)\\s*");
			Formula[] params;
			if (args.length == 1 && args[0].isEmpty()) {
				params = new Formula[0];
			} else {
				params = new Formula[args.length];
				for (int i = 0; i < args.length; i++) {
					params[i] = makeFormula(args[i], in_funcs);
				}
			}
			in_funcs.put(key, new Method(name, params));
			formula[0] = Pattern.compile(match, Pattern.LITERAL).matcher(formula[0])
							.replaceFirst(key);
		}
		return flag;
	}

	private static boolean takeMultiply(String[] formula,
																			HashMap<String, Formula> in_funcs) {
		Matcher m = MULTIPLY_PATTERN.matcher(formula[0]);
		boolean flag = false;
		while (m.find()) {
			flag = true;
			String match = m.group();
			String key = "#" + in_funcs.size();
			int opindex = match.indexOf('*');
			Formula left = makeFormula(match.substring(0, opindex).trim(), in_funcs);
			Formula right = makeFormula(match.substring(opindex + 1).trim(), in_funcs);
			in_funcs.put(key, new Multiply(left, right));
			formula[0] = Pattern.compile(match, Pattern.LITERAL).matcher(formula[0])
							.replaceFirst(key);
		}
		return flag;
	}

	private static boolean takeOr(String[] formula,
																HashMap<String, Formula> in_funcs) {
		Matcher m = OR_PATTERN.matcher(formula[0]);
		boolean flag = false;
		while (m.find()) {
			flag = true;
			String match = m.group();
			String key = "#" + in_funcs.size();
			int opindex = match.indexOf('|');
			Formula left = makeFormula(match.substring(0, opindex).trim(), in_funcs);
			Formula right = makeFormula(match.substring(opindex + 1).trim(), in_funcs);
			in_funcs.put(key, new Or(left, right));
			formula[0] = Pattern.compile(match, Pattern.LITERAL).matcher(formula[0])
							.replaceFirst(key);
		}
		return flag;
	}

	private static boolean takeSubtract(String[] formula,
																			HashMap<String, Formula> in_funcs) {
		Matcher m = SUBTRACT_PATTERN.matcher(formula[0]);
		boolean flag = false;
		while (m.find()) {
			flag = true;
			String match = m.group();
			String key = "#" + in_funcs.size();
			int opindex = match.indexOf('-');
			Formula left = makeFormula(match.substring(0, opindex).trim(), in_funcs);
			Formula right = makeFormula(match.substring(opindex + 1).trim(), in_funcs);
			in_funcs.put(key, new Subtract(left, right));
			formula[0] = Pattern.compile(match, Pattern.LITERAL).matcher(formula[0])
							.replaceFirst(key);
		}
		return flag;
	}

	public static double evaluate(final String in_formula, Context context) {
		return makeFormula(in_formula, null).evaluate(context);
	}

	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public static void main(String[] args) {
		StringFormula test = new StringFormula("pow(pi(),(2+3)/4.5)");
		BasicContext con = new BasicContext();
		con.addVariable("A", 1.0);
		con.addVariable("B", 2.0);
		con.addVariable("C", 3.0);
		con.addVariable("D", 4.0);
		con.addMethod("pi", (params) -> Math.PI);
		con.addMethod("pow", (params) -> Math.pow(params[0], params[1]));
		System.out.println(test.str + "=" + test.evaluate(con));
	}

	public static Formula makeFormula(final String in_formula,
																		HashMap<String, Formula> in_funcs) {
		HashMap<String, Formula> funcs = in_funcs == null
						? new HashMap<>()
						: in_funcs;
		String[] formula = new String[]{in_formula.trim()};
		// System.out.println(formula[0]);
		while (takeMethods(formula, funcs) || takeBrackets(formula, funcs)
						|| takeOr(formula, funcs) || takeAnd(formula, funcs) || takeDivide(
						formula, funcs) || takeMultiply(formula, funcs) || takeSubtract(
						formula, funcs) || takeAdd(formula, funcs)) {
			// System.out.println(formula[0]);
		}

		Formula f = funcs.getOrDefault(formula[0], formula[0].matches(
						"-?\\d+(\\.\\d+)?")
										? new Constant(Double.valueOf(formula[0]))
										: new Variable(formula[0]));
		return f;
	}

	public StringFormula(String str) {
		this.str = str;
		this.root = makeFormula(str, null);
	}
	private final Formula root;

	private final String str;

	public double evaluate(Context con) {
		return root.evaluate(con);
	}

	/*
	public Formula compile() {
		CompiledFormula form = new CompiledFormula(str);
		root.compile(form);
		return form.compile();
	}
	 */
	@Override
	public String toString() {
		return str;
	}

	public static interface Formula {
		public abstract double evaluate(Context con);

		// public abstract void compile(CompiledFormula form);
	}

	public static class Add extends LRFormula {
		public Add(Formula l, Formula r) {
			super(l, r);
		}

		@Override
		public double evaluate(Context con) {
			return l.evaluate(con) + r.evaluate(con);
		}
	}

	public static class And extends LRFormula {
		public And(Formula l, Formula r) {
			super(l, r);
		}

		@Override
		public double evaluate(Context con) {
			return min(l.evaluate(con), r.evaluate(con));
		}
	}

	public static final class Constant implements Formula {
		public Constant(double d) {
			this.value = d;
		}
		private final double value;

		@Override
		public double evaluate(Context con) {
			return value;
		}
	}

	public static class Divide extends LRFormula {
		public Divide(Formula l, Formula r) {
			super(l, r);
		}

		@Override
		public double evaluate(Context con) {
			return l.evaluate(con) / r.evaluate(con);
		}
	}

	public static abstract class LRFormula implements Formula {
		public LRFormula(Formula l, Formula r) {
			this.l = l;
			this.r = r;
		}
		protected final Formula l, r;
	}

	public static final class Method implements Formula {
		public Method(String name, Formula... args) {
			this.name = name;
			this.args = args;
		}
		private final Formula[] args;
		private final String name;

		@Override
		public double evaluate(Context con) {
			double[] doubs = new double[args.length];
			for (int i = 0; i < doubs.length; i++) {
				doubs[i] = args[i].evaluate(con);
			}
			return con.getMethod(name).evaluate(doubs);
		}
	}

	public static class Multiply extends LRFormula {
		public Multiply(Formula l, Formula r) {
			super(l, r);
		}

		@Override
		public double evaluate(Context con) {
			return l.evaluate(con) * r.evaluate(con);
		}
	}

	public static class Or extends LRFormula {
		public Or(Formula l, Formula r) {
			super(l, r);
		}

		@Override
		public double evaluate(Context con) {
			return max(l.evaluate(con), r.evaluate(con));
		}
	}

	public static class Subtract extends LRFormula {
		public Subtract(Formula l, Formula r) {
			super(l, r);
		}

		@Override
		public double evaluate(Context con) {
			return l.evaluate(con) - r.evaluate(con);
		}
	}

	public static final class Variable implements Formula {
		public Variable(String ref) {
			this.ref = ref;
		}
		private final String ref;

		@Override
		public double evaluate(Context con) {
			return con.get(ref);
		}
	}

}
