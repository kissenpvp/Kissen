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
