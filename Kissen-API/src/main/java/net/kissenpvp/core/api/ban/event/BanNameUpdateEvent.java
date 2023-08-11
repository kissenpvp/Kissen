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

package net.kissenpvp.core.api.ban.event;

import net.kissenpvp.core.api.ban.Ban;
import net.kissenpvp.core.api.event.Cancellable;
import net.kissenpvp.core.api.event.EventClass;

/**
 * The `BanNameUpdateEvent` interface represents an event that is triggered when the name of a ban is being updated.
 * It extends the `EventClass` and `Cancellable` interfaces.
 * <p>
 * This event provides the ability to listen and handle the event when the name of a ban is being updated.
 * Implementations of this interface can be used to perform custom actions in response to the event.
 * <p>
 * Note: This event can be cancelled, preventing the name of the ban from being updated.
 * <p>
 * Extending Interfaces:
 * - `EventClass`: Provides basic functionality for an event.
 * - `Cancellable`: Allows the event to be cancelled.
 * <p>
 * Event Flow:
 * When the name of a ban is about to be updated, the `BanNameUpdateEvent` is fired.
 * Listeners can intercept this event, perform necessary operations, and potentially cancel the event.
 * If the event is not cancelled, the name of the ban will be updated.
 * <p>
 * Cancellation:
 * The event can be cancelled by calling the `setCancelled(boolean)` method with a value of `true`.
 * This will prevent the name of the ban from being updated.
 * Cancelling the event will cause subsequent event listeners to be skipped.
 * The cancelled status of the event can be checked by calling the `isCancelled()` method.
 * <p>
 * Example Usage:
 * <pre>{@code
 *     public class BanNameUpdateListener implements EventListener (generic type = BanNameUpdateEvent)
 *     {
 *         public void call(@NotNull BanNameUpdateEvent event)
 *         {
 *             // Add custom logic here
 *         }
 *     }
 * }
 * </pre>
 *
 * @see EventClass
 * @see Cancellable
 * @see Ban
 */
public interface BanNameUpdateEvent extends EventClass, Cancellable
{
    Ban getBan();

    String getName();

    void setName(String name);
}
