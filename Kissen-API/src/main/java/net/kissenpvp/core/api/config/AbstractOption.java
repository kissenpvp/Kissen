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

import net.kissenpvp.core.api.database.file.FileEditor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.IntFunction;
import java.util.stream.Stream;

/**
 * The OptionDefault class is an abstract implementation of the Option interface,
 * which provides a default implementation for certain methods, such as setting and getting the value of the option,
 * as well as loading and saving the option from and to the configuration file.
 * <p>
 * <p>
 * The OptionDefault class also provides a default implementation for the {@link #getCode()} method,
 * which returns the simple name of the option's class as the default value for the code.
 *
 * <p>
 * Concrete option classes should extend this class and provide their own implementation for the
 * {@link #getDefault()} method,
 * which specifies the default value for the option and the {@link #getGroup()} method, which specifies the group in
 * the configuration file.
 *
 * @param <T> The type of the option value.
 * @see Option
 */
public abstract class AbstractOption<T, S> implements Option<T, S>
{

    private T value;
    private boolean initialized = false;

    /**
     * Parses the option name by converting it to a format suitable for parsing.
     *
     * <p>This method takes an option name as input and converts it to a format suitable for parsing.
     * It  converts uppercase characters to lowercase, preceded by a hyphen. The resulting string is then returned.</p>
     *
     * @param name the option name to be parsed
     * @return the parsed option name
     * @throws NullPointerException if the specified option name is `null`
     */
    protected @NotNull String parseOptionName(@NotNull String name) {
        Stream<String> optionNameStream = name.chars().mapToObj(replaceCode());
        return optionNameStream.collect(StringBuilder::new, combine(), StringBuilder::append).toString();
    }

    /**
     * Creates a {@link BiConsumer} that combines strings by appending them to a {@link StringBuilder}.
     *
     * <p>This method returns a {@link BiConsumer} that combines two strings by appending the second string to the end of the
     * first string. If the first string is empty and the second string starts with a hyphen, the hyphen is removed before
     * appending.</p>
     *
     * @return a {@link BiConsumer} that combines strings
     */
    @Contract(pure = true, value = "-> new")
    private static @NotNull BiConsumer<StringBuilder, String> combine() {
        return (stringBuilder, string) -> {
            boolean remove = stringBuilder.isEmpty() && string.startsWith("-");
            stringBuilder.append(string.substring(remove ? 1 : 0));
        };
    }

    /**
     * Creates an {@link IntFunction} that replaces characters based on certain conditions.
     *
     * <p>This method returns an {@link IntFunction} that takes an integer representing a character and returns a string.
     * It converts uppercase characters to lowercase and prefixes them with a hyphen. If the character is already lowercase,
     * it returns the character as a string.</p>
     *
     * @return an {@link IntFunction} that replaces characters
     */
    @Contract(pure = true, value = "-> new")
    private static @NotNull IntFunction<String> replaceCode() {
        return currentChar -> {
            char loweredChar = Character.toLowerCase((char) currentChar);
            boolean isUpperCase = Character.isUpperCase((char) currentChar);
            if (isUpperCase) {
                return "-" + loweredChar;
            }
            return String.valueOf(loweredChar);
        };
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public @NotNull String getCode()
    {
        return parseOptionName(getClass().getSimpleName());
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public @NotNull T getValue() {
        return value == null ? getDefault() : value;
    }

    @Override
    public void setValue(@NotNull T value) {
        if (Objects.isNull(this.value)) {
            initialized = true;
        }
        this.value = value;
    }

    @Override
    public void setUnsafe(@NotNull Object value) throws ClassCastException {
        setValue((T) value);
    }

    @Override
    public void override(@NotNull T value, @NotNull FileEditor fileEditor)
    {
        setValue(value);
        fileEditor.write(getCode(),
                value + " # [" + getDefault().getClass().getSimpleName().toLowerCase(Locale.ENGLISH) + "]",
                getDescription());
    }

    @Override
    public @NotNull S serialize()
    {
        return serialize(getValue());
    }
}
