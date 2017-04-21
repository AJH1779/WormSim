/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wormsim.utils;

import com.wormsim.LaunchFromFileMain;
import com.wormsim.data.SimulationCommands;
import com.wormsim.simulation.Simulation;
import java.io.IOException;
import static java.lang.Math.max;
import static java.lang.Math.min;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.apache.commons.math3.distribution.BinomialDistribution;
import org.apache.commons.math3.distribution.ConstantRealDistribution;
import org.apache.commons.math3.distribution.EnumeratedIntegerDistribution;
import org.apache.commons.math3.distribution.IntegerDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.distribution.UniformIntegerDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import static org.apache.commons.math3.util.FastMath.exp;
import static org.apache.commons.math3.util.FastMath.log;

/**
 *
 * @author ah810
 * @version 0.0.3
 */
public class Utils {
	/**
	 * The array of authors to be cited in the order that they should appear in
	 * print.
	 *
	 * TODO: Codes for joint positions?
	 *
	 * @since 0.0.1
	 */

	private static final String[] AUTHOR_ARRAY = new String[]{"Arthur Hills", "Mark Viney", "Simon Harvey"};

	private static final Logger LOG = Logger.getLogger(Utils.class.getName());

	/**
	 * The capture pattern for the animal stage.
	 *
	 * WIP.
	 *
	 * TODO: Make this work properly and allow for the arguments to be extracted.
	 */
	public static final Pattern ANIMAL_STAGE_PATTERN = Pattern.compile(
					"\\v\\s*?stage\\s+.*?(\\s+?\\d+(.\\d+)?)+");
	/**
	 * The pattern for assigned values and strain definitions within the animal
	 * zoo assignment pattern.
	 */

	public static final Pattern ANIMAL_ZOO_ASSIGNMENT_PATTERN = Pattern.compile(
					"(strain[^\\v\\{]*\\{[.\\s\\S]*?(\\v\\t\\})|((?<=^)|(?<=\\v))[^#\\v]*?=([^\\{]*?\\v|[^\\{]*\\{[\\s\\S]*?\\v\\}))");
	/**
	 * The generic pattern for assigned values within the input.txt format.
	 */

	public static final Pattern ASSIGNMENT_PATTERN = Pattern.compile(
					"((?<=^)|(?<=\\v))[^#\\v]*?=([^\\{]*?\\v|[^\\{]*\\{[\\s\\S]*?\\v\\})");
	/**
	 * An immutable list containing the authors of the program in the order that
	 * they should appear in reference. Currently this is used only for the
	 * informative output file normally denoted "out.txt".
	 *
	 * @since 0.0.1
	 */

	public static final List<String> AUTHORS = Collections.unmodifiableList(Arrays
					.asList(AUTHOR_ARRAY));
	/**
	 * A comma delimited list of the authors to be cited in the order that they
	 * should appear in print. Currently this is used only for the informative
	 * output file normally denoted "out.txt".
	 *
	 * @since 0.0.1
	 */

	public static final String AUTHORS_AS_STRING = String.join(", ", AUTHOR_ARRAY);
	/**
	 * Intended as a pattern to ensure brackets are coupled. WIP.
	 *
	 * TODO: Make this a one time pass rather than checks on every bracket pair.
	 */

	public static final Pattern MULTIBRACKET_VALIDITY_PATTERN = Pattern.compile(
					"\\s*\\{[\\s\\S]*\\}\\s*");
	/**
	 * The literature reference for use of this program. Currently this is used
	 * only for the informative output file normally denoted "out.txt", but will
	 * in future contain the program that has to be referenced by anyone who uses
	 * this program to generate data for their work.
	 *
	 * TODO: Apply a reference when it is available.
	 *
	 * @since 0.0.1
	 */

	public static final String REFERENCE = "null";
	/**
	 * The generic pattern for sampled values within the input.txt format.
	 */

	public static final Pattern SAMPLER_PATTERN = Pattern.compile(
					"((?<=^)|(?<=\\v))[^#\\v]*?~([^\\{]*?\\v|[^\\{]*\\{[\\s\\S]*?\\v\\})");
	/**
	 * The pattern used for grabbing the key of the animal strain with whitespace.
	 */

	public static final Pattern STRAIN_KEY_PATTERN = Pattern.compile(
					"(?<=strain)\\s+[^\\{]*");
	/**
	 * The pattern used for grabbing the strain definitions within the animal zoo
	 * assignment pattern.
	 */

	public static final Pattern STRAIN_PATTERN = Pattern.compile(
					"strain[^\\v\\{]*\\{[.\\s\\S]*?(\\v\\t\\})");
	public static final Collector<CharSequence, ?, String> TAB_JOINING = Collectors
					.joining("\t");
	/**
	 * The version code of this program.
	 *
	 * TODO: Keep Updated.
	 *
	 * @since 0.0.1
	 */

	public static final String VERSION = "0.0.3";

	public static final RealDistribution ZERO_REAL_DISTRIBUTION
					= new ConstantRealDistribution(0.0);

	/**
	 * Returns a value bounded between min and max that is closest to the
	 * specified value.
	 *
	 * @param val The value to bound
	 * @param min The minimum of the bound.
	 * @param max The maximum of the bound.
	 *
	 * @return The bounded value.
	 */
	public static int bound(int val, int min, int max) {
		return min(max(val, min), min);
	}

	/**
	 * Returns a value bounded between min and max that is closest to the
	 * specified value.
	 *
	 * @param val The value to bound
	 * @param min The minimum of the bound.
	 * @param max The maximum of the bound.
	 *
	 * @return The bounded value.
	 */
	public static float bound(float val, float min, float max) {
		return min(max(val, min), min);
	}

	/**
	 * Returns a value bounded between min and max that is closest to the
	 * specified value.
	 *
	 * @param val The value to bound
	 * @param min The minimum of the bound.
	 * @param max The maximum of the bound.
	 *
	 * @return The bounded value.
	 */
	public static long bound(long val, long min, long max) {
		return min(max(val, min), min);
	}

	/**
	 * Returns a value bounded between min and max that is closest to the
	 * specified value.
	 *
	 * @param val The value to bound
	 * @param min The minimum of the bound.
	 * @param max The maximum of the bound.
	 *
	 * @return The bounded value.
	 */
	public static double bound(double val, double min, double max) {
		return min(max(val, min), min);
	}

	/**
	 * Outputs the help dialogue to the command line which outlines all of the
	 * commands, their arguments, and what they do. This is an output of the
	 * program header followed by the help text defined in header.txt and help.txt
	 * respectively.
	 *
	 * @see header.txt for the header text.
	 * @see help.txt for the full help text.
	 *
	 * @since 0.0.1
	 */
	@SuppressWarnings(value = "UseOfSystemOutOrSystemErr")
	public static void help() {
		// TODO: Move these scanner outputs to a more consistent environment.
		// TODO: Move the files to their own package.
		System.out.println(new Scanner(LaunchFromFileMain.class.getResourceAsStream(
						"/com/wormsim/header.txt")).useDelimiter("\\Z")
						.next().replace("{authors}", Utils.AUTHORS_AS_STRING)
						.replace("{version}", Utils.VERSION)
						.replace("{reference}", Utils.REFERENCE));
		System.out.println(new Scanner(LaunchFromFileMain.class.getResourceAsStream(
						"/com/wormsim/help.txt")).useDelimiter("\\Z")
						.next().replace("{out.txt}", Simulation.OUT_TXT));
	}

	/**
	 * Returns a string representation of the provided distribution. TODO: Make
	 * this complete TODO: Make this compatible with custom distributions (or just
	 * more complex ones).
	 *
	 * @param dist The distribution to translate
	 *
	 * @return The distribution as a string.
	 */
	public static String integerDistributionToString(
					IntegerDistribution dist) {
		if (dist instanceof EnumeratedIntegerDistribution) {
			return Double.toString(dist.getNumericalMean());
		} else if (dist instanceof UniformIntegerDistribution) {
			return "Uniform(" + dist.getSupportLowerBound() + "," + dist
							.getSupportUpperBound() + ")";
		} else if (dist instanceof BinomialDistribution) {
			BinomialDistribution dist2 = (BinomialDistribution) dist;
			return "Binomial(" + dist2.getNumberOfTrials() + "," + dist2
							.getProbabilityOfSuccess() + ")";
		} else {
			return dist.toString();
		}
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

	/**
	 * Returns a boolean object represented by the string input. The definition of
	 * a true or false statement here is explicit and matches the corresponding
	 * regex. Throws an exception if the regex is not matched.
	 *
	 * Regex for true: "true|True|T|t|yes|y|TRUE"
	 *
	 * Regex for false: "false|False|F|f|no|n|FALSE"
	 *
	 * @param str The boolean as a string.
	 *
	 * @return The represented value.
	 *
	 * @throws IOException If the input does not match a boolean.
	 */
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

	public static SimulationCommands readCommandLine(String[] p_args)
					throws IllegalArgumentException {
		// Convert the arguments into command lists
		HashMap<String, List<String>> data = new HashMap<>(p_args.length);
		String current_cmd = null;
		for (String arg : p_args) {
			if (arg.startsWith("-")) {
				current_cmd = arg;
				if (data.putIfAbsent(arg, new ArrayList<>(2)) != null) {
					throw new IllegalArgumentException("Repeated Argument: " + arg);
				}
			} else if (current_cmd != null) {
				data.get(current_cmd).add(arg);
			} else {
				throw new IllegalArgumentException("First Parameter must be Argument: "
								+ arg);
			}
		}
		// Check if any of the commands are something to act upon right now, like help.
		// WARNING: Hard Coded Parameters.
		if (data.containsKey("-h") || data.containsKey("--help")) {
			// Print out the help and then terminate, although the other arguments should
			// also be checked to see if they are relevant.
			help();
			System.exit(0); // Generally not recommended, but should be fine here.
			// TODO: Detailed information as per argument for help?
		}
		SimulationCommands cmds = new SimulationCommands(data);
		return cmds;
	}

	/**
	 * Returns an integer object represented by the string input, throwing an
	 * exception if the object is not an integer.
	 *
	 * @param str The integer as a string.
	 *
	 * @return The represented value.
	 *
	 * @throws IOException If the input does not match a integer.
	 */
	public static Integer readInteger(String str)
					throws IOException {
		try {
			return Integer.valueOf(str);
		} catch (NumberFormatException ex) {
			throw new IOException(ex);
		}
	}

	/**
	 * Returns the distribution associated with the specified string. See the
	 * handbook for details of what is accepted. Or the code...
	 *
	 * @param str The string representing a distribution
	 *
	 * @return The distribution
	 */
	public static IntegerDistribution readIntegerDistribution(
					String str) {
		if (str.matches("[0-9]+(.[0-9]*)?")) {
			// I.E. a number
			return new EnumeratedIntegerDistribution(new int[]{Integer.valueOf(str)});
		} else {
			int index = str.indexOf('(');
			String prefix = str.substring(0, index - 1).toLowerCase(Locale.ROOT);
			switch (prefix) {
				case "b":
				case "binom":
				case "binomial": {
					int comma_index = str.indexOf(',', index);
					return new BinomialDistribution(Integer.valueOf(str.substring(index,
									comma_index - 1)),
									Double.valueOf(str.substring(comma_index).trim()));
				}
				case "u":
				case "uni":
				case "uniform": {
					int comma_index = str.indexOf(',', index);
					return new UniformIntegerDistribution(Integer.valueOf(str.substring(
									index,
									comma_index - 1)),
									Integer.valueOf(str.substring(comma_index).trim()));
				}
				default: {
					throw new IllegalArgumentException(
									"Unrecognised distribution form, see handbook for details. "
									+ "Provided \"" + str + "\".");
				}
			}
		}
	}

	/**
	 * Returns a long object represented by the string input, throwing an
	 * exception if the object is not an integer.
	 *
	 * @param str The long as a string.
	 *
	 * @return The represented value.
	 *
	 * @throws IOException If the input does not match a long.
	 */
	public static Long readLong(String str)
					throws IOException {
		try {
			return Long.valueOf(str);
		} catch (NumberFormatException ex) {
			throw new IOException(ex);
		}
	}

	// TODO: Revise the quality of these javadocs!
	/**
	 * Returns the distribution associated with the specified string. See the
	 * handbook for details of what is accepted. Or the code...
	 *
	 * @param str The string representing a distribution
	 *
	 * @return The distribution
	 */
	public static RealDistribution readRealDistribution(String str) {
		if (str.matches("[0-9]+(.[0-9]*)?")) {
			// I.E. a number
			return new ConstantRealDistribution(Double.valueOf(str));
		} else {
			int index = str.indexOf('(');
			String prefix = str.substring(0, index).toLowerCase(Locale.ROOT);
			switch (prefix) {
				case "n":
				case "norm":
				case "normal": {
					int comma_index = str.indexOf(',', index);
					return new NormalDistribution(Double.valueOf(str.substring(index + 1,
									comma_index).trim()),
									Double.valueOf(str.substring(comma_index + 1,
													str.length() - 2).trim()));
				}
				case "u":
				case "uni":
				case "uniform": {
					int comma_index = str.indexOf(',', index);
					return new UniformRealDistribution(Double.valueOf(str.substring(index
									+ 1, comma_index - 1)),
									Double.valueOf(str.substring(comma_index).trim()));
				}
				default:
					throw new IllegalArgumentException(
									"Unrecognised distribution form, see handbook for details. "
									+ "Provided \"" + str + "\".");
			}
		}
	}

	/**
	 * Returns a string representation of the provided distribution. TODO: Make
	 * this complete TODO: Make this compatible with custom distributions (or just
	 * more complex ones).
	 *
	 * @param dist The distribution to translate
	 *
	 * @return The distribution as a string.
	 */
	public static String realDistributionToString(RealDistribution dist) {
		if (dist instanceof ConstantRealDistribution) {
			return Double.toString(dist.getNumericalMean());
		} else if (dist instanceof UniformRealDistribution) {
			return "Uniform(" + dist.getSupportLowerBound() + "," + dist
							.getSupportUpperBound() + ")";
		} else if (dist instanceof NormalDistribution) {
			NormalDistribution dist2 = (NormalDistribution) dist;
			return "Normal(" + dist2.getMean() + "," + dist2.getStandardDeviation()
							+ ")";
		} else {
			return dist.toString();
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
