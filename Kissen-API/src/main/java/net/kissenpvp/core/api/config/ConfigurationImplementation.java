/*
 * Copyright 2023 KissenPvP
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.kissenpvp.core.api.config;

import net.kissenpvp.core.api.base.Implementation;
import net.kissenpvp.core.api.base.plugin.KissenPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * The {@link ConfigurationImplementation} interface defines the methods that a controller for managing system configuration should implement.
 * It extends the {@link Implementation} interface, which specifies that it is an implementation of a system component.
 * <p>
 * The {@link ConfigurationImplementation} interface provides methods for retrieving and modifying the system configuration.
 * Configuration values can be retrieved either individually by class.
 * <p>
 * This interface is designed to be implemented by a class that manages system configuration for a system.
 * It can be used by other components of the system to interact with the configuration controller and retrieve or modify system configuration values.
 * <p>
 * This interface should be used in conjunction with the {@link Option} class, which represents a single configuration value and provides methods for modifying or deleting the configuration value.
 * @see Implementation
 * @see Option
 */
public interface ConfigurationImplementation extends Implementation
{
    /**
     * Returns the value of the system configuration option that is associated with the specified class.
     * The class must extend the {@link Option} interface, which specifies the type of the configuration option's value.
     * This method retrieves the option by calling the {@link #getOption(Class)} method and returns the option's
     * value by calling its {@link Option#getValue()} method.
     *
     * @param <T>   the type of the configuration option's value
     * @param clazz the class of the system configuration option to retrieve
     * @return the value of the system configuration option associated with the specified class, represented as an
     * instance of type {@code T}
     * @throws UnregisteredException if the option was not registered.
     * @throws NullPointerException  if {@code clazz} is {@code null}
     * @see Option
     * @see #getOption(Class)
     * @see Option#getValue()
     */
    default <T> @NotNull T getSetting(@NotNull Class<? extends Option<T>> clazz) throws UnregisteredException
    {
        return getOption(clazz).getValue();
    }

    /**
     * Returns the system configuration option that is associated with the specified class.
     * The class must extend the {@link Option} interface, which specifies the type of the configuration option's value.
     * <p>
     * This method retrieves the option from the system's configuration.
     * The retrieved option is then returned as an instance of {@link Option} representing the system configuration
     * option associated with the specified class.
     *
     * @param <T>   the type of the configuration option's value
     * @param clazz the class of the system configuration option to retrieve
     * @return an instance of {@link Option} representing the system configuration option associated with the
     * specified class,
     * or {@code null} if the option does not exist
     * @throws UnregisteredException if the option does not exist
     * @throws NullPointerException  if {@code clazz} is {@code null}
     * @see Option
     */
    <T> @NotNull Option<T> getOption(@NotNull Class<? extends Option<T>> clazz) throws UnregisteredException;

    /**
     * Registers a system configuration option with the implementation's configuration store.
     * The option is associated with the specified plugin and option class, which must extend the {@link Option} interface.
     * <p>
     * This method registers the option.
     * The option is then associated with the specified plugin and option class, and is available for use by the system.
     * <p>
     * Note that if either the specified plugin or option class is null, this method will throw a {@link NullPointerException}.
     * @param kissenPlugin the plugin associated with the system configuration option
     * @param optionNode the class of the system configuration option to register
     * @throws NullPointerException if either the specified plugin or option class is {@code null}
     * @see Option
     */
    void registerOption(@NotNull KissenPlugin kissenPlugin, @NotNull Class<? extends Option<?>> optionNode);
}
