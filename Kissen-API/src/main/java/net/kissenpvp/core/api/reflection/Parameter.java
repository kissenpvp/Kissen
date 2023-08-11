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

package net.kissenpvp.core.api.reflection;


import org.jetbrains.annotations.NotNull;

public record Parameter<T>(@NotNull Class<T> type, @NotNull T value) {
    public static <T> @NotNull Parameter<T> parameterize(@NotNull T object) {
        return new Parameter<>((Class<T>) object.getClass(), object);
    }

    public static <T> @NotNull Parameter<T> parameterizeUnknownClass(@NotNull Class<T> type, @NotNull Object object) {
        return new Parameter<>(type, (T) object);
    }
}
