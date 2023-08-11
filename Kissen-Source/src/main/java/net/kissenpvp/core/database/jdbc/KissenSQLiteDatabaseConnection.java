package net.kissenpvp.core.database.jdbc;

import net.kissenpvp.core.api.database.connection.DatabaseDriver;
import net.kissenpvp.core.api.database.meta.BackendException;
import org.jetbrains.annotations.NotNull;

public class KissenSQLiteDatabaseConnection extends KissenJDBCDatabaseConnection {
    public KissenSQLiteDatabaseConnection(@NotNull String connectionID, @NotNull String connectionString) {
        super(connectionID, connectionString, DatabaseDriver.SQLITE);
    }

    @Override
    public void connect() throws BackendException {
        if (!getConnectionString().toLowerCase().startsWith("jdbc:sqlite:")) {
            throw new BackendException(new IllegalStateException("Illegal connection string given to sqlite connection"));
        }
        super.connect();
    }
}
