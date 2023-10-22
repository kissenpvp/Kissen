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
import com.google.gson.reflect.TypeToken;
import net.kissenpvp.core.api.database.connection.PreparedStatementExecutor;
import net.kissenpvp.core.api.database.meta.BackendException;
import net.kissenpvp.core.api.database.meta.Meta;
import net.kissenpvp.core.api.database.queryapi.Column;
import net.kissenpvp.core.api.database.queryapi.FilterQuery;
import net.kissenpvp.core.api.database.queryapi.QuerySelect;
import net.kissenpvp.core.api.database.queryapi.QueryUpdate;
import net.kissenpvp.core.api.database.queryapi.QueryUpdateDirective;
import net.kissenpvp.core.database.KissenBaseMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

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

    public KissenJDBCMeta(String table, String totalIDColumn, String keyColumn, String valueColumn) throws BackendException {
        super(table, totalIDColumn, keyColumn, valueColumn);
        getPreparedStatement(String.format("CREATE TABLE IF NOT EXISTS %s (%s VARCHAR(100) NOT NULL, %s VARCHAR(100) NOT NULL, %s TEXT NOT NULL);", getTable(), getTotalIDColumn(), getKeyColumn(), getValueColumn()), PreparedStatement::executeUpdate);
    }

    @Override
    public void purge(@NotNull String totalID) throws BackendException {
        getPreparedStatement(String.format("DELETE FROM %s WHERE %s = ?;", getTable(), getKeyColumn()), preparedStatement -> {
            preparedStatement.setString(1, totalID);
            preparedStatement.executeUpdate();
        });
    }

    @Override
    public @NotNull @Unmodifiable List<String> getStringList(@NotNull String totalID, @NotNull String key) throws BackendException {

        AtomicReference<List<String>> listAtomicReference = new AtomicReference<>(new ArrayList<>());
        getPreparedStatement(String.format("SELECT %s FROM %s WHERE %s = ? AND %s = ?;", getValueColumn(), getTable(), getTotalIDColumn(), getKeyColumn()), (preparedStatement ->
        {
            preparedStatement.setString(1, totalID);
            preparedStatement.setString(2, key);
            try(ResultSet resultSet = preparedStatement.executeQuery())
            {
                if (resultSet.next()) {
                    String json = resultSet.getString(getValueColumn());
                    Type listType = new TypeToken<List<String>>() {
                    }.getType();
                    listAtomicReference.set(new Gson().fromJson(json, listType));
                }
            }
        }));
        return listAtomicReference.get();
    }

    @Override
    public void setString(@NotNull String totalID, @NotNull String key, @Nullable String value) throws BackendException {
        getPreparedStatement(String.format("DELETE FROM %s WHERE %s = ? AND %s = ?;", getTable(), getTotalIDColumn(), getKeyColumn()), (preparedStatement ->
        {
            preparedStatement.setString(1, totalID);
            preparedStatement.setString(2, key);
            preparedStatement.executeUpdate();
        })); // I love sql and especially sqlite

        if(value != null)
        {
            getPreparedStatement(String.format("INSERT INTO %s (%s, %s, %s) VALUES (?, ?, ?);", getTable(), getTotalIDColumn(), getKeyColumn(), getValueColumn()), (preparedStatement ->
            {
                preparedStatement.setString(1, totalID);
                preparedStatement.setString(2, key);
                preparedStatement.setString(3, value);
                preparedStatement.executeUpdate();
            }));
        }
    }

    @Override
    public void setStringList(@NotNull String totalID, @NotNull String key, @Nullable List<String> value) throws BackendException {
        setString(totalID, key, new Gson().toJson(value));
    }

    @Override
    protected @NotNull String[][] execute(@NotNull QuerySelect querySelect) throws BackendException {

        StringBuilder sql = new StringBuilder("SELECT").append(" ");
        sql.append(transformSelectColumns(querySelect.getColumns())).append(" ");
        sql.append("FROM").append(" ").append(getTable());
        String[] values = new String[0];
        if (querySelect.getFilterQueries().length != 0)
        {
            sql.append(" ").append("WHERE").append(" ");
            values = transformFilters(sql, querySelect.getFilterQueries());
        }
        sql.append(";");

        return runSelectQuery(sql.toString(), values, querySelect.getColumns());
    }

    @Override
    protected long execute(@NotNull QueryUpdate queryUpdate) throws BackendException {
        StringBuilder sql = new StringBuilder("UPDATE").append(" ").append(getTable()).append(" ").append("SET").append(" ");
        String[] updateValues = transformUpdateColumns(sql, queryUpdate.getColumns());
        String[] whereValues = transformFilters(sql, queryUpdate.getFilterQueries());

        String[] total = new String[updateValues.length + whereValues.length];
        System.arraycopy(updateValues, 0, total, 0, updateValues.length);
        System.arraycopy(whereValues, 0, total, updateValues.length, whereValues.length);

        return runUpdateQuery(sql.toString(), total, Arrays.stream(queryUpdate.getColumns()).map(QueryUpdateDirective::column).distinct().toArray(Column[]::new));
    }

    public abstract void getPreparedStatement(@NotNull String query, @NotNull PreparedStatementExecutor preparedStatementExecutor);

    private long runUpdateQuery(@NotNull String sql, @NotNull String @NotNull [] parameterValues, @NotNull Column... columns) throws BackendException {
        AtomicLong atomicLong = new AtomicLong();
        getPreparedStatement(sql, preparedStatement -> {
            for (int i = 0; i < parameterValues.length; i++) {
                preparedStatement.setString(i + 1, parameterValues[i]);
            }
            atomicLong.set(preparedStatement.executeUpdate());
        });
        return atomicLong.get();
    }

    private @NotNull String @NotNull [] @NotNull [] runSelectQuery(@NotNull String sql, @NotNull String @NotNull [] parameterValues, @NotNull Column... columns) throws BackendException {
        List<String[]> strings = new ArrayList<>();
        getPreparedStatement(sql, preparedStatement -> {
            for (int i = 0; i < parameterValues.length; i++) {
                preparedStatement.setString(i + 1, parameterValues[i]);
            }
            try(ResultSet resultSet = preparedStatement.executeQuery())
            {
                while (resultSet.next()) {
                    String[] result = new String[columns.length];
                    for (int i = 0; i < columns.length; i++) {
                        result[i] = resultSet.getString(getColumn(columns[i]));
                    }
                    strings.add(result);
                }
            }
        });
        return strings.toArray(new String[0][]);
    }

    private @NotNull String @NotNull [] transformUpdateColumns(@NotNull StringBuilder stringBuilder, @NotNull QueryUpdateDirective @NotNull... columns) {
        String[] updateValues = new String[columns.length];
        for (int i = 0; i < columns.length; i++) {
            stringBuilder.append(getColumn(columns[i].column())).append("=").append(" ").append("?");
            updateValues[i] = columns[i].value();
            if(i <= columns.length)
            {
                stringBuilder.append(", ");
            }
        }
        return updateValues;
    }

    private @NotNull String transformSelectColumns(@NotNull Column @NotNull [] column) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Column currentColumn : column) {
            stringBuilder.append(getColumn(currentColumn)).append(", ");
        }
        return stringBuilder.substring(0, stringBuilder.length() - ", ".length());
    }

    private String @NotNull [] transformFilters(@NotNull StringBuilder sql, @NotNull FilterQuery @NotNull ... filterQueries) {

        String[] values = new String[filterQueries.length];
        for (int i = 0; i < filterQueries.length; i++) {

            FilterQuery filterQuery = filterQueries[i];
            String column = getColumn(filterQuery.getColumn());

            sql.append(switch (filterQuery.getFilterType()) {
                case EQUALS -> {
                    values[i] = filterQuery.getValue();
                    yield column + " = ?";
                }
                case START -> {
                    values[i] = filterQuery.getValue() + "%";
                    yield column + " LIKE ?";
                }
                case END -> {
                    values[i] = "%" + filterQuery.getValue();
                    yield column + " LIKE ?";
                }
            });
            if (i < (filterQueries.length - 1)) {
                sql.append(" AND ");
            }
        }
        return values;
    }
}
