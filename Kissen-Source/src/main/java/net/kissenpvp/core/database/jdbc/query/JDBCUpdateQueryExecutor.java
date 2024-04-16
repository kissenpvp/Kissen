package net.kissenpvp.core.database.jdbc.query;

import lombok.AccessLevel;
import lombok.Getter;
import net.kissenpvp.core.api.database.connection.PreparedStatementExecutor;
import net.kissenpvp.core.api.database.queryapi.Column;
import net.kissenpvp.core.api.database.queryapi.FilterQuery;
import net.kissenpvp.core.api.database.queryapi.update.QueryUpdate;
import net.kissenpvp.core.api.database.queryapi.update.Update;
import net.kissenpvp.core.database.jdbc.KissenJDBCMeta;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A subclass of {@link JDBCQueryExecutor} for executing JDBC update queries.
 *
 * <p>
 * This class extends {@link JDBCQueryExecutor} and provides functionality specific to executing JDBC update queries.
 * It is annotated with {@code @Getter(AccessLevel.PROTECTED)} to generate protected getter methods for its fields.
 *
 * @see JDBCQueryExecutor
 */
@Getter(AccessLevel.PROTECTED)
public class JDBCUpdateQueryExecutor extends JDBCQueryExecutor {

    private static final String UPDATE_FORMAT_WHERE = "UPDATE %s SET %s WHERE (%s) AND %s = ?;";
    private static final String UPDATE_FORMAT = "UPDATE %s SET %s WHERE %s = ?;";
    private final QueryUpdate update;

    /**
     * Constructs a JDBCUpdateQueryExecutor with the specified {@link QueryUpdate} and {@link KissenJDBCMeta}.
     *
     * @param update the {@link QueryUpdate} object representing the update query
     * @param meta   the {@link KissenJDBCMeta} object representing the JDBC metadata
     * @throws NullPointerException if either {@code update} or {@code meta} is {@code null}
     */
    public JDBCUpdateQueryExecutor(@NotNull QueryUpdate update, @NotNull KissenJDBCMeta meta) {
        super(meta);
        this.update = update;
    }

    public @NotNull String constructSQL(@NotNull List<String> update, @NotNull List<String> where) {
        FilterQuery[] queries = getUpdate().getFilterQueries();
        String updateSql = update(update, getUpdate().getColumns());
        String pluginColumn = getMeta().getTable().getPluginColumn();

        if (queries.length==0) {
            return String.format(UPDATE_FORMAT, getMeta().getTable(), updateSql, pluginColumn);
        }

        String whereSql = where(where, queries);
        return String.format(UPDATE_FORMAT_WHERE, getMeta().getTable(), updateSql, whereSql, pluginColumn);
    }

    /**
     * Executes a SQL statement and returns a {@link PreparedStatementExecutor} for further processing.
     * <p>
     * This method executes the provided SQL statement with the specified parameters and returns a {@link PreparedStatementExecutor}
     * for further processing. It sets the parameter values for the prepared statement using the {@link #setStatementValues(PreparedStatement, String[])}
     * method and updates the count using the provided {@link AtomicLong}.
     *
     * @param parameter an array of {@link String} representing the parameter values for the SQL statement
     * @param count     an {@link AtomicLong} to hold the count of affected rows
     * @return a {@link PreparedStatementExecutor} for further processing
     * @throws NullPointerException if any of the parameters is {@code null}
     * @see PreparedStatementExecutor
     * @see #setStatementValues(PreparedStatement, String[])
     */
    public @NotNull PreparedStatementExecutor executeStatement(@NotNull String[] parameter, @NotNull AtomicLong count) {
        return preparedStatement -> {
            setStatementValues(preparedStatement, parameter);
            count.set(preparedStatement.executeUpdate());
        };
    }

    /**
     * Constructs a string representing the SET clause of an SQL UPDATE statement.
     *
     * <p>This method constructs a string representing the SET clause of an SQL UPDATE statement based on the provided
     * array of {@link Update} objects. Each directive is used to set a specific column to a new value.
     * The resulting string contains comma-separated assignments of column names to their new values.</p>
     *
     * @param values  an array of string values used in constructing the SET clause
     * @param columns an array of {@link Update} objects representing the columns and their new values
     * @return a string representing the SET clause of an SQL UPDATE statement
     * @throws NullPointerException if the array of values or columns is {@code null}
     * @see Update
     */
    private @NotNull String update(@NotNull List<String> values, @NotNull Update @NotNull ... columns) {
        List<String> col = new ArrayList<>();
        if (Objects.nonNull(getMeta().getPlugin())) {
            col.add(getMeta().getTable().getPluginColumn());
            values.add(getMeta().getPlugin().getName());
        }

        for (Update column : columns) {
            if (Objects.equals(column.column(), Column.VALUE)) {
                String[] serialized = getMeta().serialize(column.value());
                col.add(getMeta().getTable().getTypeColumn());
                values.add(serialized[0]);
                col.add(getMeta().getTable().getColumn(Column.VALUE));
                values.add(serialized[1]);
                continue;
            }
            col.add(getMeta().getTable().getColumn(column.column()));
            values.add(column.value().toString());
        }

        return String.join(", ", col.stream().map("%s = ?"::formatted).toArray(String[]::new));
    }
}
