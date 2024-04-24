package net.kissenpvp.core.database.jdbc.query;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.kissenpvp.core.api.database.connection.PreparedStatementExecutor;
import net.kissenpvp.core.api.database.meta.Table;
import net.kissenpvp.core.api.database.queryapi.Column;
import net.kissenpvp.core.api.database.queryapi.select.QuerySelect;
import net.kissenpvp.core.database.jdbc.KissenJDBCMeta;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A subclass of {@link JDBCQueryExecutor} for executing JDBC select queries.
 *
 * <p>
 * This class extends {@link JDBCQueryExecutor} and provides functionality specific to executing JDBC select queries.
 * It is annotated with {@code @Getter} to automatically generate getter methods for its fields.
 *
 * @see JDBCQueryExecutor
 */
@Slf4j
@Getter
public class JDBCSelectQueryExecutor extends JDBCQueryExecutor {

    private static final String SELECT_FORMAT = "SELECT %s FROM %s WHERE %s;";
    private final QuerySelect query;

    /**
     * Constructs a JDBCSelectQueryExecutor with the specified {@link QuerySelect} and {@link KissenJDBCMeta}.
     *
     * @param select the {@link QuerySelect} object representing the select query
     * @param meta   the {@link KissenJDBCMeta} object representing the JDBC metadata
     * @throws NullPointerException if either {@code select} or {@code meta} is {@code null}
     */
    public JDBCSelectQueryExecutor(@NotNull QuerySelect select, @NotNull KissenJDBCMeta meta) {
        super(meta);
        this.query = select;
    }

    public @NotNull String constructSQL(@NotNull List<String> values) {
        String table = getMeta().getTable().getTable();
        return String.format(SELECT_FORMAT, columns(), table, where(values, getQuery().getFilterQueries()));
    }

    public @NotNull PreparedStatementExecutor executeStatement(@NotNull List<Object[]> results, @NotNull String[] parameter) {
        return statement -> {
            setStatementValues(statement, parameter);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    results.add(handleResult(resultSet));
                }
            }
        };
    }

    /**
     * Handles the result of a database query by extracting values for the specified columns from the {@link ResultSet}.
     *
     * <p>
     * This method handles the result of a database query by extracting values for the specified columns from the provided {@link ResultSet}.
     * It iterates through each column in the array and calls the {@link #handleResult(ResultSet, Column)} method to retrieve the corresponding value.
     * The values are then stored in an array and returned.
     *
     * @param resultSet the {@link ResultSet} containing the query result
     * @return an array of {@link Object} containing the values for the specified columns
     * @throws SQLException         if a database access error occurs or if one of the specified columns is not found in the result set
     * @throws NullPointerException if either {@code columns} or {@code resultSet} is {@code null}
     * @see Column
     * @see ResultSet
     */
    private @NotNull Object @NotNull [] handleResult(@NotNull ResultSet resultSet) throws SQLException {
        Column[] columns = getQuery().getColumns();
        Object[] result = new Object[columns.length];
        for (int i = 0; i < columns.length; i++) {
            result[i] = handleResult(resultSet, columns[i]);
        }
        return result;
    }

    /**
     * Handles the result of a database query for a single column by extracting its value from the {@link ResultSet}.
     *
     * <p>
     * This method handles the result of a database query for a single column by extracting its value from the provided {@link ResultSet}.
     * It retrieves the value for the specified column using the {@link ResultSet#getString(String)} method,
     * and if the column is {@link Column#VALUE}, it attempts to deserialize the value using the metadata.
     *
     * @param resultSet the {@link ResultSet} containing the query result
     * @param column    the {@link Column} representing the column for which the value should be extracted
     * @return the value of the specified column
     * @throws SQLException         if a database access error occurs or if the specified column is not found in the result set
     * @throws NullPointerException if either {@code resultSet} or {@code column} is {@code null}
     * @see Column
     * @see ResultSet
     */
    private @NotNull Object handleResult(@NotNull ResultSet resultSet, @NotNull Column column) throws SQLException {
        String value = resultSet.getString(getMeta().getTable().getColumn(column));
        if (column.equals(Column.VALUE)) {
            String clazzName = resultSet.getString(getMeta().getTable().getTypeColumn());
            String plugin = resultSet.getString(getMeta().getTable().getPluginColumn());
            return getMeta().deserialize(clazzName, value);
        }
        return value;
    }

    /**
     * Constructs a comma-separated list of column names for use in SQL statements.
     *
     * <p>
     * This method constructs a comma-separated list of column names based on the provided array of {@link Column} objects.
     * It retrieves the column names from the metadata and appends an optional type column for the {@link Column#VALUE} column.
     *
     * @return a comma-separated list of column names
     * @throws NullPointerException if {@code column} is {@code null}
     * @see Column
     */
    private @NotNull String columns() {
        return Arrays.stream(getQuery().getColumns()).map(columns -> {
            Table table = getMeta().getTable();
            String current = table.getColumn(columns);
            if (Objects.equals(columns, Column.VALUE)) {
                return String.format("%s, %s, %s", table.getPluginColumn(), table.getTypeColumn(), current);
            }
            return current;
        }).collect(Collectors.joining(", "));
    }
}
