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
import net.kissenpvp.core.api.permission.event.PermissionOptionSetEvent;
import org.jetbrains.annotations.NotNull;

public class KissenPermissionOptionSetEvent implements PermissionOptionSetEvent
{
    @Getter private final Permission permission;
    @Getter private final boolean override;
    @Getter private final String key;
    @Getter @Setter private String data;
    @Getter @Setter boolean cancelled;

    public KissenPermissionOptionSetEvent(@NotNull Permission permission, boolean override, @NotNull String key, @NotNull String option)
    {
        this.permission = permission;
        this.override = override;
        this.key = key;
        this.data = option;
        this.cancelled = false;
    }
}
