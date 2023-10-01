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

import net.kissenpvp.core.api.event.EventClass;
import net.kissenpvp.core.api.permission.PermissionGroup;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an event that is triggered when a {@link PermissionGroup} was already created.
 *
 * <p>This event extends the {@link EventClass} interface, providing access to
 * common event functionality.</p>
 *
 * <p>Implementations of this event should handle the necessary actions when a
 * permission group is created, such as initializing default permissions or
 * notifying relevant parties about the new group.</p>
 *
 * <p>
 *     Note that the {@link PermissionGroupCreateEvent} is called before this, when this server is the one initializing the creation.
 *     However, if the change is sent by a remote {@link PermissionGroupCreateEvent} is NOT called but this event is.
 * </p>
 *
 * <p>Example usage:</p>
 * <pre>
 * {@code
 * public class PermissionGroupCreatedListener implements EventListener  (generic type = PermissionGroupCreatedEvent)
 * {
 *     public void call(PermissionGroupCreatedEvent event)
 *     {
 *         // Add custom logic here
 *     }
 * }
 * }</pre>
 *
 * @see EventClass
 * @see PermissionGroup
 */
public interface PermissionGroupCreatedEvent extends EventClass
{
    @NotNull PermissionGroup<?> permissionGroup();
}
