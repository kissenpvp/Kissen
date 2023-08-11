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
public interface EventClass { }
