package net.kissenpvp.core.database.jdbc.query;

import lombok.Getter;
import net.kissenpvp.core.api.database.connection.PreparedStatementExecutor;
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
import java.util.StringJoiner;
import java.util.stream.Collectors;

@Getter
public class JDBCSelectQueryExecutor extends JDBCQueryExecutor {

    private static final String SELECT_FORMAT = "SELECT %s FROM %s;";
    private final QuerySelect query;

    public JDBCSelectQueryExecutor(@NotNull QuerySelect select, @NotNull KissenJDBCMeta meta) {
        super(meta);
        this.query = select;
    }

    public @NotNull String constructSQL(@NotNull String[] values) {
        Object[] args = {columns(getQuery().getColumns()), selectFromWhere(getQuery(), values)};
        return SELECT_FORMAT.formatted(args);
    }

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
                throwable.printStackTrace(); //TODO
                return true;
            }
        };
    }

    private @NotNull Object @NotNull [] handleResult(@NotNull Column @NotNull [] columns, @NotNull ResultSet resultSet) throws SQLException {
        Object[] result = new Object[columns.length];
        for (int i = 0; i < columns.length; i++) {
            result[i] = handleResult(resultSet, columns[i]);
        }
        return result;
    }

    private @NotNull Object handleResult(@NotNull ResultSet resultSet, @NotNull Column column) throws SQLException {
        String value = resultSet.getString(getMeta().getColumn(column));
        if (column.equals(Column.VALUE)) {
            try {
                return getMeta().deserialize(Class.forName(resultSet.getString(getMeta().getTypeColumn())), value);
            } catch (ClassNotFoundException ignored) {
            }
            throw new SQLException(); //TODO
        }
        return value;
    }

    private @NotNull String selectFromWhere(@NotNull QuerySelect select, String[] values) {
        StringJoiner table = new StringJoiner(" ").add(getMeta().getTable());
        if (select.getFilterQueries().length != 0) {
            table.add("WHERE").add(where(values, select.getFilterQueries()));
        }
        return table.toString();
    }

    private @NotNull String columns(@NotNull Column @NotNull [] column) {
        return Arrays.stream(column).map(col -> {
            String addType = ", %s"; // add type
            return getMeta().getColumn(col) + (Objects.equals(col, Column.VALUE) ? addType.formatted(getMeta().getTypeColumn()) : "");
        }).collect(Collectors.joining(", "));
    }
}
