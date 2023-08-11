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

package net.kissenpvp.core.api.permission;

import net.kissenpvp.core.api.event.EventCancelledException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.time.Duration;
import java.util.Map;


public interface Permission {
    @NotNull String getName();

    @NotNull PermissionEntry<?> getOwner();

    boolean getValue();

    void setValue(boolean value) throws EventCancelledException;

    long getStart();

    @Nullable Duration getDuration();

    long getEnd();

    void setEnd(long end) throws EventCancelledException;

    long getPredictedEnd();

    boolean isValid();

    void setOption(@NotNull String key, @NotNull String data) throws EventCancelledException;

    boolean deleteOption(@NotNull String key) throws EventCancelledException;

    @Nullable String getOption(@NotNull String key);

    @Unmodifiable @NotNull Map<String, String> getDefinedOptions();
}
