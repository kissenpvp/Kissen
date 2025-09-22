package net.kissenpvp.network.actor.rank.repository;

import net.kissenpvp.api.network.actor.rank.Rank;
import net.kissenpvp.database.InternalCachedRepository;
import net.kissenpvp.database.InternalRepository;
import net.kissenpvp.network.actor.rank.InternalRank;
import org.jspecify.annotations.NonNull;


import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * A repository implementation for managing rank-related entities in the database.
 * {@code InternalRankRepository} extends {@link InternalRepository} and provides
 * additional methods specific to rank objects. It retrieves, transforms, and
 * persists rank data using predefined schemas and queries.
 *
 * <p>This class is designed for internal use and interacts with a specific database
 * table "kspv_rank". It ensures the efficient and thread-safe handling of rank
 * data, especially for operations such as saving and retrieving rank entities.</p>
 *
 * @author Bebdor augustus irilieres cesarius, Ivo Quiring
 */
public class InternalRankRepository extends InternalCachedRepository<String, Rank>
{
    public InternalRankRepository(@NonNull DataSource dataSource) throws NullPointerException
    {
        super(dataSource);
    }

    @Override protected @NonNull CompletableFuture<Optional<Rank>> findUncached(@NonNull String id) throws NullPointerException
    {
        String sql = "SELECT priority FROM ksvp_rank WHERE id = ?;";
        return CompletableFuture.supplyAsync(() -> query(sql, (statement ->
        {
            statement.setString(1, id);
            return collectResults(id, statement).stream().findFirst();
        })));
    }

    @Override protected @NonNull CompletableFuture< Collection<Rank>> findAllUncached(@NonNull Iterable<String> id) throws NullPointerException
    {
        String placeHolders = String.join(", ", Collections.nCopies(computeIterableSize(id), "?"));
        String sql = "SELECT id, priority FROM ksvp_rank WHERE id IN (" + placeHolders + ");";
        return CompletableFuture.supplyAsync(() -> query(sql, (statement ->
        {
            int index = 1;
            for (String current : id)
            {
                statement.setString(index++, current);
            }

            Collection<Rank> rankCollection = new HashSet<>();
            try (ResultSet resultSet = statement.executeQuery())
            {
                while (resultSet.next())
                {
                    rankCollection.add(toEntity(resultSet));
                }
            }
            return Collections.unmodifiableCollection(rankCollection);
        })));
    }

    @Override public @NonNull CompletableFuture< Collection<Rank>> findAll()
    {
        return CompletableFuture.supplyAsync(() -> query("SELECT id, priority FROM ksvp_rank;", (statement ->
        {
            Collection<Rank> rankCollection = new HashSet<>();
            try (ResultSet resultSet = statement.executeQuery())
            {
                while(resultSet.next())
                {
                    rankCollection.add(toEntity(resultSet));
                }
            }
            return Collections.unmodifiableCollection(rankCollection);
        })));
    }

    @Override public @NonNull CompletableFuture<Boolean> has(@NonNull String id) throws NullPointerException
    {
        return CompletableFuture.supplyAsync(() -> query("SELECT id FROM ksvp_rank WHERE id = ?;", (statement ->
        {
            statement.setString(1, id);
            return hasResult(statement);
        })));
    }

    @Override public @NonNull CompletableFuture<Void> saveAll(@NonNull Iterable<Rank> id) throws NullPointerException
    {
        Objects.requireNonNull(id, "The iterable of ranks cannot be null.");

        String sql = "INSERT INTO ksvp_rank (id, priority) VALUES (?, ?) ON DUPLICATE KEY UPDATE priority = ?;";
        return CompletableFuture.supplyAsync(() -> query(sql, (statement ->
        {
            for (Rank rank : id)
            {
                batch(statement, rank);
                statement.addBatch();
            }
            statement.executeBatch();
            return null;
        })));
    }

    @Override
    protected @NonNull Rank toCachedEntity(@NonNull ResultSet resultSet) throws SQLException, NullPointerException
    {
        return toEntity(resultSet.getString("id"), resultSet);
    }

    @Override
    protected @NonNull Rank toCachedEntity(@NonNull String id, @NonNull ResultSet resultSet) throws SQLException, NullPointerException
    {
        Objects.requireNonNull(id, "The id cannot be null.");
        Objects.requireNonNull(resultSet, "The result set cannot be null.");

        return new InternalRank(id, resultSet.getInt("priority"));
    }

    /**
     * Adds a {@link Rank} entity as a batch operation to the given {@link PreparedStatement}.
     * The method populates the prepared statement parameters with the rank's properties, such as ID, priority,
     * prefix, and suffix.
     *
     * @param statement the prepared statement to which the rank data will be added as a batch must not be null
     * @param rank      the rank entity whose data will be added to the prepared statement, must not be null
     * @throws SQLException         if an error occurs while setting parameters or adding the batch
     * @throws NullPointerException if the provided statement or rank is null
     */
    private void batch(@NonNull PreparedStatement statement, @NonNull Rank rank) throws SQLException, NullPointerException
    {
        Objects.requireNonNull(statement, "The prepared statement cannot be null.");
        Objects.requireNonNull(rank, "The rank cannot be null.");

        statement.setString(1, rank.id());
        setDual(statement, 2, 3, Types.INTEGER, rank.priority());

        overrideSignature(rank);
    }
}
