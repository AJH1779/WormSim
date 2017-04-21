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
	 * @param p_options
	 * @param p_name        The name of this setting.
	 * @param p_constructor
	 */
	public SimulationOptionSetting( SimulationOptions p_options,
																  String p_name,
																  Constructor<T> p_constructor) {
		this(p_options, p_name, p_constructor, null, null);
	}

	/**
	 * Creates a new simulation option with the specified key name and initial
	 * value.
	 *
	 * @param p_options
	 * @param p_name        The name of this setting.
	 * @param p_constructor
	 * @param p_value       The value of this setting.
	 */
	public SimulationOptionSetting( SimulationOptions p_options,
																  String p_name,
																  Constructor<T> p_constructor,
																  T p_value) {
		this(p_options, p_name, p_constructor, p_value, null);
	}

	/**
	 *
	 * @param p_options
	 * @param p_name
	 * @param p_constructor
	 * @param p_condition
	 */
	public SimulationOptionSetting( SimulationOptions p_options,
																  String p_name,
																  Constructor<T> p_constructor,
																  Predicate<T> p_condition) {
		this(p_options, p_name, p_constructor, null, p_condition);
	}

	/**
	 *
	 * @param p_options
	 * @param p_name
	 * @param p_constructor
	 * @param p_value
	 * @param p_condition
	 */
	@SuppressWarnings("LeakingThisInConstructor")
	public SimulationOptionSetting( SimulationOptions p_options,
																  String p_name,
																  Constructor<T> p_constructor,
																  T p_value,
																  Predicate<T> p_condition) {
		if (p_condition == null || p_value == null || p_condition.test(p_value)) {
			this.value = p_value;
		} else {
			throw new AssertionError("Value is Invalid under the Condition.");
		}
		this.name = p_name;
		this.condition = p_condition;
		this.constructor = p_constructor;

		if (p_options.settings.putIfAbsent(name, this) != null) {
			throw new IllegalArgumentException(
							"Cannot create two entries with the same name (" + p_name + ")!");
		}
	}
	private final Predicate<T> condition;
	private final Constructor<T> constructor;
	private final String name;
	private T value;

	/**
	 * Returns the value currently stored for this setting.
	 *
	 * @return
	 */
	
	public T get() {
		assert value != null;
		return value;
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
	public boolean isName( String p_name) {
		return name.equalsIgnoreCase(p_name);
	}

	/**
	 *
	 * @param p_value
	 *
	 * @throws IOException
	 */
	public void set( T p_value)
					throws IOException {
		if (condition == null || condition.test(p_value)) {
			this.value = p_value;
		} else {
			throw new IOException("Value is Invalid under the Condition.");
		}
	}

	/**
	 * Sets this value to the one described by the string.
	 *
	 * @param str
	 *
	 * @throws IOException
	 */
	public void setFromString( String str)
					throws IOException {
		set(constructor.read(str));
	}

	/**
	 * A functional interface for generating objects represented by strings.
	 *
	 * @param <T> The type of the object.
	 */
	@FunctionalInterface
	public static interface Constructor<T> {
		/**
		 * Provides an object which is represented by the string given.
		 *
		 * @param str The string describing the object
		 *
		 * @return The object given by the string.
		 *
		 * @throws IOException If the string is malformed.
		 */
		
		public T read( String str)
						throws IOException;
	}
}
