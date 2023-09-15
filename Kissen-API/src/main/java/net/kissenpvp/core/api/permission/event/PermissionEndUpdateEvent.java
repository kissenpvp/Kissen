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

    @NotNull Optional<Long> getEnd();

    void setEnd(@Nullable Long end);
}
