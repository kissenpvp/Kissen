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
 * The {@code EventClass} interface is a marker interface that represents an event in the plugin architecture.
 * <p>
 * Events are used to represent actions or states in the server, such as a player joining or leaving the game. Plugins
 * can register to receive events and take actions in response to them.
 * <p>
 * Implementing this interface indicates that a class is intended to be an event and allows plugins to detect and handle
 * instances of the class as events. To create an event, simply create a class that implements this interface and add any
 * necessary fields or methods to represent the event's data.
 * <p>
 * Note that the {@code EventClass} interface itself does not provide any methods or fields for handling events. It
 * serves only as a marker interface to distinguish event classes from other classes in the plugin architecture.
 */
public interface EventClass
{
    default boolean isAsynchronous()
    {
        return false;
    }

    default boolean volatileEvent()
    {
        return false;
    }

    default @NotNull String getEventName()
    {
        return getClass().getSimpleName();
    }
}
