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

package net.kissenpvp.core.api.database.connection;

import net.kissenpvp.core.api.base.Implementation;
import net.kissenpvp.core.api.database.meta.BackendException;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface DatabaseImplementation extends Implementation {

    @NotNull DatabaseConnection[] getConnections();

    boolean registerDatabaseDriver(@NotNull Class<? extends DatabaseConnection> databaseConnection);

    @NotNull DatabaseConnection createConnection(@NotNull String connectionID, @NotNull String connectionString);

    @NotNull <T extends DatabaseConnection> Optional<T> getConnection(@NotNull Class<T> type, @NotNull String connectionID);

    <T extends DatabaseConnection> void close(@NotNull Class<T> type, @NotNull String connectionID) throws BackendException;
}
