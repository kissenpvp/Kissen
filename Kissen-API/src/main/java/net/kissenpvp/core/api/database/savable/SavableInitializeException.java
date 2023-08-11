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

package net.kissenpvp.core.api.database.savable;

/**
 * The {@code SavableInitializeException} class is an exception that is thrown when a key returned by a specific
 * implementation is required but does not exist. This exception is typically thrown in scenarios where a savable
 * object represents a synchronized map that is connected to a database, and the required key is missing.
 *
 * <p>
 * The exception extends the {@link IllegalStateException} class, indicating that the object's state is illegal due
 * to the missing key.
 * </p>
 *
 * @see Savable
 * @see SavableMap
 * @see Savable#getKeys()
 */
public class SavableInitializeException extends IllegalStateException
{ }
