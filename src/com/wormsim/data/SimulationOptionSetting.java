/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wormsim.data;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
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
	 *
	 * @throws java.io.IOException Never thrown.
	 */
	public SimulationOptionSetting(@NotNull SimulationOptions2 p_options,
																 @NotNull String p_name,
																 @NotNull Constructor<T> p_constructor)
					throws IOException {
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
	 *
	 * @throws IOException Never thrown.
	 */
	public SimulationOptionSetting(@NotNull SimulationOptions2 p_options,
																 @NotNull String p_name,
																 @NotNull Constructor<T> p_constructor,
																 @Nullable T p_value)
					throws IOException {
		this(p_options, p_name, p_constructor, p_value, null);
	}

	/**
	 *
	 * @param p_options
	 * @param p_name
	 * @param p_constructor
	 * @param p_condition
	 *
	 * @throws IOException Never thrown.
	 */
	public SimulationOptionSetting(@NotNull SimulationOptions2 p_options,
																 @NotNull String p_name,
																 @NotNull Constructor<T> p_constructor,
																 @Nullable Predicate<T> p_condition)
					throws IOException {
		this(p_options, p_name, p_constructor, null, p_condition);
	}

	/**
	 *
	 * @param p_options
	 * @param p_name
	 * @param p_constructor
	 * @param p_value
	 * @param p_condition
	 *
	 * @throws IOException Thrown if the value does not fulfil the condition.
	 */
	@SuppressWarnings("LeakingThisInConstructor")
	public SimulationOptionSetting(@NotNull SimulationOptions2 p_options,
																 @NotNull String p_name,
																 @NotNull Constructor<T> p_constructor,
																 @Nullable T p_value,
																 @Nullable Predicate<T> p_condition)
					throws IOException {
		if (p_condition == null || p_value == null || p_condition.test(p_value)) {
			this.value = p_value;
		} else {
			throw new IOException("Value is Invalid under the Condition.");
		}
		this.name = p_name;
		this.condition = p_condition;
		this.constructor = p_constructor;

		p_options.settings.put(name, this);
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
	@Nullable
	public T get() {
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
	public boolean isName(@Nullable String p_name) {
		return name.equalsIgnoreCase(p_name);
	}

	/**
	 *
	 * @param p_value
	 *
	 * @throws IOException
	 */
	public void set(@NotNull T p_value)
					throws IOException {
		if (condition == null || condition.test(p_value)) {
			this.value = p_value;
		} else {
			throw new IOException("Value is Invalid under the Condition.");
		}
	}

	public void setFromString(String str)
					throws IOException {
		this.value = constructor.read(str);
	}

	public static interface Constructor<T> {
		public T read(String str)
						throws IOException;
	}
}
