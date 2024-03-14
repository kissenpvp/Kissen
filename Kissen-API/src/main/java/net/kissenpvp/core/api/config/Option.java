/*
 * Copyright (C) 2023 KissenPvP
 *
 * This program is licensed under the Apache License, Version 2.0.
 *
 * This software may be redistributed and/or modified under the terms
 * of the Apache License as published by the Apache Software Foundation,
 * either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the Apache
 * License, Version 2.0 for the specific language governing permissions
 * and limitations under the License.
 *
 * You should have received a copy of the Apache License, Version 2.0
 * along with this program. If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

package net.kissenpvp.core.api.config;

import net.kissenpvp.core.api.base.serializer.InstancedSerializer;
import net.kissenpvp.core.api.base.serializer.InstancedTextSerializer;
import net.kissenpvp.core.api.database.file.FileEditor;
import org.jetbrains.annotations.NotNull;

/**
 * The {@link Option} interface represents a system configuration option that has a specific type and a value.
 * <p>
 * This option can be received using {@link ConfigurationImplementation#getOption(Class)} and its value with
 * {@link ConfigurationImplementation#getSetting(Class)}.
 * <p>
 * This interface provides methods for getting and setting the value of the configuration option.
 * The value is of the generic type {@code <T>}, which is specified by the implementing class.
 * <p>
 * This interface should be used in conjunction with the {@link ConfigurationImplementation} interface,
 * which provides methods for storing and retrieving system configuration options.
 * <p>
 * Most commonly, a custom option should extend {@link AbstractOption}, which already has some methods implemented to
 * make it easier.
 *
 * @param <T> the type of the value of the configuration option
 * @see ConfigurationImplementation
 * @see AbstractOption
 */
public interface Option<T, S> extends InstancedSerializer<T, S> {

    boolean isInitialized();

    /**
     * Returns the group of the configuration option. The group is used to specify where the option should be placed
     * in the configuration file.
     * This value is used when sorting the configuration file, which helps with organization and readability.
     * <p>
     * For example, if you have a configuration file with options related to networking, you may want to group those
     * options together
     * under a "network" group to make it easier to locate them. Similarly, if you have options related to graphics
     * settings, you may want
     * to group them under a "graphics" group.
     * <p>
     * The group should be a non-null string that represents the name of the group. The name should be concise and
     * descriptive, and it should
     * help to identify the category or purpose of the option. For instance, if the option is related to email
     * settings, an appropriate
     * group name could be "email".
     *
     * @return the name of the group associated with this option
     * @throws NullPointerException if the group name is null
     */
    @NotNull String getGroup();

    /**
     * Returns the key with which the value of the configuration option is stored in the file.
     *
     * <p>
     * If the option extends the {@link AbstractOption} class, the code is generated based on the simple name
     * of the implementing class.
     *
     * <p>
     * This method returns a non-null string representing the path where the value is stored in the file.
     *
     * @return The path of the value in the file.
     * @throws NullPointerException If the path is null.
     */
    @NotNull String getCode();

    /**
     * This method returns a description of the configuration option, explaining what the option does.
     * The description should provide enough information to allow the user to understand the purpose and
     * effect of the option.
     *
     * <p>
     * This method returns a non-null string representing the description of the setting.
     *
     * @return The description of the setting.
     * @throws NullPointerException If the description is null.
     */
    @NotNull String getDescription();

    /**
     * This method returns the priority of the configuration option. The priority is used to determine the order
     * in which the options should be listed when displayed to the user or when written to a file.
     *
     * <p>
     * The priority is returned as an integer value, where higher values indicate lower priority. The priority value
     * is used for sorting purposes.
     *
     * @return The priority of the option as an integer value.
     */
    int getPriority();

    /**
     * Returns the default value of the configuration option.
     * This value is specified in the configuration file when it is started for the first time.
     *
     * <p>
     * Note that the default value is of the same generic type {@code <T>} as the option's value.
     *
     * <p>
     * This method returns a non-null value representing the default value of the option.
     *
     * @return The default value of the configuration option.
     */
    @NotNull T getDefault();

    /**
     * Returns the current value of the configuration option. If the value is not yet loaded, this method will load it
     * before returning.
     *
     * <p>
     * Note that the returned value will never be null.
     *
     * @return The current value of the configuration option.
     * @see #setValue(Object)
     */
    @NotNull T getValue();

    /**
     * Sets the value of the configuration option to the specified value and saves it in the configuration file.
     * Note that the new value is not immediately saved to the file. To save the value, you must call the
     * {@link #override(Object, FileEditor)} method.
     *
     * <p>
     * This method should be used to change the value of the configuration option and will be returned by
     * {@link #getValue()}.
     *
     * <p>
     * The new value to be set must be of the same type as the option. If the value is null, a
     * {@link NullPointerException} will be thrown.
     *
     * @param value The new value of the configuration option to be set.
     * @throws NullPointerException If the value is null.
     * @see #getValue()
     * @see #override(Object, FileEditor)
     */
    void setValue(@NotNull T value);

    /**
     * Sets the value of the configuration option to the given value and saves it in the configuration.
     * This method should only be used when the type of the new value is unknown.
     * <p>
     * Note that the new value is not immediately saved to the configuration file. In order to save the value,
     * {@link #override(Object, FileEditor)} must be called.
     * <p>
     * The new value to be set must be convertible to the same type as the option. If the value is not convertible to
     * the same type, a {@link ClassCastException} will be thrown.
     *
     * @param value The new value of the configuration option to be set.
     * @throws NullPointerException If the value is null.
     * @throws ClassCastException   If the new value is not convertible to the same type as the option.
     */
    void setUnsafe(@NotNull Object value) throws ClassCastException;

    /**
     * Saves the given value of the configuration option to the specified configuration file.
     * This method will call {@link #setValue(Object)} to update the setting as well.
     * <p>
     * Note that this method overrides the current value of the option in the file with the given value, regardless
     * of its previous value.
     *
     * @param value      The value to be saved in the configuration file.
     * @param fileEditor The file where the value is to be saved.
     * @throws NullPointerException If either the value or file parameter is null.
     */
    void override(@NotNull T value, @NotNull FileEditor fileEditor);
}
