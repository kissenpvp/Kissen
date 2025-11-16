package net.kissenpvp.network.actor.rank.repository;

import com.google.common.base.Preconditions;
import net.kissenpvp.api.network.actor.PlayerClient;
import net.kissenpvp.api.network.actor.rank.RankSubscription;
import net.kissenpvp.api.network.actor.rank.RankSubscriptionRepository;
import net.kissenpvp.api.temporal.WritableTemporalObject;
import net.kissenpvp.database.InternalRepository;
import net.kissenpvp.network.actor.rank.InternalRankSubscription;
import net.kissenpvp.temporal.InternalWritableTemporalObject;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;


import javax.sql.DataSource;
import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
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
public class InternalRankSubscriptionRepository extends InternalRepository<String, RankSubscription> implements RankSubscriptionRepository
{
    public InternalRankSubscriptionRepository(@NonNull DataSource dataSource) throws NullPointerException
    {
        super(dataSource);
    }

    private static @NonNull LocalDateTime toDateTime(@NonNull Instant instant)
    {
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    @Override public @NonNull CompletableFuture<@NonNull Optional<RankSubscription>> find(@NonNull String id)
    {
        String sql = "SELECT rank_id, player_id, start_time, expiry, expected_expiry FROM ksvp_rank_subscription WHERE id = ?;";
        return CompletableFuture.supplyAsync(() -> assumeNotNull(query(sql, (statement ->
        {
            statement.setString(1, id);
            return collectResults(id, statement).stream().findFirst();
        }))));
    }

    @Override public @NonNull CompletableFuture< Collection<RankSubscription>> findAll(@NonNull Iterable<String> id)
    {
        String placeHolders = String.join(", ", Collections.nCopies(computeIterableSize(id), "?"));
        String sql = "SELECT id, rank_id, player_id, start_time, expiry, expected_expiry FROM ksvp_rank_subscription WHERE id IN (" + placeHolders + ")";
        return CompletableFuture.supplyAsync(() -> query(sql, statement ->
        {
            int index = 1;
            for (String current : id)
            {
                statement.setString(index++, current);
            }

            return collectResults(statement);
        }));
    }

    @Override public @NonNull CompletableFuture< Collection<RankSubscription>> findAll()
    {
        String sql = "SELECT id, rank_id, player_id, start_time, expiry, expected_expiry FROM ksvp_rank_subscription";
        return CompletableFuture.supplyAsync(() -> query(sql, this::collectResults));
    }

    @Override public @NonNull CompletableFuture<Boolean> has(@NonNull String id)
    {
        return CompletableFuture.supplyAsync(() -> query("SELECT id FROM ksvp_rank_subscription WHERE id = ?;", (statement ->
        {
            statement.setString(1, id);
            return hasResult(statement);
        })));
    }

    @Override public @NonNull RankSubscription toEntity(@NonNull String id, @NonNull ResultSet resultSet) throws SQLException, NullPointerException
    {
        Preconditions.checkNotNull(id, "The rank subscription ID cannot be null.");
        Preconditions.checkNotNull(resultSet, "The result set cannot be null.");

        UUID playerId = UUID.fromString(resultSet.getString("player_id"));

        Instant start = resultSet.getDate("start_time").toInstant(); // expected to be not null
        Instant expiry = convertSafely(date -> Instant.ofEpochMilli(date.getTime()), resultSet.getDate("expiry"));
        Instant expectedExpiry = convertSafely(date -> Instant.ofEpochMilli(date.getTime()), resultSet.getDate("expected_expiry"));
        WritableTemporalObject temporal = new InternalWritableTemporalObject(start, expiry, expectedExpiry);

        return new InternalRankSubscription(id, resultSet.getString("rank_id"), playerId, temporal);
    }

    @Override public @NonNull RankSubscription toEntity(@NonNull ResultSet resultSet) throws SQLException, NullPointerException
    {
        Preconditions.checkNotNull(resultSet, "The result set cannot be null.");
        return toEntity(resultSet.getString("id"), resultSet);
    }

    @Override public @NonNull CompletableFuture<Void> saveAll(@NonNull Iterable<RankSubscription> id)
    {
        Preconditions.checkNotNull(id, "The rank subscriptions cannot be null.");

        String sql = "INSERT INTO ksvp_rank_subscription (id, rank_id, player_id, start_time, expiry,  expected_expiry) VALUES (?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE rank_id = ?, player_id = ?, expiry = ?;";

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

                statement.addBatch();
            }
            statement.executeBatch();
            return null;
        })));
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
    private void expectedExpiry(@NonNull PreparedStatement statement, @Nullable LocalDateTime expiry) throws SQLException, NullPointerException
    {
        Preconditions.checkNotNull(statement, "The prepared statement cannot be null.");

        if (Objects.nonNull(expiry))
        {
            // we cannot use setTimestamp because the java.sql.TimeStamp class is very old
            // https://stackoverflow.com/a/73967623
            statement.setObject(6, expiry, Types.TIMESTAMP);
            return;
        }

        statement.setNull(6, Types.TIMESTAMP);
    }

    @Override public @NonNull CompletableFuture<@NonNull Optional<RankSubscription>> findActive(@NonNull PlayerClient player)
    {
        String sql = "SELECT id, rank_id, player_id, start_time, expiry, expected_expiry FROM ksvp_rank_subscription WHERE player_id = ? AND (expiry IS NULL OR expiry > NOW()) ORDER BY start_time DESC LIMIT 1;";

        return CompletableFuture.supplyAsync(() -> assumeNotNull(query(sql, (statement ->
        {
            statement.setString(1, String.valueOf(player.id()));
            try(ResultSet resultSet = statement.executeQuery())
            {
                if(!resultSet.next())
                {
                    return Optional.empty();
                }

                return Optional.of(toEntity(resultSet));
            }
        }))));
    }

    @Override public @NonNull CompletableFuture<@NonNull List<RankSubscription>> findHistory(@NonNull PlayerClient player)
    {
        String sql = "SELECT id, rank_id, player_id, start_time, expiry, expected_expiry FROM ksvp_rank_subscription WHERE player_id = ? ORDER BY start_time DESC;";
        return CompletableFuture.supplyAsync(() -> assumeNotNull(query(sql, (statement ->
        {
            statement.setString(1, String.valueOf(player.id()));

            List<RankSubscription> history = new ArrayList<>();
            try(ResultSet resultSet = statement.executeQuery())
            {
                while(resultSet.next())
                {
                    history.add(toEntity(resultSet));
                }
            }
            return Collections.unmodifiableList(history);
        }))));
    }
}
