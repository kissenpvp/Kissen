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

package net.kissenpvp.core.permission;

import net.kissenpvp.core.api.base.Implementation;
import net.kissenpvp.core.api.event.EventCancelledException;
import net.kissenpvp.core.api.permission.AbstractPermission;
import net.kissenpvp.core.api.permission.AbstractPermissionGroup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface InternalPermissionImplementation<T extends AbstractPermission> extends Implementation {

    void addPermission(@NotNull String permission);

    @NotNull
    AbstractPermissionGroup<?> create(@NotNull String name, @Nullable Map<String, Object> data) throws EventCancelledException;

    @NotNull Optional<AbstractPermissionGroup<?>> getPermissionGroupSavable(@NotNull String name);

    @NotNull @Unmodifiable Set<AbstractPermissionGroup<T>> getInternalGroups();

    void removePermissionGroup(@NotNull String name);

}
