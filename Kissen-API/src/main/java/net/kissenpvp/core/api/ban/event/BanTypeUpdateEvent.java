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
import net.kissenpvp.core.api.ban.BanType;
import net.kissenpvp.core.api.event.Cancellable;
import net.kissenpvp.core.api.event.EventClass;
import org.jetbrains.annotations.NotNull;

/**
 *
 * The BanTypeUpdateEvent interface represents an event that is fired when the type of a ban is updated.
 * This event extends the EventClass interface and implements the Cancellable interface.
 * <p>
 *
 * The BanTypeUpdateEvent provides information about the ban and the updated ban type.
 * It also allows cancelling the event and modifying the ban type.
 * </p>
 * <p>
 *
 * This event is typically used in scenarios where the type of a ban needs to be changed, and other components
 * need to be notified of this change.
 * </p>
 *
 * To cancel the event, the {@link Cancellable#setCancelled(boolean)} method can be used, where setting the parameter
 * to {@code true} will prevent further event handlers from being invoked.
 * </p>
 * <p>
 *
 * Note that implementations of this interface should ensure that the {@link #getBan()} and {@link #getBanType()} methods
 * always return non-null values, and the {@link #setBanType(BanType)} method should throw a {@link NullPointerException}
 *
 * if a null BanType object is provided.
 * </p>
 * <p>
 *
 * Example usage:
 * <pre>{@code
 *      public class BanTypeUpdateListener implements EventListener (generic type = BanTypeUpdateEvent)
 *      {
 *           public void call(@NotNull BanTypeUpdateEvent event)
 *           {
 *              // Add custom logic here
 *           }
 *      }
 * }
 * </pre>
 * </p>

 * @see EventClass
 * @see Cancellable
 * @see Ban
 * @see BanType
 */
public interface BanTypeUpdateEvent extends EventClass, Cancellable
{
    /**
     * Retrieves the Ban object associated with the {@link Ban} that is being updated.
     *
     * <p>
     * This method returns the Ban object, which contains information about the ban,
     * excluding details such as the player to be banned or reason.
     * However the template contains information about the ban duration, and any additional actions to be taken.
     * </p>
     *
     * <p>
     * The returned Ban object should never be null.
     * </p>
     *
     * @return the Ban object representing the ban being updated
     * @see Ban
     */
    @NotNull Ban getBan();

    /**
     * Retrieves the BanType object representing the current ban type.
     *
     * <p>
     * This method returns the BanType object that represents the current ban type
     * associated with the ban being updated. The BanType object provides information
     * about the specific type of ban, such as temporary or permanent ban, IP ban,
     * or any custom ban types defined in the system.
     * </p>
     *
     * <p>
     * The returned BanType object should never be null.
     * </p>
     *
     * @return the BanType object representing the current ban type
     * @see BanType
     * @see #setBanType(BanType)
     */
    @NotNull BanType getBanType();

    /**
     * Sets the ban type to the provided BanType object.
     *
     * <p>
     * This method allows modifying the ban type of the ban being updated to the
     * specified BanType object. The BanType object represents the new ban type,
     * which can be a predefined type or a custom type defined in the system.
     * </p>
     *
     * <p>
     * The provided BanType object must not be null.
     * </p>
     *
     * <p>
     * After calling this method, the ban type of the associated ban will be updated
     * to the provided BanType object.
     * </p>
     *
     * <p>
     * Note that this method does not perform any validation or enforcement of the
     * ban type change. It is the responsibility of the calling code to ensure the
     * validity and consistency of the ban type modification.
     * </p>
     *
     * @param banType the BanType object representing the new ban type
     * @see #getBanType()
     */
    void setBanType(@NotNull BanType banType);
}
