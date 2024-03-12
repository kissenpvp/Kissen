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

package net.kissenpvp.core.api.base;

/**
 * Signals that an implementation is absent for an interface file.
 * This exception is thrown when an interface file exists but does not have a child class extending it
 * and defining the necessary code properties.
 *
 * <p>This exception is a subclass of {@code IllegalStateException}, indicating that the program's
 * current state is illegal due to the absence of a required implementation for an interface.
 *
 * <p>Typically, this exception is thrown when attempting to use a specific interface that should have
 * an implementation class associated with it, but no such implementation is found.
 *
 * <p>Example usage:
 * <pre>{@code
 * try {
 *     getImplementation(SomeInterface.class);
 * } catch (ImplementationAbsentException e) {
 *     System.err.println("Error: The implementation for SomeInterface is missing.");
 * }
 * }</pre>
 */
public class ImplementationAbsentException extends IllegalStateException {
}
