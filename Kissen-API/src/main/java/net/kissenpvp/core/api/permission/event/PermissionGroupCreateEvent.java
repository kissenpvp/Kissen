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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Represents an event that is triggered when a {@link net.kissenpvp.core.api.permission.PermissionGroup} creation is attempted.
 * This event can be canceled, preventing the permission group from being created.
 *
 * <p>This event extends the {@link EventClass} and {@link Cancellable} interfaces,
 * providing access to common event functionality and cancellation capabilities.</p>
 *
 * <p>Implementations of this event should handle the necessary actions when a permission
 * group creation is attempted, such as validating permissions or notifying relevant parties.</p>
 *
 * <p>Canceling this event will prevent the permission group from being created.</p>
 *
 * <p>
 *     Note that this event is NOT called when a {@link net.kissenpvp.core.api.permission.PermissionGroup} is created.
 *     These cases include on the server start when they are being cached, or the creation is initialized by another server.
 *     However, {@link PermissionGroupCreatedEvent} will always be called.
 * </p>
 *
 * <p>Example usage:</p>
 * <pre>
 * {@code
 * public class PermissionGroupCreateListener implements EventListener  (generic type = PermissionGroupCreateEvent)
 * {
 *     public void call(PermissionGroupCreateEvent event)
 *     {
 *         // Add custom logic here
 *     }
 * }
 * }</pre>
 *
 * @see EventClass
 * @see Cancellable
 * @see net.kissenpvp.core.api.permission.PermissionGroup
 */
public interface PermissionGroupCreateEvent extends EventClass, Cancellable
{
    @NotNull String getName();

    @Nullable Map<String, String> getData();

    void setData(@Nullable Map<String, String> data);
}
