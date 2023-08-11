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
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Represents an event that is triggered when a permissions end date is changed.
 * This event can be canceled, preventing the new end date from being applied.
 *
 * <p>This event extends the {@link EventClass} and {@link Cancellable} interfaces,
 * providing access to common event functionality and cancellation capabilities.</p>
 *
 * <p>Implementations of this event should handle the necessary actions when a permission
 * update process is completed, such as applying the updated permissions to the appropriate
 * entities or notifying relevant parties about the changes.</p>
 *
 * <p>Canceling this event will prevent the new end date from being applied.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 * {@code
 * public class PermissionEndUpdateListener implements EventListener  (generic type = PermissionEndUpdateEvent)
 * {
 *     public void call(PermissionEndUpdateEvent event)
 *     {
 *         // Add custom logic here
 *     }
 * }
 * }</pre>
 *
 * @see EventClass
 * @see Cancellable
 * @see Permission#setEnd(long)
 */
public interface PermissionEndUpdateEvent extends EventClass, Cancellable
{
    @NotNull Permission getPermission();

    @NotNull Optional<@Nullable Long> getEnd();

    void setEnd(@Nullable Long end);
}
