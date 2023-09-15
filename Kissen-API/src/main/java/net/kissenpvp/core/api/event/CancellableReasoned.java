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
 * The {@code CancellableReasoned} interface extends the {@link Cancellable} interface to provide additional methods for
 * setting and retrieving a reason for cancellation.
 * <p>
 * A cancel reason is a human-readable explanation of why an event was cancelled. Providing a reason for cancellation can
 * be useful for debugging and logging purposes, and can provide important information to the user about why the event was
 * cancelled.
 * <p>
 * Implementing this interface indicates that an event can be cancelled and provides the ability to set and retrieve a
 * cancel reason using the {@link #setCancelReason(String)} and {@link #getCancelReason()} methods, respectively.
 */
public interface CancellableReasoned extends Cancellable {

    /**
     * Sets the reason for cancellation.
     * <p>
     * The provided cancelReason should be a human-readable explanation of why the event was cancelled. It is recommended to
     * include a reason for cancellation when possible, as it can be useful for debugging and logging purposes, and can
     * provide important information to the user about why the event was cancelled.
     * <p>
     * The cancelReason can be any string, but it is recommended to keep it concise and descriptive. If the reason for
     * cancellation is too lengthy or complex, it may be preferable to log or display the reason separately from the
     * cancellation event.
     * <p>
     * If no cancelReason is provided, the cancellation reason will be set to {@code null}. This can be useful for checking
     * if a reason for cancellation has been set or not.
     *
     * @param cancelReason the reason for cancellation, should be a human-readable explanation of why the event was
     *                     cancelled.
     */
    void setCancelReason(String cancelReason);

    /**
     * Returns the reason for cancellation.
     * <p>
     * This method returns a human-readable explanation of why the event was cancelled, if a reason was provided. It is
     * recommended to include a reason for cancellation when possible, as it can be useful for debugging and logging
     * purposes, and can provide important information to the user about why the event was cancelled.
     * <p>
     * If no cancelReason was provided, this method will return {@code null}. This can be useful for checking if a reason
     * for cancellation has been set or not.
     *
     * @return the reason for cancellation, a human-readable explanation of why the event was cancelled, or {@code null} if
     *         no reason was provided.
     */
    String getCancelReason();
}
