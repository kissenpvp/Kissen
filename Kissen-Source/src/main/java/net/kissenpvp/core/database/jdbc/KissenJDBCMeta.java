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

import net.kissenpvp.core.api.base.plugin.KissenPlugin;
import net.kissenpvp.core.api.database.connection.PreparedStatementExecutor;
import net.kissenpvp.core.api.database.meta.BackendException;
import net.kissenpvp.core.api.database.meta.Meta;
import net.kissenpvp.core.api.database.meta.Table;
import net.kissenpvp.core.api.database.queryapi.Column;
import net.kissenpvp.core.api.database.queryapi.select.QuerySelect;
import net.kissenpvp.core.api.database.queryapi.update.QueryUpdate;
import net.kissenpvp.core.api.database.queryapi.update.Update;
import net.kissenpvp.core.database.KissenBaseMeta;
import net.kissenpvp.core.database.jdbc.query.JDBCSelectQueryExecutor;
import net.kissenpvp.core.database.jdbc.query.JDBCUpdateQueryExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

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

    /**
     * Constructs a new instance of KissenJDBCMeta with the specified table and column names.
     *
     * <p>This constructor initializes a new instance of KissenJDBCMeta with the provided table and column names.
     * It invokes the constructor of the superclass using the specified parameters,
     * then generates the database table if it does not already exist.</p>
     *
     * @param table the name of the database table
     * @see BackendException
     */
    public KissenJDBCMeta(@NotNull Table table, @Nullable KissenPlugin kissenPlugin) {
        super(table, kissenPlugin);
    }

    @Override
    public void purge(@NotNull String totalID) {

        if (Objects.isNull(getPlugin())) {
            getPreparedStatement(String.format("DELETE FROM %s WHERE %s IS NULL AND (%s = ?);", getTable(), getTable().getPluginColumn(), getTable().getColumn(Column.TOTAL_ID)), preparedStatement -> {
                preparedStatement.setString(1, totalID);
                preparedStatement.executeUpdate();
            });
            return;
        }

        getPreparedStatement(String.format("DELETE FROM %s WHERE %s = ? AND (%s = ?);", getTable(), getTable().getPluginColumn(), getTable().getColumn(Column.TOTAL_ID)), preparedStatement -> {
            preparedStatement.setString(1, getPluginName());
            preparedStatement.setString(2, totalID);
            preparedStatement.executeUpdate();
        });
    }

    @Override
    protected void setJson(@NotNull String totalID, @NotNull String key, @Nullable Object object) {
        String[] serialized = serialize(object);
        if (serialized == null) {

            internalDelete(totalID, key);
            return;
        }

        assert object != null;
        Update update = new Update(Column.VALUE, object);
        QueryUpdate queryUpdate = update(update).where(Column.TOTAL_ID, totalID).and(Column.KEY, key);
        if (queryUpdate.execute().exceptionally((t) -> 0L).join() == 0) {
            insert(totalID, key, serialized);
        }
    }

    @Override
    protected @NotNull CompletableFuture<Object[][]> execute(@NotNull QuerySelect select) {
        JDBCSelectQueryExecutor executor = new JDBCSelectQueryExecutor(select, this);

        return CompletableFuture.supplyAsync(() -> {
            List<Object[]> array = new ArrayList<>();

            List<String> values = new ArrayList<>();
            String sql = executor.constructSQL(values);
            getPreparedStatement(sql, executor.executeStatement(array, values.toArray(String[]::new)));
            return array.toArray(new Object[0][]);
        }).handle(logExceptions());
    }

    @Override
    protected @NotNull CompletableFuture<Long> execute(@NotNull QueryUpdate update) {
        JDBCUpdateQueryExecutor executor = new JDBCUpdateQueryExecutor(update, this);
        return CompletableFuture.supplyAsync(() -> {
            List<String> updateValues = new ArrayList<>(), whereValues = new ArrayList<>();
            String sql = executor.constructSQL(updateValues, whereValues);
            String[] total = Stream.concat(updateValues.stream(), whereValues.stream()).toArray(String[]::new);

            AtomicLong count = new AtomicLong();
            getPreparedStatement(sql, executor.executeStatement(total, count));
            return count.get();
        }).handle(logExceptions()).thenApply((data) -> data);
    }

    @Override
    public void addMap(@NotNull String id, @NotNull Map<@NotNull String, @NotNull Object> data) throws BackendException {
        getPreparedStatement(String.format("INSERT INTO %s (%s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?);", getTable(), getTable().getColumn(Column.TOTAL_ID), getTable().getColumn(Column.KEY), getTable().getPluginColumn(), getTable().getTypeColumn(), getTable().getColumn(Column.VALUE)), preparedStatement -> {
            for (Map.Entry<String, Object> current : data.entrySet()) {
                preparedStatement.setString(1, id);
                preparedStatement.setString(2, current.getKey());
                preparedStatement.setString(3, getPluginName());

                String[] serialized = serialize(current.getValue());
                preparedStatement.setString(4, serialized[0]);
                preparedStatement.setString(5, serialized[1]);
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        });
    }

    /**
     * Inserts a new dataset into the database.
     *
     * <p>This method inserts a new dataset into the database with the specified total ID, key, and values.
     * It constructs a SQL INSERT statement using the table and column names,
     * then prepares and executes the statement with the provided values.</p>
     *
     * @param totalID the total ID for the record
     * @param key     the key for the record
     * @param values  an array containing the type and value for the record
     * @throws NullPointerException if any of the parameters or values array is `null`
     */
    private void insert(@NotNull String totalID, @NotNull String key, @NotNull String[] values) {
        String insert = "INSERT INTO %s (%s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?);";
        Object[] arguments = {getTable().getTable(), getTable().getColumn(Column.TOTAL_ID), getTable().getColumn(Column.KEY), getTable().getPluginColumn(), // plugin
                getTable().getTypeColumn(), // type
                getTable().getColumn(Column.VALUE)};

        getPreparedStatement(String.format(insert, arguments), (preparedStatement -> {
            preparedStatement.setString(1, totalID);
            preparedStatement.setString(2, key);
            preparedStatement.setString(3, getPluginName());
            preparedStatement.setString(4, values[0]); // type
            preparedStatement.setString(5, values[1]); // value
            preparedStatement.executeUpdate();
        }));
    }

    private void internalDelete(@NotNull String totalID, @NotNull String key) {
        if (Objects.isNull(getPlugin())) {
            getPreparedStatement(String.format("DELETE FROM %s WHERE %s IS NULL AND (%s = ? AND %s = ?);", getTable(), getTable().getPluginColumn(), getTable().getColumn(Column.TOTAL_ID), getTable().getColumn(Column.KEY)), (preparedStatement -> {
                preparedStatement.setString(1, totalID);
                preparedStatement.setString(2, key);
                preparedStatement.executeUpdate();
            }));
            return;
        }
        getPreparedStatement(String.format("DELETE FROM %s WHERE %s = ? AND (%s = ? AND %s = ?);", getTable(), getTable().getPluginColumn(), getTable().getColumn(Column.TOTAL_ID), getTable().getColumn(Column.KEY)), (preparedStatement -> {
            preparedStatement.setString(1, getPluginName());
            preparedStatement.setString(2, totalID);
            preparedStatement.setString(3, key);
            preparedStatement.executeUpdate();
        }));
    }

    /**
     * Prepares a {@link PreparedStatement} for the specified query and executes it using the provided {@link PreparedStatementExecutor}.
     *
     * <p>This method prepares a {@link PreparedStatement} for the specified query and executes it using the provided {@link PreparedStatementExecutor}.
     * The {@link PreparedStatement} and any associated resources are managed by the implementation of this method.</p>
     *
     * @param query                     the SQL query string to be prepared
     * @param preparedStatementExecutor the {@link PreparedStatementExecutor} to execute the prepared statement
     * @throws NullPointerException if either the query or the {@link PreparedStatementExecutor} is {@code null}
     * @see PreparedStatement
     * @see PreparedStatementExecutor
     */
    public abstract void getPreparedStatement(@NotNull String query, @NotNull PreparedStatementExecutor preparedStatementExecutor);
}
