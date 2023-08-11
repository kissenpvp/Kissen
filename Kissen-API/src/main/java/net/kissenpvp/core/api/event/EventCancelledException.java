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
 * The {@code EventCancelledException} class is an exception that is thrown when an event has been cancelled.
 * It is a subclass of the {@link Exception} class, indicating that it is a checked exception that needs to be
 * handled or declared.
 *
 * <p>
 * This exception can be used in situations where an event needs to be cancelled and an exception needs to be raised
 * to indicate the cancellation.
 * By throwing this exception, the calling code can be aware that the event has been cancelled and take appropriate
 * action.
 * </p>
 *
 * <p>
 * The {@code EventCancelledException} class provides several constructors to create instances of the exception with
 * different messages and optional cause.
 * This allows for flexibility in providing specific details about the cancellation or capturing the root cause of
 * the cancellation.
 * </p>
 *
 * <p>
 * Note: This exception is a checked exception, which means that it needs to be handled or declared in the calling code.
 * It is recommended to catch this exception and handle it appropriately, or declare it in the method signature using
 * the {@code throws} keyword.
 * </p>
 *
 * @see Exception
 */
public class EventCancelledException extends Exception {}
