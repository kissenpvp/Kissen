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
import net.kissenpvp.core.database.settings.KeepSQLiteFile;
import net.kissenpvp.core.reflection.KissenReflectionClass;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
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
        if(!(databaseConnection instanceof KissenSQLiteDatabaseConnection) && !KissenCore.getInstance().getImplementation(ConfigurationImplementation.class).getSetting(KeepSQLiteFile.class))
        {
            deleteObsoleteDatabaseFiles();
        }
        KissenCore.getInstance().getLogger().info("The application is currently utilizing the backend driver '{}' for the database.", databaseConnection.getDriver());
        return databaseConnection;
    }

    private void deleteObsoleteDatabaseFiles() {
        File[] files = Paths.get("").toAbsolutePath().toFile().listFiles((curr, s) -> s.endsWith(".db"));
        if(files != null)
        {
            Arrays.stream(files).filter(file -> !file.delete()).forEach(file -> KissenCore.getInstance().getLogger().error("The system was unable to delete the file {}.", file));
        }
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
