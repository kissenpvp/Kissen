package net.kissenpvp.core.database.jdbc.query;

import lombok.AccessLevel;
import lombok.Getter;
import net.kissenpvp.core.api.database.connection.PreparedStatementExecutor;
import net.kissenpvp.core.api.database.queryapi.Column;
import net.kissenpvp.core.api.database.queryapi.update.QueryUpdate;
import net.kissenpvp.core.api.database.queryapi.update.Update;
import net.kissenpvp.core.database.jdbc.KissenJDBCMeta;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;
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

    private static final String UPDATE_FORMAT = "UPDATE %s SET %s;";
    private final QueryUpdate update;

    /**
     * Constructs a JDBCUpdateQueryExecutor with the specified {@link QueryUpdate} and {@link KissenJDBCMeta}.
     *
     * @param update the {@link QueryUpdate} object representing the update query
     * @param meta the {@link KissenJDBCMeta} object representing the JDBC metadata
     * @throws NullPointerException if either {@code update} or {@code meta} is {@code null}
     */
    public JDBCUpdateQueryExecutor(@NotNull QueryUpdate update, @NotNull KissenJDBCMeta meta) {
        super(meta);
        this.update = update;
    }

    /**
     * Constructs a SQL statement for executing an update operation.
     * <p>
     * This method constructs a SQL statement for executing an update operation based on the provided update values and WHERE conditions.
     * It retrieves the table name from the metadata, then formats the SQL statement using the {@code UPDATE_FORMAT} constant
     * and the {@link #updateWhere(String[], String[], Update[])} method to generate the UPDATE clause with the WHERE condition.
     *
     * @param update an array of {@link String} representing the update values
     * @param where an array of {@link String} representing the values for the WHERE conditions
     * @return a SQL statement for executing an update operation
     * @throws NullPointerException if any of the parameters is {@code null}
     * @see #updateWhere(String[], String[], Update[])
     */
    public @NotNull String constructSQL(@NotNull String[] update, @NotNull String[] where) {
        String table = getMeta().getTable();
        return UPDATE_FORMAT.formatted(table, updateWhere(where, update, getUpdate().getColumns()));
    }

    /**
     * Executes a SQL statement and returns a {@link PreparedStatementExecutor} for further processing.
     * <p>
     * This method executes the provided SQL statement with the specified parameters and returns a {@link PreparedStatementExecutor}
     * for further processing. It sets the parameter values for the prepared statement using the {@link #setStatementValues(PreparedStatement, String[])}
     * method and updates the count using the provided {@link AtomicLong}.
     *
     * @param sql the SQL statement to execute
     * @param parameter an array of {@link String} representing the parameter values for the SQL statement
     * @param count an {@link AtomicLong} to hold the count of affected rows
     * @return a {@link PreparedStatementExecutor} for further processing
     * @throws NullPointerException if any of the parameters is {@code null}
     * @see PreparedStatementExecutor
     * @see #setStatementValues(PreparedStatement, String[])
     */
    public @NotNull PreparedStatementExecutor executeStatement(@NotNull String sql, @NotNull String[] parameter, @NotNull AtomicLong count) {
        return preparedStatement -> {
            setStatementValues(preparedStatement, parameter);
            count.set(preparedStatement.executeUpdate());
        };
    }

    /**
     * Constructs a SQL UPDATE statement with a WHERE clause based on the provided values and update columns.
     * <p>
     * This method constructs a SQL UPDATE statement with a WHERE clause based on the provided values for WHERE conditions,
     * update values, and update columns. It utilizes the {@link #update(String[], Update[])} and {@link #where(String[], net.kissenpvp.core.api.database.queryapi.FilterQuery...)}
     * methods to generate the UPDATE and WHERE clauses, respectively, and then joins them together.
     *
     * @param whereValues an array of {@link String} representing the values for the WHERE conditions
     * @param updateValues an array of {@link String} representing the values to be updated
     * @param columns an array of {@link Update} representing the columns to be updated with corresponding new values
     * @return a SQL UPDATE statement with a WHERE clause
     * @throws NullPointerException if any of the parameters is {@code null}
     * @see Update
     * @see #update(String[], Update[])
     * @see #where(String[], net.kissenpvp.core.api.database.queryapi.FilterQuery...)
     */
    private @NotNull String updateWhere(@NotNull String[] whereValues, @NotNull String[] updateValues, @NotNull Update[] columns) {
        StringJoiner table = new StringJoiner(" ").add(update(updateValues, columns));
        if (getUpdate().getFilterQueries().length != 0) {
            table.add("WHERE").add(where(whereValues, getUpdate().getFilterQueries()));
        }
        return table.toString();
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
    private @NotNull String update(@NotNull String @NotNull [] values, @NotNull Update @NotNull ... columns) {
        String[] col = new String[values.length];
        int c = 0;
        for (int i = 0; i < columns.length; i++) {
            int k = i + c;
            if(Objects.equals(columns[i].column(), Column.VALUE))
            {
                String[] serialized = getMeta().serialize(columns[i].value());
                col[k] = getMeta().getTypeColumn();
                values[k] = serialized[0];
                c++;
                col[k + 1] = getMeta().getColumn(Column.VALUE);
                values[k + 1] = serialized[1];
                continue;
            }
            col[k] = getMeta().getColumn(columns[i].column());
            values[k] = columns[i].value().toString();
        }

        return String.join(", ", Arrays.stream(col).map("%s = ?"::formatted).toArray(String[]::new));
    }
}
