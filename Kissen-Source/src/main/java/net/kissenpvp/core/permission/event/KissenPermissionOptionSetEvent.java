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

package net.kissenpvp.core.permission.event;

import lombok.Getter;
import lombok.Setter;
import net.kissenpvp.core.api.permission.AbstractPermission;
import net.kissenpvp.core.api.permission.event.PermissionOptionSetEvent;
import org.jetbrains.annotations.NotNull;

@Getter
public class KissenPermissionOptionSetEvent implements PermissionOptionSetEvent
{
    private final AbstractPermission permission;
    private final boolean override;
    private final String key;
    @Setter private String data;
    @Setter boolean cancelled;

    public KissenPermissionOptionSetEvent(@NotNull AbstractPermission permission, boolean override, @NotNull String key, @NotNull String option)
    {
        this.permission = permission;
        this.override = override;
        this.key = key;
        this.data = option;
        this.cancelled = false;
    }
}
