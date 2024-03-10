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
import net.kissenpvp.core.api.database.queryapi.FilterQuery;
import net.kissenpvp.core.api.database.queryapi.select.QuerySelect;
import net.kissenpvp.core.api.database.queryapi.update.QueryUpdate;
import net.kissenpvp.core.api.database.queryapi.update.QueryUpdateDirective;
import net.kissenpvp.core.database.KissenBaseMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    private final MessageFormat SELECT_FORMAT = new MessageFormat("SELECT {0} FROM {1} WHERE {2};");
    private final MessageFormat UPDATE_FORMAT = new MessageFormat("UPDATE {0} SET {1} WHERE {2};");
    private final MessageFormat VAR_PATTERN = new MessageFormat("{0} = ?");

    public KissenJDBCMeta(@NotNull String table, @NotNull String totalIDColumn, @NotNull String keyColumn, @NotNull String valueColumn) throws BackendException {
        super(table, totalIDColumn, keyColumn, valueColumn);
        getPreparedStatement(String.format("CREATE TABLE IF NOT EXISTS %s (%s VARCHAR(100) NOT NULL, %s VARCHAR(100) NOT NULL, %s JSON NOT NULL CHECK (JSON_VALID(%s)));", getTable(), getTotalIDColumn(), getKeyColumn(), getValueColumn(), getValueColumn()), PreparedStatement::executeUpdate);
    }

    @Override
    public void purge(@NotNull String totalID) throws BackendException {
        getPreparedStatement(String.format("DELETE FROM %s WHERE %s = ?;", getTable(), getTotalIDColumn()), preparedStatement -> {
            preparedStatement.setString(1, totalID);
            preparedStatement.executeUpdate();
        });
    }

    @Override
    protected void setJson(@NotNull String totalID, @NotNull String key, @Nullable String value) {
        if (value == null) {
            getPreparedStatement(String.format("DELETE FROM %s WHERE %s = ? AND %s = ?;", getTable(), getTotalIDColumn(), getKeyColumn()), (preparedStatement -> {
                preparedStatement.setString(1, totalID);
                preparedStatement.setString(2, key);
                preparedStatement.executeUpdate();
            }));
            return;
        }

        getPreparedStatement(String.format("UPDATE %s SET %s = ? WHERE %s = ? AND %s = ?;", getTable(), getValueColumn(), getTotalIDColumn(), getKeyColumn()), (preparedStatement -> {
            preparedStatement.setString(1, value);
            preparedStatement.setString(2, totalID);
            preparedStatement.setString(3, key);

            if (preparedStatement.executeUpdate() == 0) {
                insert(totalID, key, value);
            }
        }));
    }

    private void insert(@NotNull String totalID, @NotNull String key, @NotNull String value) {
        getPreparedStatement(String.format("INSERT INTO %s (%s, %s, %s) VALUES (?, ?, ?);", getTable(), getTotalIDColumn(), getKeyColumn(), getValueColumn()), (preparedStatement -> {
            preparedStatement.setString(1, totalID);
            preparedStatement.setString(2, key);
            preparedStatement.setObject(3, value);
            preparedStatement.executeUpdate();
        }));
    }

    @Override
    protected @NotNull CompletableFuture<Object[][]> execute(@NotNull QuerySelect select) throws BackendException {

        return CompletableFuture.supplyAsync(() -> {

            String[] values = new String[select.getFilterQueries().length];
            Object[] objects = {columns(select.getColumns()), getTable(), where(values, select.getFilterQueries())};
            return runSelect(SELECT_FORMAT.format(objects), values, select.getColumns());
        });

    }

    @Override
    protected @NotNull CompletableFuture<Long> execute(@NotNull QueryUpdate queryUpdate) throws BackendException {
        return CompletableFuture.supplyAsync(() -> {
            String[] updateValues = new String[queryUpdate.getColumns().length];
            String[] whereValues = new String[queryUpdate.getFilterQueries().length];

            Object[] objects = {getTable(), update(updateValues, queryUpdate.getColumns()), where(whereValues, queryUpdate.getFilterQueries())};

            String[] total = new String[updateValues.length + whereValues.length];
            System.arraycopy(updateValues, 0, total, 0, updateValues.length);
            System.arraycopy(whereValues, 0, total, updateValues.length, whereValues.length);

            return runUpdate(UPDATE_FORMAT.format(objects), total);
        });
    }

    public abstract void getPreparedStatement(@NotNull String query, @NotNull PreparedStatementExecutor preparedStatementExecutor);

    private long runUpdate(@NotNull String sql, @NotNull String @NotNull [] parameterValues) throws BackendException {
        AtomicLong atomicLong = new AtomicLong();
        getPreparedStatement(sql, preparedStatement -> {
            for (int i = 0; i < parameterValues.length; i++) {
                preparedStatement.setString(i + 1, parameterValues[i]);
            }
            atomicLong.set(preparedStatement.executeUpdate());
        });
        return atomicLong.get();
    }

    private @NotNull Object @NotNull [] @NotNull [] runSelect(@NotNull String sql, @NotNull String @NotNull [] parameterValues, @NotNull Column... columns) throws BackendException {
        List<Object[]> array = new ArrayList<>();
        getPreparedStatement(sql, preparedStatement -> {
            for (int i = 0; i < parameterValues.length; i++) {
                preparedStatement.setString(i + 1, parameterValues[i]);
            }
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    handleResult(columns, resultSet, array);
                }
            }
        });
        return array.toArray(new Object[0][]);
    }

    private void handleResult(@NotNull Column @NotNull [] columns, @NotNull ResultSet resultSet, List<Object[]> array) throws SQLException {
        Object[] result = new Object[columns.length];
        for (int i = 0; i < columns.length; i++) {
            Column column = columns[i];
            String value = resultSet.getString(getColumn(column));
            if (column.equals(Column.VALUE)) {
                try {
                    result[i] = deserialize(value);
                } catch (ClassNotFoundException classNotFoundException) {
                    throw new RuntimeException(classNotFoundException); //TODO
                }
                continue;
            }
            result[i] = value;
        }
        array.add(result);
    }

    private @NotNull String columns(@NotNull Column @NotNull [] column) {
        return Arrays.stream(column).map(this::getColumn).collect(Collectors.joining(", "));
    }

    private @NotNull String where(@NotNull String[] values, @NotNull FilterQuery @NotNull ... filterQueries) {
        return IntStream.range(0, filterQueries.length).mapToObj(i -> {
            FilterQuery filterQuery = filterQueries[i];
            String column = getColumn(filterQuery.getColumn());

            String clause = switch (filterQuery.getFilterType()) {
                case EXACT_MATCH -> {
                    values[i] = filterQuery.getValue();
                    yield column + " = ?";
                }
                case STARTS_WITH -> {
                    values[i] = filterQuery.getValue() + "%";
                    yield column + " LIKE ?";
                }
                case ENDS_WITH -> {
                    values[i] = "%" + filterQuery.getValue();
                    yield column + " LIKE ?";
                }
            };

            return i == 0 ? clause : filterQuery.getFilterOperator() + " " + clause;
        }).collect(Collectors.joining());
    }

    private @NotNull String update(@NotNull String[] values, @NotNull QueryUpdateDirective @NotNull ... columns) {
        Arrays.setAll(values, i -> columns[i].value());

        return String.join(", ", Arrays.stream(columns).map(update -> {
            Object[] column = {getColumn(update.column())};
            return VAR_PATTERN.format(column);
        }).toArray(String[]::new));
    }
}
