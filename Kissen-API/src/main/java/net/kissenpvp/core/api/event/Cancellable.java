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

/**
 * The {@code Cancellable} interface provides methods for determining and setting the cancellation state of an event.
 * <p>
 * An event can be cancelled if some condition is met, such as an error occurring or a user input being invalid. If an
 * event is cancelled, it will not be executed in the server, but it may still be passed to other plugins for processing.
 * <p>
 * Implementing this interface indicates that an event can be cancelled and provides the ability to set and retrieve the
 * cancellation state using the {@link #setCancelled(boolean)} and {@link #isCancelled()} methods, respectively.
 */
public interface Cancellable {

    /**
     * Gets the cancellation state of this event.
     * <p>
     * This method returns {@code true} if the event is cancelled and {@code false} otherwise. If an event is cancelled,
     * it will not be executed in the server, but it may still be passed to other plugins for processing.
     *
     * @return {@code true} if this event is cancelled, {@code false} otherwise.
     */
    boolean isCancelled();

    /**
     * Sets the cancellation state of this event.
     * <p>
     * If {@code cancel} is {@code true}, the event will be cancelled and will not be executed in the server, but it may
     * still be passed to other plugins for processing. If {@code cancel} is {@code false}, the event will not be
     * cancelled and will be executed in the server as normal.
     *
     * @param cancel {@code true} if you wish to cancel this event, {@code false} otherwise.
     */
    void setCancelled(boolean cancel);
}
