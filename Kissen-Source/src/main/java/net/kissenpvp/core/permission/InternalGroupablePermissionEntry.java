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

package net.kissenpvp.core.permission;

import net.kissenpvp.core.api.permission.GroupablePermissionEntry;
import net.kissenpvp.core.api.permission.Permission;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface InternalGroupablePermissionEntry<T extends Permission> extends GroupablePermissionEntry<T>
{
    @NotNull Set<T> internalPermissionCollector(@NotNull Set<String> blacklistedGroups, @NotNull Set<String> blacklistedPermissions);

    @NotNull Set<String> internalGroupCollector(@NotNull Set<String> blacklistedGroups);
}
