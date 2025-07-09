package net.kissenpvp.network.actor.rank.repository;

import net.kissenpvp.api.network.actor.rank.Rank;
import net.kissenpvp.api.network.actor.rank.RankRepository;
import net.kissenpvp.database.InternalRepository;
import net.kissenpvp.network.actor.rank.InternalRank;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.Objects;
import java.util.Optional;
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
public class InternalRankRepository extends InternalRepository<String, Rank> implements RankRepository
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
        super("ksvp_rank_data", connection, "SELECT priority, prefix, suffix FROM %s WHERE id = ?;");
    }

    @Override
    protected @NotNull Rank toEntity(@NotNull ResultSet resultSet) throws SQLException, NullPointerException
    {
        return toEntity(resultSet.getString("id"), resultSet);
    }

    @Override
    protected @NotNull Rank toEntity(@NotNull String id, @NotNull ResultSet resultSet) throws SQLException, NullPointerException
    {
        JSONComponentSerializer serializer = JSONComponentSerializer.json();

        Component prefix = null;
        String prefixString = resultSet.getString("prefix");
        if(!resultSet.wasNull())
        {
            prefix = serializer.deserialize(prefixString);
        }

        Component suffix = null;
        String suffixString = resultSet.getString("suffix");
        if(!resultSet.wasNull())
        {
            suffix = serializer.deserialize(suffixString);
        }

        return new InternalRank(id, resultSet.getInt("priority"), prefix, suffix);
    }

    @Override public @NotNull CompletableFuture<Void> saveAll(@NotNull Iterable<Rank> id) throws NullPointerException
    {
        String sql = "INSERT INTO %s (id, priority, prefix, suffix) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE priority = ?, prefix = ?, suffix = ?";

        return CompletableFuture.supplyAsync(() -> query(sql, (statement ->
        {
            for(Rank rank : id)
            {
                addBatch(statement, rank);
            }
            statement.executeBatch();
            return null;
        })));
    }

    /**
     * Adds a {@link Rank} entity as a batch operation to the given {@link PreparedStatement}.
     * The method populates the prepared statement parameters with the rank's properties, such as ID, priority, prefix, and suffix.
     *
     * @param statement the prepared statement to which the rank data will be added as a batch must not be null
     * @param rank the rank entity whose data will be added to the prepared statement, must not be null
     * @throws SQLException if an error occurs while setting parameters or adding the batch
     * @throws NullPointerException if the provided statement or rank is null
     */
    private void addBatch(@NotNull PreparedStatement statement, @NotNull Rank rank) throws SQLException, NullPointerException
    {
        Objects.requireNonNull(statement, "The prepared statement cannot be null.");
        Objects.requireNonNull(rank, "The rank cannot be null.");

        Optional<String> prefix = rank.prefix().map(JSONComponentSerializer.json()::serialize);
        Optional<String> suffix = rank.suffix().map(JSONComponentSerializer.json()::serialize);

        statement.setString(1, rank.id());
        setDual(statement, 2, 5, Types.INTEGER, rank.priority());
        setDual(statement, 3, 6, Types.VARCHAR, prefix.orElse(null));
        setDual(statement, 4, 7, Types.VARCHAR, suffix.orElse(null));

        overrideSignature(rank);
        statement.addBatch();
    }
}
