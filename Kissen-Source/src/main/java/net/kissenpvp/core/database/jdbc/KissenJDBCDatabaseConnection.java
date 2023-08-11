package net.kissenpvp.core.database.jdbc;

import lombok.Getter;
import net.kissenpvp.core.api.database.connection.DatabaseDriver;
import net.kissenpvp.core.api.database.connection.MYSQLDatabaseConnection;
import net.kissenpvp.core.api.database.meta.BackendException;
import net.kissenpvp.core.api.database.meta.Meta;
import net.kissenpvp.core.api.database.meta.ObjectMeta;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Getter
public abstract class KissenJDBCDatabaseConnection implements MYSQLDatabaseConnection {

    private final String connectionID, connectionString;
    private final DatabaseDriver driver;
    private Connection connection;

    public KissenJDBCDatabaseConnection(String connectionID, String connectionString, DatabaseDriver driver) {
        this.connectionID = connectionID;
        this.connectionString = connectionString;
        this.driver = driver;
    }

    @Override
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException sqlException) {
            return false;
        }
    }

    @Override
    public void connect() throws BackendException {

        if (!isConnected()) {
            try {
                Class.forName(getDriver().toString());
                connection = DriverManager.getConnection(connectionString);
            } catch (SQLException | ClassNotFoundException exception) {
                throw new BackendException(exception);
            }
            return;
        }
        throw new BackendException(new IllegalStateException());
    }

    @Override
    public void disconnect() throws BackendException {
        if (isConnected()) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException sqlException) {
                throw new BackendException(sqlException);
            }
        }
        throw new BackendException(new IllegalStateException());
    }

    @Override
    public @NotNull ObjectMeta createObjectMeta(@NotNull String table) {
        return new KissenObjectJDBCMeta(table) {
            @Override
            public @NotNull PreparedStatement getPreparedStatement(@NotNull String query) throws SQLException {
                try {
                    return KissenJDBCDatabaseConnection.this.getPreparedStatement(query);
                } catch (BackendException backendException) {
                    throw (SQLException) backendException.getCause();
                }
            }
        };
    }

    @Override
    public @NotNull Meta createMeta(@NotNull String table, @NotNull String uuidColumn, @NotNull String keyColumn, @NotNull String valueColumn) {
        return new KissenJDBCMeta(table, uuidColumn, keyColumn, valueColumn) {
            @Override
            public @NotNull PreparedStatement getPreparedStatement(@NotNull String query) throws SQLException {
                try {
                    return KissenJDBCDatabaseConnection.this.getPreparedStatement(query);
                } catch (BackendException backendException) {
                    throw (SQLException) backendException.getCause();
                }
            }
        };
    }

    @Override
    public @NotNull PreparedStatement getPreparedStatement(@NotNull String query) throws BackendException {

        if (!isConnected()) {
            disconnect(); // clean up broken connection
            connect(); // reconnect if possible
        }

        //noinspection SqlSourceToSinkFlow
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            return preparedStatement;
        } catch (SQLException sqlException) {
            throw new BackendException(sqlException);
        }
    }
}
