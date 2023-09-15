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
