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

package net.kissenpvp.core.permission.event;

import lombok.Getter;
import lombok.Setter;
import net.kissenpvp.core.api.permission.PermissionEntry;
import net.kissenpvp.core.api.permission.event.PermissionEntryClearEvent;
import org.jetbrains.annotations.NotNull;

public class KissenPermissionEntryClearEvent implements PermissionEntryClearEvent
{
    @Getter private final PermissionEntry<?> permissionEntry;
    @Getter @Setter private boolean cancelled;

    public KissenPermissionEntryClearEvent(@NotNull PermissionEntry<?> permissionEntry)
    {
        this.permissionEntry = permissionEntry;
        this.cancelled = false;
    }
}
