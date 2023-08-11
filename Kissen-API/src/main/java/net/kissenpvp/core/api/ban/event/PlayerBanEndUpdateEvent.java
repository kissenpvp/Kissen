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

import net.kissenpvp.core.api.ban.PlayerBan;
import net.kissenpvp.core.api.event.Cancellable;
import net.kissenpvp.core.api.event.EventClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * The `PlayerBanEndUpdateEvent` interface represents an event that is triggered when a player ban end date is being updated.
 * It extends the `EventClass` and `Cancellable` interfaces.
 *
 * <p>This event provides the ability to listen and handle the event when a player ban end date is being updated.
 * Implementations of this interface can be used to perform custom actions in response to the event.</p>
 *
 * <p>Note: This event can be cancelled, preventing the player ban end date from being updated.</p>
 * <p>
 * Extending Interfaces:
 * - `EventClass`: Provides basic functionality for an event.
 * - `Cancellable`: Allows the event to be cancelled.
 * <p>
 * Event Flow:
 * When a player ban end date is about to be updated, the `PlayerBanEndUpdateEvent` is fired.</p>
 * <p>Listeners can intercept this event, perform necessary operations, and potentially cancel the event.</p>
 * <p>If the event is not cancelled, the player ban end date will be updated.</p>
 * <p>
 * Cancellation:
 * The event can be cancelled by calling the `setCancelled(boolean)` method with a value of `true`.
 * This will prevent the player ban end date from being updated.</p>
 * <p>Cancelling the event will cause subsequent event listeners to be skipped.</p>
 * <p>The cancelled status of the event can be checked by calling the `isCancelled()` method.</p>
 *
 * Example Usage:
 * <pre>{@code
 *   public class PlayerBanEndUpdateListener implements EventListener (generic type = PlayerBanEndUpdateEvent)
 *   {
 *       public void call(PlayerBanEndUpdateEvent event)
 *       {
 *           // Add custom logic here
 *       }
 *   }
 * }</pre>
 *
 * @see EventClass
 * @see Cancellable
 * @see PlayerBan
 */
public interface PlayerBanEndUpdateEvent extends EventClass, Cancellable
{
    @NotNull PlayerBan getPlayerBan();

    @NotNull Optional<@Nullable Long> getEnd();

    void setEnd(@NotNull Long end);
}
