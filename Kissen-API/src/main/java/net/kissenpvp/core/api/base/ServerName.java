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

package net.kissenpvp.core.api.base;

import net.kissenpvp.core.api.config.AbstractOption;
import net.kissenpvp.core.api.config.options.OptionString;
import org.jetbrains.annotations.NotNull;

/**
 * The {@code ServerName} class extends the {@link OptionString} class and represents an option for specifying the
 * server name.
 * It provides a default implementation for setting and getting the value of the server name option,
 * as well as loading and saving the option from and to the configuration file.
 * <p>
 * To use this option, you can retrieve an instance of the {@code ServerName} class by calling the
 * {@link net.kissenpvp.core.api.config.ConfigurationImplementation#getOption(Class)} method,
 * passing the {@code ServerName} class as the parameter. This will give you access to the methods for setting and
 * getting the value of the server name option.
 * <p>
 * Alternatively, you can directly retrieve the value of the server name option by calling the
 * {@link net.kissenpvp.core.api.config.ConfigurationImplementation#getSetting(Class)} method,
 * again passing the {@code ServerName} class as the parameter. This method will return the current value of the
 * server name option.
 * </p>
 *
 * <p>
 * The {@code ServerName} class also overrides the following methods from the {@link AbstractOption} class:
 * </p>
 * <ul>
 *     <li>{@link net.kissenpvp.core.api.config.Option#getGroup()} - Returns the group to which the server name
 *     option belongs.</li>
 *     <li>{@link net.kissenpvp.core.api.config.Option#getDescription()} - Returns the description of the server name
 *     option.</li>
 *     <li>{@link net.kissenpvp.core.api.config.Option#getDefault()} - Returns the default value of the server name
 *     option.</li>
 * </ul>
 *
 * @see OptionString
 * @see net.kissenpvp.core.api.config.ConfigurationImplementation
 */
public class ServerName extends OptionString
{
    @Override public @NotNull String getGroup()
    {
        return "server";
    }

    @Override public @NotNull String getDescription()
    {
        return "The name of the server which is used to identify.";
    }

    @Override public @NotNull String getDefault()
    {
        return "localhost";
    }
}
