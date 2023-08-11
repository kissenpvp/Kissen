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
import net.kissenpvp.core.api.permission.Permission;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an event that is triggered when a permission option is deleted.
 * This event can be canceled, preventing the permission option from being deleted.
 *
 * <p>This event extends the {@link EventClass} and {@link Cancellable} interfaces,
 * providing access to common event functionality and cancellation capabilities.</p>
 *
 * <p>Implementations of this event should handle the necessary actions when a permission
 * option is deleted, such as updating related permissions or notifying relevant parties.</p>
 *
 * <p>Canceling this event will prevent the permission option from being deleted.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 * {@code
 * public class PermissionOptionDeleteListener implements EventListener (generic type = PermissionOptionDeleteEvent)
 * {
 *     public void call(PermissionOptionDeleteEvent event)
 *     {
 *         // Add custom logic here
 *     }
 * }
 * }</pre>
 *
 * @see EventClass
 * @see Cancellable
 * @see Permission#deleteOption(String)
 */
public interface PermissionOptionDeleteEvent extends EventClass, Cancellable
{
    @NotNull String getKey();

    @NotNull String getOption();

    @NotNull Permission getPermission();
}
