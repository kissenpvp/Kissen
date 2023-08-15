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

package net.kissenpvp.core.api.user.usersetttings;

import net.kissenpvp.core.api.base.serializer.TextSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface PlayerSetting<T> extends TextSerializer<T> {
    @NotNull String getKey();

    @NotNull T getDefaultValue();

    @NotNull UserValue<T>[] getPossibleValues();

    default @NotNull Optional<String> getPermission() {
        return Optional.empty();
    }

    default void setValue(@Nullable T value) {
    }
}
