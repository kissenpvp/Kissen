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
import net.kissenpvp.core.api.permission.Permission;
import org.jetbrains.annotations.NotNull;

public class KissenPermissionValueUpdateEvent implements net.kissenpvp.core.api.permission.event.PermissionValueUpdateEvent
{
    @Getter private final Permission permission;
    @Getter @Setter private boolean value;
    @Getter @Setter private boolean cancelled;

    public KissenPermissionValueUpdateEvent(@NotNull Permission permission, boolean value)
    {
        this.permission = permission;
        this.value = value;
        this.cancelled = false;
    }
}
