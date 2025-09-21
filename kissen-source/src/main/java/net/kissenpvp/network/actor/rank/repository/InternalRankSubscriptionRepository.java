package net.kissenpvp.network.actor.rank.repository;

import net.kissenpvp.api.database.Repository;
import net.kissenpvp.api.network.actor.rank.RankSubscription;
import net.kissenpvp.api.temporal.WritableTemporalObject;
import net.kissenpvp.database.InternalRepository;
import net.kissenpvp.network.actor.rank.InternalRankSubscription;
import net.kissenpvp.temporal.InternalWritableTemporalObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
        super(
                "ksvp_rank_subscription",
                connection,
                "SELECT player_id, rank_id, start_time, expiry, expected_expiry FROM ksvp_rank_subscription WHERE id = ?;",
                "SELECT * FROM ksvp_rank_subscription;",
                "SELECT * FROM ksvp_rank_subscription WHERE id IN (%s);"
        );
    }

    @Override
    public @NotNull RankSubscription toEntity(@NotNull String id, @NotNull ResultSet resultSet) throws SQLException, NullPointerException
    {
        Objects.requireNonNull(id, "The rank subscription ID cannot be null.");
        Objects.requireNonNull(resultSet, "The result set cannot be null.");

        UUID playerId = UUID.fromString(resultSet.getString("player_id"));

        Instant start = resultSet.getDate("start_time").toInstant(); // expected to be not null
        Instant expiry = convertSafely(date -> Instant.ofEpochMilli(date.getTime()), resultSet.getDate("expiry"));
        Instant expectedExpiry = convertSafely(date -> Instant.ofEpochMilli(date.getTime()), resultSet.getDate("expected_expiry"));
        WritableTemporalObject temporal = new InternalWritableTemporalObject(start, expiry, expectedExpiry);

        return new InternalRankSubscription(id, resultSet.getString("rank_id"), playerId, temporal);
    }

    @Override
    public @NotNull RankSubscription toEntity(@NotNull ResultSet resultSet) throws SQLException, NullPointerException
    {
        Objects.requireNonNull(resultSet, "The result set cannot be null.");
        return toEntity(resultSet.getString("id"), resultSet);
    }

    @Override
    public @NotNull CompletableFuture<Void> saveAll(@NotNull Iterable<RankSubscription> id) throws NullPointerException
    {
        Objects.requireNonNull(id, "The rank subscriptions cannot be null.");

        String sql = "INSERT INTO ksvp_rank_subscription (id, rank_id, player_id, start_time, expiry, expected_expiry) VALUES (?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE rank_id = ?, player_id = ?, expiry = ?;";

        return CompletableFuture.supplyAsync(() -> query(sql, (statement ->
        {
            for (RankSubscription subscription : id)
            {
                statement.setString(1, subscription.id());

                LocalDateTime start = toDateTime(subscription.temporal().start());
                LocalDateTime expiry = subscription.temporal().expiry().map(InternalRankSubscriptionRepository::toDateTime).orElse(null);

                setDual(statement, 2, 7, Types.VARCHAR, subscription.parentId());
                setDual(statement, 3, 8, Types.VARCHAR, String.valueOf(subscription.player().id()));

                // we cannot use setTimestamp because the java.sql.TimeStamp class is very old
                // https://stackoverflow.com/a/73967623
                statement.setObject(4, start, Types.TIMESTAMP);
                setDual(statement, 5, 9, Types.BIGINT, expiry);
                expectedExpiry(statement, expiry);

                overrideSignature(subscription);
                statement.addBatch();
            }
            statement.executeBatch();
            return null;
        })));
    }

    private static @NotNull LocalDateTime toDateTime(@NotNull Instant instant)
    {
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    /**
     * Sets the expected expiry value in the given {@link PreparedStatement}.
     * <p>
     * If the expiry value is not null, it sets the value at index 6; otherwise,
     * it sets the value at index 6 to {@code NULL} with the appropriate SQL type.
     * <p>
     * This just acts as a helper function for copying the value of the actual expiry when the object is being created.
     *
     * @param statement The {@link PreparedStatement} where the expiry value will be set. Must not be null.
     * @param expiry    The expiry value to be set in the {@link PreparedStatement}. Can be null.
     * @throws SQLException         If an error occurs while interacting with the {@link PreparedStatement}.
     * @throws NullPointerException If the provided {@link PreparedStatement} is null.
     */
    private void expectedExpiry(@NotNull PreparedStatement statement, @Nullable LocalDateTime expiry) throws SQLException, NullPointerException {
        Objects.requireNonNull(statement, "The prepared statement cannot be null.");

        if (Objects.nonNull(expiry)) {
            // we cannot use setTimestamp because the java.sql.TimeStamp class is very old
            // https://stackoverflow.com/a/73967623
            statement.setObject(6, expiry, Types.TIMESTAMP);
            return;
        }

        statement.setNull(6, Types.DATE);
    }
}
