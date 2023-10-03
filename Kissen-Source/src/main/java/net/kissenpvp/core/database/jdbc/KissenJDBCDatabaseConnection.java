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

package net.kissenpvp.core.database.jdbc;

import lombok.Getter;
import net.kissenpvp.core.api.database.connection.DatabaseDriver;
import net.kissenpvp.core.api.database.connection.MYSQLDatabaseConnection;
import net.kissenpvp.core.api.database.connection.PreparedStatementExecutor;
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
            public void getPreparedStatement(@NotNull String query, @NotNull PreparedStatementExecutor preparedStatementExecutor) {
                KissenJDBCDatabaseConnection.this.getPreparedStatement(query, preparedStatementExecutor);
            }
        };
    }

    @Override
    public @NotNull Meta createMeta(@NotNull String table, @NotNull String uuidColumn, @NotNull String keyColumn, @NotNull String valueColumn) {
        return new KissenJDBCMeta(table, uuidColumn, keyColumn, valueColumn) {
            @Override
            public void getPreparedStatement(@NotNull String query, @NotNull PreparedStatementExecutor preparedStatementExecutor) {
                KissenJDBCDatabaseConnection.this.getPreparedStatement(query, preparedStatementExecutor);
            }
        };
    }

    public void getPreparedStatement(@NotNull String query, @NotNull PreparedStatementExecutor statementExecutor) {

        if (!isConnected()) {
            disconnect(); // clean up broken connection
            connect(); // reconnect if possible
        }

        try {
            @SuppressWarnings("SqlSourceToSinkFlow") PreparedStatement preparedStatement = connection.prepareStatement(query);
            statementExecutor.execute(preparedStatement);
            preparedStatement.close();
        } catch (SQLException sqlException) {
            if (!statementExecutor.handle(sqlException))
            {
                throw new BackendException(sqlException);
            }
        }
    }
}
