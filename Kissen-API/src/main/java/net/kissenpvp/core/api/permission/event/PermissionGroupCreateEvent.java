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
