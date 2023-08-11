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

package net.kissenpvp.core.database;

import net.kissenpvp.core.api.config.ConfigurationImplementation;
import net.kissenpvp.core.api.database.connection.DatabaseConnection;
import net.kissenpvp.core.api.database.connection.DatabaseImplementation;
import net.kissenpvp.core.api.database.meta.BackendException;
import net.kissenpvp.core.api.reflection.Parameter;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.database.jdbc.KissenMySQLDatabaseConnection;
import net.kissenpvp.core.database.jdbc.KissenSQLiteDatabaseConnection;
import net.kissenpvp.core.database.mongodb.KissenMongoDatabaseConnection;
import net.kissenpvp.core.database.settings.DatabaseDNS;
import net.kissenpvp.core.reflection.KissenReflectionClass;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


public class KissenDatabaseImplementation implements DatabaseImplementation {

    private final Set<DatabaseConnection> databaseConnections;
    private final Set<Class<? extends DatabaseConnection>> connectionClasses;

    public KissenDatabaseImplementation() {
        this.databaseConnections = new HashSet<>();
        this.connectionClasses = new HashSet<>();
    }

    public @NotNull DatabaseConnection connectDatabase() {
        DatabaseImplementation databaseImplementation = KissenCore.getInstance().getImplementation(DatabaseImplementation.class);
        databaseImplementation.registerDatabaseDriver(KissenMySQLDatabaseConnection.class);
        databaseImplementation.registerDatabaseDriver(KissenSQLiteDatabaseConnection.class);
        databaseImplementation.registerDatabaseDriver(KissenMongoDatabaseConnection.class);

        String connectionQuery = KissenCore.getInstance().getImplementation(ConfigurationImplementation.class).getSetting(DatabaseDNS.class);
        DatabaseConnection databaseConnection = databaseImplementation.createConnection("public", connectionQuery);
        KissenCore.getInstance().getLogger().info("The application is currently utilizing the backend driver '{}' for the database.", databaseConnection.getDriver());
        return databaseConnection;
    }

    @Override
    public @NotNull DatabaseConnection[] getConnections() {
        return databaseConnections.toArray(new DatabaseConnection[0]);
    }

    @Override
    public boolean registerDatabaseDriver(@NotNull Class<? extends DatabaseConnection> databaseDriver) {
        return connectionClasses.add(databaseDriver);
    }

    @Override
    public @NotNull DatabaseConnection createConnection(@NotNull String connectionID, @NotNull String connectionString) {
        DatabaseConnection databaseConnection = null;
        for (Class<? extends DatabaseConnection> clazz : connectionClasses) {
            try {
                databaseConnection = (DatabaseConnection) new KissenReflectionClass(clazz).newInstance(Parameter.parameterize(connectionID), Parameter.parameterize(connectionString));
                databaseConnection.connect();

                if (databaseConnection.isConnected()) {
                    break;
                }
                KissenCore.getInstance().getLogger().error("Could not establish a connection to database backend '{}' using connection string '{}'.", databaseConnection.getDriver(), connectionString);

            } catch (BackendException ignored) {
            }
            databaseConnection = null;
        }

        if (databaseConnection == null) {
            throw new NullPointerException("No suitable driver found for connection string '" + connectionString + "'.");
        }

        databaseConnections.add(databaseConnection);
        return databaseConnection;
    }

    @Override
    public @NotNull <T extends DatabaseConnection> Optional<T> getConnection(@NotNull Class<T> type, @NotNull String connectionID) {
        return (Optional<T>) databaseConnections.stream().filter(databaseConnection -> type.equals(databaseConnection.getClass())).filter(databaseConnection -> databaseConnection.getConnectionID().equals(connectionID)).findFirst();
    }

    @Override
    public <T extends DatabaseConnection> void close(@NotNull Class<T> type, @NotNull String connectionID) throws BackendException {
        Optional<T> connection = getConnection(type, connectionID);
        if (connection.isPresent()) {
            connection.get().disconnect();
        }
    }
}
