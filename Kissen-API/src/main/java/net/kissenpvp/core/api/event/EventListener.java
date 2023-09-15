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


import org.jetbrains.annotations.NotNull;


/**
 * This interface represents an event listener that can be registered with the {@link EventImplementation}.
 *
 * <p>
 * The generic type {@code T} is the type of the event that should be listened to by this event listener. Event
 * listeners
 * implement this interface to respond to specific events within the application's execution flow. By registering an
 * event listener with the {@link EventImplementation}, it becomes eligible to receive and handle events of the
 * specified type.
 * </p>
 *
 * @param <T> the type of the event to be listened to by this event listener
 */
public interface EventListener<T extends EventClass> {

    /**
     * Called when an event of the specified type is triggered. The event class is unknown in this interface,
     * but is known when the event is registered with the {@link EventImplementation}.
     *
     * <p>
     * This method is invoked when an event of the specified type occurs. The event object is passed as a parameter,
     * providing the necessary data and context for the event listener to perform its actions or respond to the event.
     * Event listeners should implement their logic within this method to handle the event accordingly.
     * </p>
     *
     * @param event the event that was triggered
     */
    void call(@NotNull T event);
}

