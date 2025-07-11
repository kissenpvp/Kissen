package net.kissenpvp.network.actor.rank.repository;

import net.kissenpvp.api.database.Repository;
import net.kissenpvp.api.network.actor.rank.RankSubscription;
import net.kissenpvp.database.InternalRepository;
import net.kissenpvp.network.actor.rank.InternalRankSubscription;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * InternalRankSubscriptionRepository is a repository implementation responsible for handling
 * operations related to rank subscriptions. This class extends {@code InternalRepository}
 * for database-related functionalities and implements {@code SubscriptionRepository}
 * for managing rank-specific subscriptions.
 * <p>
 * The repository interacts with a database table that stores data related to rank subscriptions,
 * and it provides methods for persisting and retrieving {@code RankSubscription} entities.
 * It includes functionality to map database records to {@code RankSubscription} objects
 * and vice versa.
 *
 * @author Bebdor augustus irilieres cesarius, Ivo Quiring
 */
public class InternalRankSubscriptionRepository extends InternalRepository<String, RankSubscription> implements Repository<String, RankSubscription>
{
    /**
     * Constructs an {@code InternalRankSubscriptionRepository} instance with the specified database connection.
     * This repository is responsible for managing the persistence and retrieval of rank subscription-related data
     * from the underlying database. It initializes the repository with predefined configurations for table name
     * and query.
     *
     * @param connection the database connection to be used for executing queries, must not be null
     * @throws NullPointerException if the provided connection is null
     */
    public InternalRankSubscriptionRepository(@NotNull Connection connection) throws NullPointerException
    {
        super("ksvp_rank_subscription", connection, "SELECT player_id, rank_id FROM %s WHERE id = ?");
    }

    @Override
    protected @NotNull RankSubscription toEntity(@NotNull String id, @NotNull ResultSet resultSet) throws SQLException, NullPointerException
    {
        Objects.requireNonNull(id, "The rank subscription ID cannot be null.");
        Objects.requireNonNull(resultSet, "The result set cannot be null.");

        UUID playerId = UUID.fromString(resultSet.getString("player_id"));
        return new InternalRankSubscription(id, resultSet.getString("rank_id"), playerId);
    }

    @Override
    protected @NotNull RankSubscription toEntity(@NotNull ResultSet resultSet) throws SQLException, NullPointerException
    {
        Objects.requireNonNull(resultSet, "The result set cannot be null.");
        return toEntity(resultSet.getString("id"), resultSet);
    }

    @Override
    public @NotNull CompletableFuture<Void> saveAll(@NotNull Iterable<RankSubscription> id) throws NullPointerException
    {
        Objects.requireNonNull(id, "The rank subscriptions cannot be null.");

        String sql = "INSERT INTO %s (id, rank_id, player_id) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE rank_id = ?, player_id = ?;";

        return CompletableFuture.supplyAsync(() -> query(sql, (statement ->
        {
            for (RankSubscription subscription : id)
            {
                statement.setString(1, subscription.id());

                setDual(statement, 2, 4, Types.VARCHAR, subscription.parentId());
                setDual(statement, 3, 5, Types.VARCHAR, String.valueOf(subscription.player().id()));

                overrideSignature(subscription);
                statement.addBatch();
            }
            statement.executeBatch();
            return null;
        })));
    }
}
