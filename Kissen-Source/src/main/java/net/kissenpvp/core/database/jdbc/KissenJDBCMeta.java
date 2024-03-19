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

import net.kissenpvp.core.api.database.connection.PreparedStatementExecutor;
import net.kissenpvp.core.api.database.meta.BackendException;
import net.kissenpvp.core.api.database.meta.Meta;
import net.kissenpvp.core.api.database.queryapi.Column;
import net.kissenpvp.core.api.database.queryapi.select.QuerySelect;
import net.kissenpvp.core.api.database.queryapi.update.QueryUpdate;
import net.kissenpvp.core.api.database.queryapi.update.Update;
import net.kissenpvp.core.database.KissenBaseMeta;
import net.kissenpvp.core.database.jdbc.query.JDBCSelectQueryExecutor;
import net.kissenpvp.core.database.jdbc.query.JDBCUpdateQueryExecutor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;

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

    public KissenJDBCMeta(@NotNull String table, @NotNull String totalIDColumn, @NotNull String keyColumn, @NotNull String valueColumn) throws BackendException {
        super(table, totalIDColumn, keyColumn, valueColumn, "type");
        generateTable();
    }

    @Contract(mutates = "param1")
    private static void setStatementValues(@NotNull PreparedStatement preparedStatement, @NotNull String @NotNull [] parameterValues) throws SQLException {
        for (int i = 0; i < parameterValues.length; i++) {
            preparedStatement.setString(i + 1, parameterValues[i]);
        }
    }

    protected void generateTable() {
        String query = "CREATE TABLE IF NOT EXISTS %s (%s VARCHAR(100) NOT NULL, %s VARCHAR(100) NOT NULL, %s TINYTEXT NOT NULL, %s JSON NOT NULL CHECK (JSON_VALID(%s)));";
        getPreparedStatement(query.formatted(getTable(), getTotalIDColumn(), getKeyColumn(), getTypeColumn(), getValueColumn(), getValueColumn()), PreparedStatement::executeUpdate);
    }

    @Override
    public void purge(@NotNull String totalID) throws BackendException {
        getPreparedStatement(String.format("DELETE FROM %s WHERE %s = ?;", getTable(), getTotalIDColumn()), preparedStatement -> {
            preparedStatement.setString(1, totalID);
            preparedStatement.executeUpdate();
        });
    }

    @Override
    protected void setJson(@NotNull String totalID, @NotNull String key, @Nullable Object object) {
        String[] serialized = serialize(object);
        if (serialized == null) {
            getPreparedStatement(String.format("DELETE FROM %s WHERE %s = ? AND %s = ?;", getTable(), getTotalIDColumn(), getKeyColumn()), (preparedStatement -> {
                preparedStatement.setString(1, totalID);
                preparedStatement.setString(2, key);
                preparedStatement.executeUpdate();
            }));
            return;
        }

        assert object != null;
        Update update = new Update(Column.VALUE, object);
        QueryUpdate queryUpdate = update(update).where(Column.TOTAL_ID, totalID).and(Column.KEY, key);
        if (queryUpdate.execute().exceptionally((t) -> 0L).join() == 0) {
            insert(totalID, key, serialized);
        }
    }

    private void insert(@NotNull String totalID, @NotNull String key, @NotNull String[] values) {
        String insert = "INSERT INTO %s (%s, %s, %s, %s) VALUES (?, ?, ?, ?);";
        getPreparedStatement(insert.formatted(getTable(), getTotalIDColumn(), getKeyColumn(), getTypeColumn(), getValueColumn()), (preparedStatement -> {
            preparedStatement.setString(1, totalID);
            preparedStatement.setString(2, key);
            preparedStatement.setString(3, values[0]); // type
            preparedStatement.setString(4, values[1]); // value
            preparedStatement.executeUpdate();
        }));
    }

    @Override
    protected @NotNull CompletableFuture<Object[][]> execute(@NotNull QuerySelect select) throws BackendException {
        JDBCSelectQueryExecutor executor = new JDBCSelectQueryExecutor(select, this);
        return CompletableFuture.supplyAsync(() -> {
            List<Object[]> array = new ArrayList<>();

            String[] values = new String[select.getFilterQueries().length];
            getPreparedStatement(executor.constructSQL(values), executor.executeStatement(array, values, select.getColumns()));

            return array.toArray(new Object[0][]);
        }).handle(logExceptions());
    }

    @Override
    protected @NotNull CompletableFuture<Long> execute(@NotNull QueryUpdate update) throws BackendException {
        JDBCUpdateQueryExecutor executor = new JDBCUpdateQueryExecutor(update, this);
        return CompletableFuture.supplyAsync(() -> {
            Predicate<Update> isValue = (directive) -> Objects.equals(directive.column(), Column.VALUE);
            int valueColumns = (int) Arrays.stream(update.getColumns()).filter(isValue).count();

            String[] updateValues = new String[update.getColumns().length + valueColumns];
            String[] whereValues = new String[update.getFilterQueries().length];
            String sql = executor.constructSQL(updateValues, whereValues);

            String[] total = new String[updateValues.length + whereValues.length];
            System.arraycopy(updateValues, 0, total, 0, updateValues.length);
            System.arraycopy(whereValues, 0, total, updateValues.length, whereValues.length);

            AtomicLong count = new AtomicLong();
            getPreparedStatement(sql, executor.executeStatement(sql, total, count));
            return count.get();
        }).handle(logExceptions());
    }

    public abstract void getPreparedStatement(@NotNull String query, @NotNull PreparedStatementExecutor preparedStatementExecutor);
}
