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
    @NotNull PermissionGroup<?> getPermissionGroup();
}
