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
import net.kissenpvp.core.api.permission.PermissionEntry;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an event that is triggered when all permissions of a specific entry are cleared.
 * This event can be cancelled, preventing the permissions from being cleared.
 *
 * <p>This event extends the {@link EventClass} and {@link Cancellable} interfaces,
 * providing access to common event functionality and cancellation capabilities.</p>
 *
 * <p>Implementations of this event should handle the necessary actions when all permissions
 * of a specific entry are cleared, such as updating the entry's permission settings or
 * notifying relevant parties about the clearance.</p>
 *
 * <p>Canceling this event will prevent the permissions from being cleared.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 * {@code
 * public class PermissionEntryClearListener implements EventListener (generic type = PermissionEntryClearEvent) {
 *     public void call(PermissionEntryClearEvent event) {
 *         // Add custom logic here
 *     }
 * }
 * }</pre>
 *
 * @see EventClass
 * @see Cancellable
 * @see PermissionEntry#wipePermissions()
 */
public interface PermissionEntryClearEvent extends EventClass, Cancellable {
    @NotNull PermissionEntry<?> getPermissionEntry();
}
