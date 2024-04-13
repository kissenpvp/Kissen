package net.kissenpvp.core.database.jdbc.query;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.kissenpvp.core.api.database.connection.PreparedStatementExecutor;
import net.kissenpvp.core.api.database.queryapi.Column;
import net.kissenpvp.core.api.database.queryapi.FilterQuery;
import net.kissenpvp.core.api.database.queryapi.select.QuerySelect;
import net.kissenpvp.core.database.jdbc.KissenJDBCMeta;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
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

    private static final String SELECT_FORMAT = "SELECT %s FROM %s;";
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

    /**
     * Constructs a SQL SELECT statement based on the provided values for WHERE conditions.
     *
     * <p>
     * This method constructs a SQL SELECT statement based on the provided values for WHERE conditions.
     * It retrieves the column names using the {@link #columns(Column[])} method, and constructs the SELECT statement
     * using the {@link #selectFromWhere(QuerySelect, List)}  method. The column names and the SELECT statement are
     * then formatted into a single SQL string using the {@code SELECT_FORMAT} constant.
     *
     * @param values an array of {@link String} representing the values for the WHERE conditions
     * @return a SQL SELECT statement
     * @throws NullPointerException if {@code values} is {@code null}
     * @see #columns(Column[])
     * @see #selectFromWhere(QuerySelect, List)
     */
    public @NotNull String constructSQL(@NotNull List<String> values) {
        Object[] args = {columns(getQuery().getColumns()), selectFromWhere(getQuery(), values)};
        return SELECT_FORMAT.formatted(args);
    }

    /**
     * Executes a SQL statement and populates a list with the query results.
     *
     * <p>
     * This method executes the provided SQL statement with the specified parameters and populates the provided list
     * with the query results. It sets the parameter values for the prepared statement using the {@link #setStatementValues(PreparedStatement, String[])}
     * method, executes the statement, and iterates through the result set. For each row in the result set, it calls
     * the {@link #handleResult(Column[], ResultSet)} method to handle the result and adds it to the list.
     *
     * @param results   a {@link List} to populate with the query results
     * @param parameter an array of {@link String} representing the parameter values for the SQL statement
     * @param columns   an array of {@link Column} representing the columns to extract from the result set
     * @return a {@link PreparedStatementExecutor} for further processing
     * @throws NullPointerException if {@code results}, {@code parameter}, or {@code columns} is {@code null}
     * @see PreparedStatementExecutor
     * @see #setStatementValues(PreparedStatement, String[])
     * @see #handleResult(Column[], ResultSet)
     */
    public @NotNull PreparedStatementExecutor executeStatement(@NotNull List<Object[]> results, @NotNull String[] parameter, @NotNull Column... columns) {
        return new PreparedStatementExecutor() {
            @Override
            public void execute(@NotNull PreparedStatement statement) throws SQLException {
                setStatementValues(statement, parameter);

                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        results.add(handleResult(columns, resultSet));
                    }
                }
            }

            @Override
            public boolean handle(@NotNull SQLException throwable) {
                log.error("An exception occurred when executing sql request.", throwable); //TODO
                return true;
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
     * @param columns   an array of {@link Column} representing the columns for which values should be extracted
     * @param resultSet the {@link ResultSet} containing the query result
     * @return an array of {@link Object} containing the values for the specified columns
     * @throws SQLException         if a database access error occurs or if one of the specified columns is not found in the result set
     * @throws NullPointerException if either {@code columns} or {@code resultSet} is {@code null}
     * @see Column
     * @see ResultSet
     */
    private @NotNull Object @NotNull [] handleResult(@NotNull Column @NotNull [] columns, @NotNull ResultSet resultSet) throws SQLException {
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
            return getMeta().deserialize(plugin, clazzName, value);
        }
        return value;
    }

    /**
     * Constructs a SQL SELECT statement with a WHERE clause based on the provided {@link QuerySelect} and values.
     *
     * <p>
     * This method constructs a SQL SELECT statement with a WHERE clause based on the provided {@link QuerySelect} object
     * and values for WHERE conditions. It retrieves the table name from the metadata and generates the WHERE clause using
     * the {@link #where(List, FilterQuery...)} method. The SELECT statement is constructed by joining the table name and
     * the WHERE clause.
     *
     * @param select the {@link QuerySelect} object representing the SELECT query
     * @param values an array of {@link String} representing the values for the WHERE conditions
     * @return a SQL SELECT statement with a WHERE clause
     * @throws NullPointerException if {@code select} is {@code null}
     * @see QuerySelect
     * @see #where(List, FilterQuery...)
     */
    private @NotNull String selectFromWhere(@NotNull QuerySelect select, @NotNull List<String> values) {
        StringJoiner table = new StringJoiner(" ").add(getMeta().getTable().getTable());
        if (select.getFilterQueries().length!=0) {
            table.add("WHERE").add(where(values, select.getFilterQueries()));
        }
        return table.toString();
    }

    /**
     * Constructs a comma-separated list of column names for use in SQL statements.
     *
     * <p>
     * This method constructs a comma-separated list of column names based on the provided array of {@link Column} objects.
     * It retrieves the column names from the metadata and appends an optional type column for the {@link Column#VALUE} column.
     *
     * @param column an array of {@link Column} representing the columns to include in the list
     * @return a comma-separated list of column names
     * @throws NullPointerException if {@code column} is {@code null}
     * @see Column
     */
    private @NotNull String columns(@NotNull Column @NotNull [] column) {
        return Arrays.stream(column).map(col -> {
            String addType = ", %s, %s"; // add type and plugin
            return getMeta().getTable().getColumn(col) + (Objects.equals(col, Column.VALUE) ? addType.formatted(getMeta().getTable().getPluginColumn(), getMeta().getTable().getTypeColumn()):"");
        }).collect(Collectors.joining(", "));
    }
}
