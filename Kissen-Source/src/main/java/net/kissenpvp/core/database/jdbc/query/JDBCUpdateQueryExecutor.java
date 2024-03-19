package net.kissenpvp.core.database.jdbc.query;

import net.kissenpvp.core.api.database.connection.PreparedStatementExecutor;
import net.kissenpvp.core.api.database.queryapi.Column;
import net.kissenpvp.core.api.database.queryapi.update.QueryUpdate;
import net.kissenpvp.core.api.database.queryapi.update.Update;
import net.kissenpvp.core.database.jdbc.KissenJDBCMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicLong;

public class JDBCUpdateQueryExecutor extends JDBCQueryExecutor {

    private static final String UPDATE_FORMAT = "UPDATE %s SET %s;";
    private final QueryUpdate update;

    public JDBCUpdateQueryExecutor(@NotNull QueryUpdate update, @NotNull KissenJDBCMeta meta) {
        super(meta);
        this.update = update;
    }

    public @NotNull String constructSQL(@NotNull String[] update, @NotNull String[] where) {
        String table = getMeta().getTable();
        return UPDATE_FORMAT.formatted(table, updateWhere(where, update, this.update.getColumns()));
    }

    public @NotNull PreparedStatementExecutor executeStatement(@NotNull String sql, @NotNull String[] parameter, @NotNull AtomicLong count) {
        return preparedStatement -> {
            setStatementValues(preparedStatement, parameter);
            count.set(preparedStatement.executeUpdate());
        };
    }

    private @NotNull String updateWhere(@NotNull String[] whereValues, @NotNull String[] updateValues, @NotNull Update[] columns) {
        StringJoiner table = new StringJoiner(" ").add(update(updateValues, columns));
        if (update.getFilterQueries().length != 0) {
            table.add("WHERE").add(where(whereValues, update.getFilterQueries()));
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
