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

package net.kissenpvp.core.api.base;

import net.kissenpvp.core.api.database.meta.ObjectMeta;
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
     * Retrieves the public metadata from the underlying database accessed by the {@code Kissen} instance.
     * <p>This method returns an {@code ObjectMeta} interface that serves as an abstraction layer for interacting
     * with the database and accessing its metadata.</p>
     * <p>The public metadata represents essential information stored in the database, such as user data, current
     * game scores, and other relevant details.</p>
     * <p>It is important to note that the returned {@code ObjectMeta} interface provides methods and operations to
     * query and manipulate the database, but does not contain the actual data itself. Instead, it acts as a gateway
     * to the database's metadata.</p>
     * <p>Accessing the public metadata through the {@code ObjectMeta} interface can be useful for performing
     * database operations, retrieving statistics, or making informed decisions based on the stored information.</p>
     * <p>Implementation Note:</p>
     * <p>The concrete implementation of this method may vary depending on the specific {@code Kissen} implementation
     * being used.</p>
     *
     * @return the {@code ObjectMeta} interface representing the public metadata accessed from the underlying database.
     * @see ObjectMeta
     */
    @NotNull ObjectMeta getPublicMeta();

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

    /**
     * Creates a repeated task with a specified delay, tick rate, and code to execute.
     *
     * <p>
     * This method serves as an interface for creating tasks that are to be repeated at a specified tick rate.
     * The tick rate represents the frequency at which the task will be executed, measured in ticks.
     * In this system, 20 ticks equal 1 second.
     * </p>
     *
     * <p>
     * The parameters for this method are as follows:
     * </p>
     * <ul>
     *     <li>{@code delay}: The delay, in ticks, indicating after how many ticks the transferred task will be
     *     executed.</li>
     *     <li>{@code tickRate}: The rate at which this task should repeat, in ticks.</li>
     *     <li>{@code runnable}: The code itself, encapsulated within a {@link Runnable} object, which will be
     *     executed as the task.
     *         It must not be null ({@code @NotNull}).</li>
     * </ul>
     *
     * <p>
     * Note: It is important to ensure that the tick rate and delay values are appropriate for the desired behavior
     * of the repeated task.
     * An incorrect configuration may result in undesired performance or behavior.
     * </p>
     *
     * @param delay    the delay, in ticks, indicating after how many ticks the transferred task will be executed
     * @param runnable the code to execute, encapsulated within a {@link Runnable} object (must not be null)
     */
    void repeatTask(@NotNull String name, int delay, @NotNull Runnable runnable);

    /**
     * Executes a task, possibly with a specified delay, in the main thread.
     *
     * <p>
     * This method allows for the execution of a task in the main thread, optionally with a specified delay.
     * The task represents a piece of code encapsulated within a {@link Runnable} object that will be executed.
     * </p>
     *
     * <p>
     * The parameters for this method are as follows:
     * </p>
     * <ul>
     *     <li>{@code delay}: The delay, in ticks, indicating after how many ticks the action will be taken.
     *         A value smaller than 0 can be used to indicate no delay (e.g., -1).</li>
     *     <li>{@code runnable}: The code itself, encapsulated within a {@link Runnable} object, which will be
     *     executed as the task.
     *         It must not be null ({@code @NotNull}).</li>
     * </ul>
     *
     * <p>
     * Note: The delay parameter is measured in ticks, where 20 ticks equal 1 second.
     * It is important to consider the tick rate and adjust the delay value accordingly to achieve the desired timing.
     * </p>
     *
     * @param delay    the delay, in ticks, indicating after how many ticks the action will be taken (a value smaller
     *                 than 0 indicates no delay)
     * @param runnable the code to execute, encapsulated within a {@link Runnable} object (must not be null)
     */
    void runTask(long delay, @NotNull Runnable runnable);

    @NotNull Logger getLogger();
}
