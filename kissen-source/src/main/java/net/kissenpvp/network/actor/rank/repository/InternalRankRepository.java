package net.kissenpvp.network.actor.rank.repository;

import net.kissenpvp.api.network.actor.rank.Rank;
import net.kissenpvp.database.InternalCachedRepository;
import net.kissenpvp.database.InternalRepository;
import net.kissenpvp.network.actor.rank.InternalRank;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.Objects;
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
    /**
     * Constructs an {@code InternalRankRepository} instance with the specified database connection.
     * This repository is responsible for managing the persistence and retrieval of rank-related data
     * from the underlying database. It initializes the repository with predefined configurations
     * for table name and query.
     *
     * @param connection the database connection to be used for executing queries, must not be null
     * @throws NullPointerException if the provided connection is null
     */
    public InternalRankRepository(@NotNull Connection connection) throws NullPointerException
    {
        super(
                "ksvp_rank",
                connection,
                "SELECT priority FROM ksvp_rank WHERE id = ?;",
                "SELECT * FROM ksvp_rank;",
                "SELECT * FROM ksvp_rank WHERE id IN (%s);"
        );
    }

    @Override
    protected @NotNull Rank toCachedEntity(@NotNull ResultSet resultSet) throws SQLException, NullPointerException
    {
        Objects.requireNonNull(resultSet, "The result set cannot be null.");
        return toEntity(resultSet.getString("id"), resultSet);
    }

    @Override
    protected @NotNull Rank toCachedEntity(@NotNull String id, @NotNull ResultSet resultSet) throws SQLException,
            NullPointerException
    {
        Objects.requireNonNull(id, "The id cannot be null.");
        Objects.requireNonNull(resultSet, "The result set cannot be null.");

        return new InternalRank(id, resultSet.getInt("priority"));
    }

    @Override public @NotNull CompletableFuture<Void> saveAll(@NotNull Iterable<Rank> id) throws NullPointerException
    {
        Objects.requireNonNull(id, "The iterable of ranks cannot be null.");

        String sql = "INSERT INTO ksvp_rank (id, priority) VALUES (?, ?) ON DUPLICATE KEY UPDATE priority = ?;";

        return CompletableFuture.supplyAsync(() -> query(sql, (statement ->
        {
            for (Rank rank : id)
            {
                addBatch(statement, rank);
            }
            statement.executeBatch();
            return null;
        })));
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
    private void addBatch(@NotNull PreparedStatement statement, @NotNull Rank rank) throws SQLException,
            NullPointerException
    {
        Objects.requireNonNull(statement, "The prepared statement cannot be null.");
        Objects.requireNonNull(rank, "The rank cannot be null.");

        statement.setString(1, rank.id());
        setDual(statement, 2, 3, Types.INTEGER, rank.priority());

        overrideSignature(rank);
        statement.addBatch();
    }
}
