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

package net.kissenpvp.core.networking.socket;

import net.kissenpvp.core.api.networking.socket.DataPackage;
import net.kissenpvp.core.api.networking.socket.Execution;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.net.Socket;
import java.util.Map;


public interface SocketEntity
{
    /**
     * Executes a {@link Execution}. This execution has to be registered beforehand with <br>{@code #addExecution(String, Execution)}.
     *
     * @param dataPackage The data sent to the server.
     * @param tempSocket  The socket the message is coming from.
     */
    void execute(@NotNull DataPackage dataPackage, @NotNull Socket tempSocket);

    /**
     * A standard method of adding executions, which must always be there.
     */
    void insertExecutions();

    /**
     * Adds a new {@link Execution} to the connector.
     *
     * @param id        The ID with which this execution is triggered.
     * @param execution The code to be executed.
     */
    void addExecution(@NotNull String id, @NotNull Execution execution);

    /**
     * Is called as soon as a client connects.
     *
     * @param execution The code to be executed.
     */
    void onClientConnect(@NotNull Execution execution);

    /**
     * Is called as soon as a client disconnects.
     *
     * @param execution The code to be executed.
     */
    void onClientDisconnect(@NotNull Execution execution);

    @NotNull @Unmodifiable Map<String, Execution> getExecutions();

    @Nullable Socket getSocket();

    /**
     * Starts the connector.
     */
    void start();

    /**
     * Whether the client is running right now.
     *
     * @return whether the client has been started before using {@link #start()}.
     */
    boolean isRunning();

    /**
     * Stops the connector.
     */
    void stop();
}
