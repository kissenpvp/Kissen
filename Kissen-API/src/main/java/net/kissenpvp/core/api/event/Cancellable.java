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
