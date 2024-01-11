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

package net.kissenpvp.core.api.event;

import net.kissenpvp.core.api.base.Implementation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Set;


/**
 * The {@code EventImplementation} interface provides methods for managing and triggering events in the plugin
 * architecture.
 * <p>
 * Events are used to represent actions or states in the server, such as a player joining or leaving the game. Plugins
 * can register to receive events and take actions in response to them.
 * <p>
 * This interface extends the {@link Implementation} interface, which provides methods for managing plugin
 * implementations. By implementing this interface, plugins can manage events and trigger them when necessary.
 */
public interface EventImplementation extends Implementation
{
    /**
     * Returns the set of all loaded event listeners for this plugin.
     *
     * <p>
     * This method retrieves the set of event listeners that have been loaded. Event listeners are components
     * that are designed to respond to specific events within the plugin's execution flow. They are registered with
     * the plugin
     * and can be triggered when the corresponding events occur.
     * </p>
     *
     * @return an unmodifiable set of all loaded event listeners for this plugin.
     * @throws UnsupportedOperationException if the plugin does not support event listeners.
     */
    @Unmodifiable @NotNull Set<?> getEventListener() throws UnsupportedOperationException;

    /**
     * Registers an event listener.
     *
     * <p>
     * This method allows registering an event listener. The event listener will be added to the set of
     * registered event listeners. When an event is triggered, all registered event listeners will be notified
     * and can respond accordingly.
     * </p>
     *
     * @param eventListener the event listener to register.
     * @throws NullPointerException if the eventListener is {@code null}.
     */
    void registerEvent(@NotNull EventListener<?>... eventListener) throws NullPointerException;

    /**
     * Triggers an event by calling all registered event listeners for that event.
     *
     * <p>
     * This method triggers an event by invoking all registered event listeners associated with the event. The
     * registered event
     * listeners will be invoked in the order they were registered. If an event listener cancels the event by
     * implementing the
     * {@link Cancellable} interface and returning {@code false}, the event execution will be stopped, and the method
     * will return {@code false}.
     * If no event listener cancels the event or the event does not implement the {@link Cancellable} interface, the
     * method
     * will return {@code true}.
     * </p>
     *
     * @param event the event to trigger.
     * @return {@code true} if the event should be executed, {@code false} if it should be cancelled.
     * This only applies to events that implement the {@link Cancellable} interface; other events will always return
     * {@code true}.
     * @throws IllegalArgumentException if the event is not of the correct type for any registered event listeners.
     * @throws NullPointerException     if the event is {@code null}.
     */
    <T extends EventClass> boolean call(@NotNull T event) throws IllegalArgumentException, NullPointerException;
}
