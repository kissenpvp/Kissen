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

package net.kissenpvp.core.api.permission.event;

import net.kissenpvp.core.api.event.Cancellable;
import net.kissenpvp.core.api.event.EventClass;
import net.kissenpvp.core.api.permission.Permission;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an event that is triggered when a permission is unset for a specific entry.
 * This event can be canceled, preventing the permission from being unset.
 *
 * <p>This event extends the {@link EventClass} and {@link Cancellable} interfaces,
 * providing access to common event functionality and cancellation capabilities.</p>
 *
 * <p>Implementations of this event should handle the necessary actions when a permission
 * is unset for a specific entry, such as updating the entry's permission settings or
 * notifying relevant parties about the change.</p>
 *
 * <p>Canceling this event will prevent the permission from being unset.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 * {@code
 * public class PermissionEntryUnsetPermissionListener implements EventListener (generic type = PermissionEntryUnsetPermissionEvent)
 * {
 *     public void call(PermissionEntryUnsetPermissionEvent event)
 *     {
 *         // Add custom logic here
 *     }
 * }
 * }</pre>
 *
 * @see EventClass
 * @see Cancellable
 * @see net.kissenpvp.core.api.permission.PermissionEntry#unsetPermission(String)
 */
public interface PermissionEntryUnsetPermissionEvent extends EventClass, Cancellable
{
    @NotNull Permission getPermission();
}
