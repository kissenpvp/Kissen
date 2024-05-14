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

import net.kissenpvp.core.api.networking.client.entitiy.ConsoleClient;
import net.kissenpvp.core.api.networking.client.entitiy.ServerEntity;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.UUID;

/**
 * The {@code Kissen} interface represents the core functionality of the Kissen library.
 * It provides methods for accessing the internals, retrieving specific implementations, and obtaining public metadata.
 *
 * <p>Implementations of the {@code Kissen} interface should provide concrete implementations of these methods based
 * on the library's functionality.</p>
 * <p>The Kissen system contains various components called {@link Implementation} which hold information about this
 * system and its features.</p>
 * <p>Note that the specific method to obtain an instance of the {@code Kissen} interface may vary depending on your
 * system.
 * Please refer to your system's manual or documentation for instructions on how to obtain the {@code Kissen}
 * instance.</p>
 *
 * <p>The Kissen library provides the following functionality:</p>
 * <ul>
 *     <li>Access to the internals: The {@code Kissen} interface exposes methods to access and manipulate the
 *     internal components of the library.</li>
 *     <li>Retrieving specific implementations: The library offers multiple implementations of the {@code Kissen}
 *     interface. Users can retrieve a specific implementation based on their requirements.</li>
 *     <li>Obtaining public metadata: The {@code Kissen} interface provides methods to obtain metadata about the
 *     library, such as user data or current game scores.</li>
 * </ul
 *
 * @see Implementation
 */
public interface Kissen {

    /**
     * Returns an instance of the specified implementation class, which is a subclass of the {@link Implementation}
     * class.
     * The {@link Implementation} class represents a specific component of the Kissen system, providing additional
     * features or customization options.
     * The {@code implementation} parameter specifies the class of the implementation to retrieve.
     * The returned instance is an object of the specified implementation class and is guaranteed to be non-null.
     *
     * <p>Using this method, users can obtain a specific implementation of the {@link Implementation} class, tailored
     * to their requirements or desired functionality.</p>
     *
     * <p>The specified implementation class must be a valid subclass of the {@link Implementation} class. It should
     * provide specific functionality or enhancements to the core functionality of the Kissen library.</p>
     *
     * <p>The {@link Implementation} class serves as a base class for different implementations, allowing developers
     * to extend and customize the library according to their needs.</p>
     *
     * <p>When calling this method, users should pass the class object of the desired implementation as the {@code
     * implementation} parameter. The method will return an instance of the specified implementation class.</p>
     *
     * <p><strong>Note:</strong> It is important to ensure that the provided implementation class is compatible with
     * the Kissen library version being used. Incompatibilities may lead to unexpected behavior or errors.</p>
     *
     * @param implementation the class of the implementation to retrieve.
     * @param <T>            a type parameter that extends {@code Implementation}.
     * @return an instance of the specified implementation class.
     * @see Implementation
     */
    @NotNull <T extends Implementation> T getImplementation(@NotNull Class<T> implementation) throws ImplementationAbsentException;

    /**
     * Returns the port on which the server is running.
     *
     * <p>
     * This method retrieves the port number on which the server is currently running.
     * The port number represents the endpoint through which clients can connect to the server
     * to access its services or resources.
     * </p>
     *
     * <p>
     * The returned port number is an {@code int} value indicating the server's listening port.
     * It is typically a positive integer within the valid range of port numbers (0 to 65535).
     * </p>
     *
     * @return the port of the server
     */
    int getPort();

    /**
     * Checks if a player with the specified UUID is currently online.
     *
     * <p>
     * This method determines whether a player with the given UUID is currently connected
     * and online in the game. The UUID uniquely identifies a player across different sessions
     * and remains consistent for the same player over time.
     * </p>
     *
     * <p>
     * The UUID parameter must not be null ({@code @NotNull}). If a null UUID is provided,
     * an {@link IllegalArgumentException} will be thrown.
     * </p>
     *
     * <p>
     * The method returns a boolean value indicating the online status of the player.
     * It returns {@code true} if the player with the specified UUID is currently online,
     * and {@code false} otherwise.
     * </p>
     *
     * <p>
     * Note: The online status of a player may change dynamically as players join or leave the game.
     * Therefore, the result of this method may not be accurate if it is not called in real-time.
     * </p>
     *
     * @param uuid the UUID of the player to check
     * @return {@code true} if the player is online, {@code false} otherwise
     * @throws IllegalArgumentException if the UUID parameter is null
     */
    boolean isOnline(@NotNull UUID uuid);

    /**
     * Checks if a player with the specified name is currently online.
     *
     * <p>
     * This method determines whether a player with the given name is currently connected
     * and online in the game. The player's name is a unique identifier that distinguishes
     * them from other players.
     * </p>
     *
     * <p>
     * The name parameter must not be null ({@code @NotNull}). If a null name is provided,
     * an {@link IllegalArgumentException} will be thrown.
     * </p>
     *
     * <p>
     * The method returns a boolean value indicating the online status of the player.
     * It returns {@code true} if the player with the specified name is currently online,
     * and {@code false} otherwise.
     * </p>
     *
     * <p>
     * Note: The online status of a player may change dynamically as players join or leave the game.
     * Therefore, the result of this method may not be accurate if it is not called in real-time.
     * </p>
     *
     * @param name the name of the player to check
     * @return {@code true} if the player is online, {@code false} otherwise
     * @throws IllegalArgumentException if the name parameter is null
     */
    boolean isOnline(@NotNull String name);

    /**
     * Returns the console as a {@link ServerEntity}.
     *
     * <p>
     * This method retrieves the console as a {@link ServerEntity} instance.
     * The console represents the system's primary interface for interacting with the application
     * or managing the server's operations.
     * </p>
     *
     * <p>
     * The returned console is guaranteed to be not null ({@code @NotNull}).
     * If, for any reason, the console is not available or cannot be retrieved, an exception will be thrown.
     * </p>
     *
     * <p>
     * Note: The console functionality may vary depending on the specific application or server being used.
     * This method assumes that a console implementation is available and accessible.
     * </p>
     *
     * @return the console as a {@link ConsoleClient}
     * @throws IllegalStateException if the console is not available or cannot be retrieved
     */
    @NotNull ConsoleClient getConsole();
}
