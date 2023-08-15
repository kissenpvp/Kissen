package net.kissenpvp.core.database.jdbc;

import net.kissenpvp.core.api.database.connection.DatabaseDriver;
import net.kissenpvp.core.api.database.meta.BackendException;
import org.jetbrains.annotations.NotNull;

public class KissenMySQLDatabaseConnection extends KissenJDBCDatabaseConnection {
    public KissenMySQLDatabaseConnection(@NotNull String connectionID, @NotNull String connectionString) {
        super(connectionID, connectionString, DatabaseDriver.MYSQL);
    }

    @Override
    public void connect() throws BackendException {
        if (!getConnectionString().toLowerCase().startsWith("jdbc:mysql://")) {
            throw new BackendException(new IllegalStateException("Illegal connection string given to mysql connection"));
        }
        super.connect();
    }
}
