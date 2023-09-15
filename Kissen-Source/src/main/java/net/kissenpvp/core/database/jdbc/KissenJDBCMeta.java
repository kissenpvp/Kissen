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

import com.google.gson.Gson;
import lombok.SneakyThrows;
import net.kissenpvp.core.api.database.meta.BackendException;
import net.kissenpvp.core.api.database.meta.Meta;
import net.kissenpvp.core.api.database.queryapi.*;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.database.KissenBaseMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The {@code KissenMeta} class is an abstract class that provides a common base for classes representing metadata
 * for Kissen objects in the database.
 *
 * <p>
 * This class is designed to be extended by concrete implementations that provide specific metadata for Kissen objects.
 * It defines common functionality and properties that are applicable to all Kissen metadata implementations.
 * </p>
 *
 * <p>
 * The class implements the {@link Meta} interface, which represents the metadata interface for the database.
 * </p>
 *
 * <p>
 * The class is annotated with {@code @AllArgsConstructor} and {@code @Getter} annotations, which automatically
 * generate a constructor
 * that accepts arguments for all fields and generates corresponding getters for each field.
 * </p>
 *
 * @see Meta
 */
public abstract class KissenJDBCMeta extends KissenBaseMeta {

    public KissenJDBCMeta(String table, String totalIDColumn, String keyColumn, String valueColumn) {
        super(table, totalIDColumn, keyColumn, valueColumn);
    }

    @Override
    public void setString(@NotNull String totalID, @NotNull String key, @Nullable String value) throws BackendException {
        insert(totalID, key, value, insertString());
    }

    @Override
    public void setStringList(@NotNull String totalID, @NotNull String key, @Nullable List<String> value) throws BackendException {
        insert(totalID, key, new Gson().toJson(value), insertString());
    }

    @SuppressWarnings("unchecked")
    @Override
    public @NotNull @Unmodifiable List<String> getStringList(@NotNull String totalID, @NotNull String key) throws BackendException {
        return Arrays.stream(execute(getDefaultQuery(totalID, key))).map(data -> (ArrayList<String>) new Gson().fromJson(data[0], ArrayList.class)).findFirst().orElse(new ArrayList<String>());
    }

    @Override
    public @NotNull String[][] execute(@NotNull QuerySelect querySelect) throws BackendException {
        StringBuilder query = new StringBuilder("SELECT").append(" ");
        for (int i = 0; i < querySelect.getColumns().length; i++) {
            query.append(getColumn(querySelect.getColumns()[i]));
            if (i < (querySelect.getColumns().length - 1)) {
                query.append(",");
            }
            query.append(" ");
        }

        query.append("FROM ").append(getTable()).append(" ");

        String[] values = decodeFilterQueries(querySelect, query);
        query.append(";");
        return processQuerySelect(querySelect, query.toString(), values);
    }

    @Override
    public long execute(@NotNull QueryUpdate queryUpdate) throws BackendException {
        String[] values = new String[queryUpdate.getColumns().length + queryUpdate.getFilterQueries().length];
        StringBuilder query = new StringBuilder("UPDATE ").append(getTable()).append(" SET ");
        for (int i = 0; i < queryUpdate.getColumns().length; i++) {
            QueryUpdateDirective queryUpdateDirective = queryUpdate.getColumns()[i];
            query.append(getColumn(queryUpdateDirective.column())).append(" = ?");
            values[i] = queryUpdateDirective.value();
            if (i < (queryUpdate.getColumns().length - 1)) {
                query.append(",");
            }
            query.append(" ");
        }

        System.arraycopy(decodeFilterQueries(queryUpdate, query), 0, values, queryUpdate.getColumns().length, queryUpdate.getFilterQueries().length);
        query.append(";");

        try {
            try (PreparedStatement preparedStatement = getPreparedStatement(query.toString())) {
                for (int i = 0; i < values.length; i++) {
                    preparedStatement.setString(i + 1, values[i]);
                }

                ExecutorService executorService = Executors.newFixedThreadPool(1);
                CompletableFuture<Long> completableFuture = new CompletableFuture<>();
                executorService.submit(() -> completableFuture.complete(preparedStatement.executeLargeUpdate()));
                return completableFuture.join();
            }
        } catch (SQLException sqlException) {
            throw new BackendException(sqlException);
        }
    }

    @Override
    public void purge(@NotNull String totalID) {
        Thread thread = new Thread(() ->
        {
            try (PreparedStatement deleteStatement = getPreparedStatement("DELETE FROM " + getTable() + " WHERE " + getTotalIDColumn() + " = ?;")) {
                deleteStatement.setString(1, totalID);
                deleteStatement.executeUpdate();
            } catch (SQLException sqlException) {
                KissenCore.getInstance().getLogger().error("Could not execute database query. This can lead to data loss or unwanted behaviour. It is recommended to shutdown the server to prevent any harm to the data.", sqlException);
            }
        });
        thread.setName("purge");
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
    }

    /**
     * Inserts a value into the database with the specified total ID, key, value, and writer.
     *
     * <p>
     * This method performs the insertion of a value into the database table associated with the Kissen objects. It
     * requires the total ID,
     * key, value, and a writer implementation to handle the insertion process. The writer is responsible for
     * executing the necessary database
     * operations to insert the value.
     * </p>
     *
     * <p>
     * If the provided value is {@code null}, the method will only delete any existing records with the same total ID
     * and key from the
     * database table.
     * </p>
     *
     * <p>
     * This method spawns a new thread to execute the database operations asynchronously. The thread executes the
     * deletion of existing records,
     * followed by the insertion of the new value if it is not {@code null}.
     * </p>
     *
     * @param totalID the total ID associated with the value.
     * @param key     the key associated with the value.
     * @param value   the value to be inserted into the database. If {@code null}, only existing records will be
     *                deleted.
     * @param writer  the writer implementation responsible for executing the database operations for insertion.
     * @throws NullPointerException if {@code totalID}, {@code key}, or {@code writer} is {@code null}.
     */
    public <T> void insert(@NotNull String totalID, @NotNull String key, @Nullable T value, @NotNull Writer<T> writer) throws BackendException {
        try {
            PreparedStatement deleteStatement = getPreparedStatement("DELETE FROM " + getTable() + " WHERE " + getTotalIDColumn() + " = ? AND " + getKeyColumn() + " = ?;");
            deleteStatement.setString(1, totalID);
            deleteStatement.setString(2, key);
            spawnThread(totalID, key, value, writer, deleteStatement);
        } catch (SQLException sqlException) {
            throw new BackendException(sqlException);
        }
    }

    /**
     * Spawns a new thread to execute the database operations for insertion and deletion.
     *
     * <p>
     * This method creates a new thread that executes for example the deletion of existing records from the database
     * table and the insertion of a new value,
     * if provided. The deletion and insertion operations are performed using the provided writer implementation.
     * </p>
     *
     * <p>
     * The thread is started with the name "sql" and the minimum priority.
     * </p>
     *
     * @param totalID           the total ID associated with the value.
     * @param key               the key associated with the value.
     * @param value             the value to be inserted into the database. If {@code null}, only existing records
     *                          will be deleted.
     * @param writer            the writer implementation responsible for executing the database operations for
     *                          insertion.
     * @param preparedStatement the prepared statement used for the deletion operation.
     * @throws NullPointerException if {@code totalID}, {@code key}, {@code writer}, or {@code preparedStatement} is
     *                              {@code null}.
     */
    public <T> void spawnThread(@NotNull String totalID, @NotNull String key, @Nullable T value, @NotNull Writer<T> writer,
                                @NotNull PreparedStatement preparedStatement) {
        Thread thread = new Thread(() ->
        {
            try {
                preparedStatement.executeUpdate();
                preparedStatement.close();

                if (value != null) {
                    writer.write(totalID, key, value);
                }
            } catch (SQLException sqlException) {
                KissenCore.getInstance().getLogger().error("Could not execute database query. This can lead to data loss or unwanted behaviour. It is recommended to shutdown the server to prevent any harm to the data.", sqlException);
            }
        });
        thread.setName("sql");
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
    }

    /**
     * Returns a writer implementation for inserting values as strings into the database.
     *
     * <p>
     * This method returns a writer implementation that is responsible for executing the necessary database
     * operations to insert values as strings
     * into the associated database table.
     * </p>
     *
     * @return a writer implementation for inserting values as strings into the database.
     */
    @Contract(pure = true)
    private @NotNull Writer<String> insertString() {
        return (totalID1, key1, value) ->
        {
            PreparedStatement insertStatement = getPreparedStatement("INSERT INTO " + getTable() + " (" + getTotalIDColumn() + ", " + getKeyColumn() + ", " + getValueColumn() + ") VALUES (?, ?, ?);");
            insertStatement.setString(1, totalID1);
            insertStatement.setString(2, key1);
            insertStatement.setString(3, value);
            insertStatement.executeUpdate();
            insertStatement.close();
        };
    }


    /**
     * Creates the table in the database if it does not already exist.
     *
     * <p>
     * This method is responsible for creating the table in the database if it does not already exist. The table will
     * have columns for the ID,
     * key, and value fields. The specific data types and constraints may vary depending on the database
     * implementation. If an error occurs
     * during the table creation process, an error message will be logged.
     * </p>
     */
    @SneakyThrows
    protected void createTable() {
        getPreparedStatement("CREATE TABLE IF NOT EXISTS " + getTable() + " (" + getTotalIDColumn() + " VARCHAR(100) " + "NOT NULL, " + getKeyColumn() + " VARCHAR(100) NOT NULL, " + getValueColumn() + " TEXT NOT NULL) ").executeUpdate();
    }

    @NotNull
    private String[][] processQuerySelect(@NotNull QuerySelect querySelect, @NotNull String query, @NotNull String @NotNull [] values) throws BackendException {
        List<String[]> data = new ArrayList<>();
        try {
            try (PreparedStatement preparedStatement = getPreparedStatement(query)) {
                for (int i = 0; i < values.length; i++) {
                    preparedStatement.setString(i + 1, values[i]);
                }
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        String[] resultData = new String[querySelect.getColumns().length];
                        for (int i = 0; i < resultData.length; i++) {
                            resultData[i] = resultSet.getString(getColumn(querySelect.getColumns()[i]));
                        }
                        data.add(resultData);
                    }
                }
            }
        } catch (SQLException sqlException) {
            throw new BackendException(sqlException);
        }


        return data.toArray(new String[0][]);
    }

    private @NotNull String[] decodeFilterQueries(@NotNull QueryComponent<?> queryComponent, @NotNull StringBuilder query) {
        String[] values = new String[queryComponent.getFilterQueries().length];
        if (queryComponent.getFilterQueries().length != 0) {
            query.append("WHERE").append(" ");
            for (int i = 0; i < queryComponent.getFilterQueries().length; i++) {
                decodeFilterQuery(queryComponent, query, values, i);

                if (i < (queryComponent.getFilterQueries().length - 1)) {
                    query.append(" AND ");
                }
            }
        }
        return values;
    }

    /**
     * Retrieves a prepared statement for the specified SQL query.
     *
     * <p>
     * This method prepares a {@link PreparedStatement} for the given SQL query.
     * The prepared statement can be used to execute the query with parameters.
     * It provides a more efficient way to execute multiple queries with the same structure but different parameter
     * values.
     * </p>
     *
     * <p>
     * Note: The returned prepared statement may still require binding parameters before execution.
     * It is the responsibility of the caller to provide the necessary parameter values.
     * </p>
     *
     * @param query The SQL query string for which to retrieve a prepared statement.
     * @return A {@link PreparedStatement} object for the specified query.
     * @throws SQLException         if a database access error occurs or the query is invalid.
     * @throws NullPointerException if {@code query} is {@code null}.
     */
    public abstract @NotNull PreparedStatement getPreparedStatement(@NotNull String query) throws SQLException;

    private void decodeFilterQuery(@NotNull QueryComponent<?> queryComponent, @NotNull StringBuilder query, @NotNull String[] values, int i) {
        FilterQuery filterQuery = queryComponent.getFilterQueries()[i];
        String column = getColumn(filterQuery.getColumn());
        switch (filterQuery.getFilterType()) {
            case EQUALS -> {
                query.append(column).append(" = ").append("?");
                values[i] = filterQuery.getValue();
            }
            case START -> {
                query.append(column).append(" LIKE ").append("?");
                values[i] = filterQuery.getValue() + "%";
            }
            case END -> {
                query.append(column).append(" LIKE ").append("?");
                values[i] = "%" + filterQuery.getValue();
            }
        }
    }

    /**
     * Functional interface for writing data to a database.
     *
     * <p>
     * This functional interface provides a method for writing data to a database. Implementations of this interface
     * should handle the logic
     * of inserting or updating data in the appropriate table.
     * </p>
     *
     * @param <T> the type of the data to be written.
     */
    public interface Writer<T> {
        /**
         * Writes the specified data to the database.
         *
         * <p>
         * This method is responsible for inserting or updating the specified data in the database. The
         * implementation should perform the
         * necessary operations to ensure the data is correctly stored in the appropriate table.
         * </p>
         *
         * @param totalID the total ID associated with the data.
         * @param key     the key associated with the data.
         * @param value   the data to be written.
         * @throws SQLException         if an error occurs while writing the data to the database.
         * @throws NullPointerException if any of the parameters ({@code totalID}, {@code key}, {@code value}) is
         *                              {@code null}.
         */
        void write(@NotNull String totalID, @NotNull String key, @NotNull T value) throws SQLException;
    }
}