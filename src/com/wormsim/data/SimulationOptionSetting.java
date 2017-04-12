/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wormsim.data;

import java.io.IOException;
import java.util.function.Predicate;
import java.util.logging.Logger;

/**
 * Denotes a setting of the options.
 *
 * TODO: Implement this for dealing with the options
 *
 * TODO: Implement optimum primitive handling.
 *
 * @author ah810
 * @version 0.0.1
 * @param <T>
 */
public final class SimulationOptionSetting<T> {
	private static final Logger LOG = Logger.getLogger(
					SimulationOptionSetting.class.getName());

	/**
	 * Creates a new simulation option with the specified key name.
	 *
	 * TODO: Include the ability to have legacy options in future.
	 *
	 * @param p_name        The name of this setting.
	 * @param p_constructor
	 *
	 * @throws java.io.IOException Never thrown.
	 */
	public SimulationOptionSetting(String p_name,
																 Constructor p_constructor)
					throws IOException {
		this(p_name, p_constructor, null, null);
	}

	/**
	 * Creates a new simulation option with the specified key name and initial
	 * value.
	 *
	 * @param p_name        The name of this setting.
	 * @param p_constructor
	 * @param p_value       The value of this setting.
	 *
	 * @throws IOException Never thrown.
	 */
	public SimulationOptionSetting(String p_name,
																 Constructor p_constructor,
																 T p_value)
					throws IOException {
		this(p_name, p_constructor, p_value, null);
	}

	/**
	 *
	 * @param p_name
	 * @param p_constructor
	 * @param condition
	 *
	 * @throws IOException Never thrown.
	 */
	public SimulationOptionSetting(String p_name,
																 Constructor p_constructor,
																 Predicate<T> condition)
					throws IOException {
		this(p_name, p_constructor, null, condition);
	}

	/**
	 *
	 * @param p_name
	 * @param p_constructor
	 * @param p_value
	 * @param condition
	 *
	 * @throws IOException Thrown if the value does not fulfil the condition.
	 */
	public SimulationOptionSetting(String p_name,
																 Constructor p_constructor,
																 T p_value,
																 Predicate<T> condition)
					throws IOException {
		if (condition == null || p_value == null || condition.test(p_value)) {
			this.value = p_value;
		} else {
			throw new IOException("Value is Invalid under the Condition.");
		}
		this.name = p_name;
		this.condition = condition;
	}
	private final Predicate<T> condition;
	private final String name;
	private Constructor p_constructor;
	private T value;

	/**
	 *
	 * @return @throws IOException
	 */
	public T get()
					throws IOException {
		if (value != null) {
			return value;
		} else {
			throw new IOException("Null Pointer Exception");
		}
	}

	/**
	 * Returns the name of this setting as it should appear as a key.
	 *
	 * @return The key it should take.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns true if the value is non-null, false otherwise.
	 *
	 * @return true if the value is non-null, false otherwise.
	 */
	public boolean isFulfilled() {
		return value != null;
	}

	/**
	 * Returns true if the provided string is a valid key for this setting. This
	 * is included as a way of dealing with legacy names if the names change.
	 *
	 * @param p_name The key string
	 *
	 * @return True if it is a valid key for this setting, false otherwise.
	 */
	public boolean isName(String p_name) {
		return name.equalsIgnoreCase(p_name);
	}

	/**
	 *
	 * @param p_value
	 *
	 * @throws IOException
	 */
	void set(T p_value)
					throws IOException {
		if (condition == null || p_value == null || condition.test(p_value)) {
			this.value = p_value;
		} else {
			throw new IOException("Value is Invalid under the Condition.");
		}
	}

	public static interface Constructor<T> {
		public T read(String str)
						throws IOException;
	}
}
