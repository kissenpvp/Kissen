package net.kissenpvp.core.database.jdbc.query;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kissenpvp.core.api.database.queryapi.Column;
import net.kissenpvp.core.api.database.queryapi.FilterQuery;
import net.kissenpvp.core.database.jdbc.KissenJDBCMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Getter
@AllArgsConstructor
public class JDBCQueryExecutor {

    private final KissenJDBCMeta meta;

    @Contract(mutates = "param1")
    protected static void setStatementValues(@NotNull PreparedStatement preparedStatement, @NotNull String @NotNull [] parameterValues) throws SQLException {
        for (int i = 0; i < parameterValues.length; i++) {
            preparedStatement.setString(i + 1, parameterValues[i]);
        }
    }

    /**
     * Constructs a string representing the WHERE clause based on the provided filter queries.
     *
     * <p>This method constructs a string representing the WHERE clause of a SQL query based on the provided
     * array of {@link FilterQuery} objects. Each filter query is used to generate a condition within the WHERE clause.
     * The resulting string combines all conditions using logical operators such as "AND" or "OR".</p>
     *
     * @param values        an array of string values used in constructing the WHERE clause
     * @param filterQueries an array of {@link FilterQuery} objects representing the conditions of the WHERE clause
     * @return a string representing the WHERE clause
     * @throws NullPointerException if the array of values or filter queries is {@code null}
     * @see FilterQuery
     */
    protected @NotNull String where(@NotNull String[] values, @NotNull FilterQuery @NotNull ... filterQueries) {
        int length = filterQueries.length;
        return IntStream.range(0, length).mapToObj(whereEntry(values, filterQueries)).collect(Collectors.joining());
    }

    /**
     * Creates a function to generate a part of the WHERE clause for a given filter query.
     *
     * <p>This method creates a function that generates a part of the WHERE clause in an SQL query based on the provided
     * array of {@link FilterQuery} objects. Each filter query represents a condition within the WHERE clause. The
     * resulting function constructs a clause string for a specific filter query, considering the column name,
     * comparison operator, and value.</p>
     *
     * @param values        an array of string values used in constructing the WHERE clause
     * @param filterQueries an array of {@link FilterQuery} objects representing the conditions of the WHERE clause
     * @return a function mapping integer indices to parts of the WHERE clause based on filter queries
     * @throws NullPointerException if the array of values or filter queries is {@code null}
     * @see FilterQuery
     */
    @Contract(pure = true, value = "_, _ -> new")
    private @NotNull IntFunction<String> whereEntry(@NotNull String[] values, @NotNull FilterQuery[] filterQueries) {
        final String pattern = "%s REGEXP ?";
        return i -> {
            FilterQuery filterQuery = filterQueries[i];

            String serialized = filterQuery.getValue().toString();
            if (filterQuery.getColumn().equals(Column.VALUE)) {
                serialized = getMeta().serialize(filterQuery.getValue())[1]; // [0] is not required;
            }
            values[i] = serialized;

            String clause = pattern.formatted(getMeta().getColumn(filterQuery.getColumn()));
            return i == 0 ? clause : " " + filterQuery.getFilterOperator() + " " + clause;
        };
    }
}
