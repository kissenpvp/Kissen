package net.kissenpvp.database;

import net.kissenpvp.api.database.PersistableEntity;
import net.kissenpvp.api.database.Repository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.StreamSupport;

public abstract class InternalRepository<P, T extends PersistableEntity<P>> implements Repository<P, T>
{
    private static final Logger log = LoggerFactory.getLogger(InternalRepository.class);
    private final Connection connection;
    private final String table;

    protected abstract @NotNull @UnmodifiableView T toEntity(@NotNull P id, @NotNull ResultSet resultSet) throws SQLException;

    protected abstract @NotNull @UnmodifiableView T toEntity(@NotNull ResultSet resultSet) throws SQLException;

    private @NotNull Collection<T> loadAll(@NotNull ResultSet resultSet) throws SQLException
    {
        List<T> data = new ArrayList<>();
        while (resultSet.next())
        {
            data.add(toEntity(resultSet));
        }
        return data;
    }

    public InternalRepository(@NotNull String table, @NotNull Connection connection)
    {
        if (table.isBlank() || table.length() > 64 || !table.matches("^[a-zA-Z_][a-zA-Z0-9_$]{0,63}$"))
        {
            String message = "The chosen table name %s, is not valid for a database table.";
            throw new IllegalArgumentException(String.format(message, table));
        }

        this.table = table;
        this.connection = connection;
    }

    protected abstract @NotNull String createTableQuery();


    public @NotNull String table()
    {
        return table;
    }

    protected abstract @NotNull String findQuery();

    @Override public @NotNull CompletableFuture<T> find(@NotNull P id)
    {
        return CompletableFuture.supplyAsync(() -> query(findQuery(), (statement ->
        {
            statement.setObject(1, id);

            try (ResultSet resultSet = statement.executeQuery())
            {
                if (!resultSet.next())
                {
                    return null;
                }

                return toEntity(id, resultSet);
            }
        })));
    }

    @Override public @NotNull CompletableFuture<@UnmodifiableView Collection<T>> findAll(@NotNull Iterable<P> id)
    {
        List<P> primaryKeys = StreamSupport.stream(id.spliterator(), false).toList();

        if(primaryKeys.isEmpty())
        {
            return CompletableFuture.completedFuture(Collections.emptyList());
        }

        String placeholders = String.join(",", Collections.nCopies(primaryKeys.size(), "?"));
        String sql = "SELECT * FROM %s WHERE id IN (" + placeholders + ");";

        return CompletableFuture.supplyAsync(() -> query(sql, statement -> {
            int index = 1;
            for (P current : primaryKeys) {
                statement.setObject(index++, current);
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                return massToEntity(resultSet);
            }
        }));
    }

    @Override public @NotNull CompletableFuture<@UnmodifiableView Collection<T>> findAll()
    {
        return CompletableFuture.supplyAsync(() -> query("SELECT * FROM %s;", (statement ->
        {
            try (ResultSet resultSet = statement.executeQuery())
            {
                return massToEntity(resultSet);
            }
        })));
    }

    private @NotNull @UnmodifiableView Collection<T> massToEntity(@NotNull ResultSet resultSet) throws SQLException
    {
        List<T> data = new ArrayList<>();
        while (resultSet.next())
        {
            data.add(toEntity(resultSet));
        }
        return Collections.unmodifiableList(data);
    }

    @Override public @NotNull CompletableFuture<Boolean> has(@NotNull P id)
    {
        return CompletableFuture.supplyAsync(() -> query("SELECT id FROM %s WHERE id = ?;", (statement ->
        {
            statement.setObject(1, id);
            try (ResultSet resultSet = statement.executeQuery())
            {
                return resultSet.next();
            }
        })));
    }

    @Override public @NotNull CompletableFuture<Void> save(@NotNull T id)
    {
        return saveAll(Collections.singleton(id));
    }

    protected <X> @NotNull X query(@NotNull String sql, @NotNull QueryExecutor<X> queryExecutor)
    {
        try
        {
            return unsafeQuery(sql, queryExecutor);
        }
        catch (SQLException sqlException)
        {
            log.error("There has been an error while executing the query {}.", sql, sqlException);
            throw new IllegalStateException(sqlException);
        }
    }

    protected <X> @NotNull X unsafeQuery(@NotNull String sql, @NotNull QueryExecutor<X> queryExecutor) throws SQLException
    {
        //noinspection SqlSourceToSinkFlow
        try (PreparedStatement statement = this.connection.prepareStatement(String.format(sql, table())))
        {
            return Objects.requireNonNull(queryExecutor.executeQuery(statement));
        }
    }
}
