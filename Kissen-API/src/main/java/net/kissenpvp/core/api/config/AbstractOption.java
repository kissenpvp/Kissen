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

import net.kissenpvp.core.api.database.file.FileEditor;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

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
public abstract class AbstractOption<T> implements Option<T>
{

    private T value;

    @Override
    public @NotNull String getCode()
    {
        return getClass().getSimpleName().toLowerCase();
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

    @Override public @NotNull String serialize()
    {
        return serialize(getValue());
    }
}
