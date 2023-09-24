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
public class EventCancelledException extends RuntimeException
{
    public EventCancelledException() {
    }

    public EventCancelledException(String message) {
        super(message);
    }

    public EventCancelledException(String message, Throwable cause) {
        super(message, cause);
    }

    public EventCancelledException(Throwable cause) {
        super(cause);
    }

    public EventCancelledException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
