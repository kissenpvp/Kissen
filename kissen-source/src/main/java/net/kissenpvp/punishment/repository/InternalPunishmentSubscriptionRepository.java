package net.kissenpvp.punishment.repository;

import com.google.common.base.Preconditions;
import net.kissenpvp.api.database.QueryExecutor;
import net.kissenpvp.api.network.actor.PlayerClient;
import net.kissenpvp.api.punishment.Punishment;
import net.kissenpvp.api.punishment.PunishmentSubscription;
import net.kissenpvp.api.punishment.PunishmentSubscriptionRepository;
import net.kissenpvp.api.temporal.WritableTemporalObject;
import net.kissenpvp.base.KissenCore;
import net.kissenpvp.database.mariadb.InternalCachedRepository;
import net.kissenpvp.database.mariadb.InternalRepository;
import net.kissenpvp.network.actor.InternalPlayerRepository;
import net.kissenpvp.punishment.InternalPunishmentSubscription;
import net.kissenpvp.temporal.InternalWritableTemporalObject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
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
 * This class is responsible for managing the storage and retrieval of {@link PunishmentSubscription} entities
 * in a database, using a caching mechanism for improved performance. It provides methods for converting
 * database result sets to entities, saving batch data, and handling specific properties such as message and
 * timespan for subscriptions.
 *
 * @author Ivo Quiring
 * @see InternalCachedRepository
 * @see PunishmentSubscription
 * @see Punishment
 */
public class InternalPunishmentSubscriptionRepository extends InternalRepository<String, PunishmentSubscription> implements PunishmentSubscriptionRepository
{
    public InternalPunishmentSubscriptionRepository(@NonNull DataSource dataSource) throws NullPointerException
    {
        super(dataSource);
    }

    @Override
    public @NonNull InternalPunishmentSubscription toEntity(@NonNull String id, @NonNull ResultSet resultSet) throws SQLException, NullPointerException
    {

        Preconditions.checkNotNull(id, "The id cannot be null.");
        Preconditions.checkNotNull(resultSet, "The result set cannot be null.");

        Component message = null;
        String messageString = resultSet.getString("message");
        if (!resultSet.wasNull())
        {
            message = GsonComponentSerializer.gson().deserialize(messageString);
        }
        int parentId = resultSet.getInt("parent_id");
        UUID linkId = UUID.fromString(resultSet.getString("link_id"));
        UUID operatorId = convertSafely(UUID::fromString, resultSet.getString("operator_id"));

        Instant start = Instant.ofEpochMilli(resultSet.getDate("start_time").getTime()); // expected to be not null

        Instant expiry = convertSafely(date -> Instant.ofEpochMilli(date.getTime()), resultSet.getDate("expiry"));
        Instant expectedExpiry = convertSafely(date -> Instant.ofEpochMilli(date.getTime()), resultSet.getDate(
                "expected_expiry"));

        WritableTemporalObject temporal = new InternalWritableTemporalObject(start, expiry, expectedExpiry);

        return new InternalPunishmentSubscription(id, parentId, linkId, operatorId, temporal, message);
    }

    @Override
    public @NonNull InternalPunishmentSubscription toEntity(@NonNull ResultSet resultSet) throws SQLException,
            NullPointerException
    {
        Preconditions.checkNotNull(resultSet, "The result set cannot be null.");

        return toEntity(resultSet.getString("id"), resultSet);
    }

    @Override public @NonNull CompletableFuture<@NonNull Optional<PunishmentSubscription>> find(@NonNull String id)
    {
        String sql = "SELECT link_id, parent_id, parent_signature, operator_id, start_time, expiry, expected_expiry, message FROM ksvp_punishment_subscription WHERE id = ?;";
        return CompletableFuture.supplyAsync(() -> assumeNotNull(query(sql, statement ->
        {
            statement.setString(1, id);
            return collectResults(id, statement).stream().findFirst();
        })));
    }

    @Override public @NonNull CompletableFuture<Collection<PunishmentSubscription>> findAll(@NonNull Iterable<String> id)
    {
        String placeHolders = String.join(", ", Collections.nCopies(computeIterableSize(id), "?"));
        String sql = "SELECT id, link_id, parent_id, parent_signature, operator_id, start_time, expiry, expected_expiry, message FROM ksvp_punishment_subscription WHERE id IN (" + placeHolders + ")";
        return CompletableFuture.supplyAsync(() -> assumeNotNull(query(sql, statement ->
        {
            int index = 1;
            for (String current : id)
            {
                statement.setString(index++, current);
            }

            return collectResults(statement);
        })));
    }

    @Override public @NonNull CompletableFuture<Collection<PunishmentSubscription>> findAll()
    {
        String sql = "SELECT id, link_id, parent_id, parent_signature, operator_id, start_time, expiry, expected_expiry, message FROM ksvp_punishment_subscription;";
        return CompletableFuture.supplyAsync(() -> assumeNotNull(query(sql, this::collectResults)));
    }

    @Override public @NonNull CompletableFuture<Boolean> has(@NonNull String id)
    {
        String sql = "SELECT id FROM ksvp_punishment_subscription WHERE id = ?;";
        return CompletableFuture.supplyAsync(() -> query(sql, statement ->
        {
            statement.setString(1, id);
            return hasResult(statement);
        }));
    }

    @Override
    public @NonNull CompletableFuture<Void> saveAll(@NonNull Iterable<PunishmentSubscription> id)
    {
        Preconditions.checkNotNull(id, "The iterable of subscriptions cannot be null.");

        String sql = "INSERT INTO ksvp_punishment_subscription (id, link_id, parent_id,  operator_id, start_time, expiry, expected_expiry, message) VALUES (?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE link_id = ?, expiry = ?, message = ?; ";
        return CompletableFuture.supplyAsync(() -> query(sql, (statement ->
        {
            for (PunishmentSubscription subscription : id)
            {
                addBatch(statement, subscription);
            }

            statement.executeBatch();
            return null;
        })));
    }

    /**
     * Adds a {@link PunishmentSubscription} to the provided {@link PreparedStatement} as part of a batch operation.
     * This method prepares the statement with the necessary values from the given subscription, including optional
     * fields,
     * and invokes helper methods to set both primary and duplicate indices.
     *
     * @param statement    The {@link PreparedStatement} to which the subscription's data will be added. Must not be
     *                     null.
     * @param subscription The {@link PunishmentSubscription} containing the data to be added to the batch. Must not
     *                     be null.
     * @throws SQLException         If an issue occurs while setting values in the {@link PreparedStatement}.
     * @throws NullPointerException If the {@link PreparedStatement} or {@link PunishmentSubscription} is null.
     */
    private void addBatch(@NonNull PreparedStatement statement, @NonNull PunishmentSubscription subscription) throws SQLException, NullPointerException
    {
        Preconditions.checkNotNull(statement, "The prepared statement cannot be null.");
        Preconditions.checkNotNull(subscription, "The subscription cannot be null.");

        statement.setString(1, subscription.id());

        LocalDateTime date = toDateTime(subscription.temporal().start());
        LocalDateTime expiry = subscription.temporal().expiry().map(InternalPunishmentSubscriptionRepository::toDateTime).orElse(null);
        Optional<String> message = subscription.message().map(JSONComponentSerializer.json()::serialize);

        setDual(statement, 2, 9, Types.VARCHAR, String.valueOf(subscription.linkId()));
        statement.setInt(3, subscription.parentId());

        operator(statement, subscription); // populates slot 4

        // we cannot use setTimestamp because the java.sql.TimeStamp class is very old
        // https://stackoverflow.com/a/73967623
        statement.setObject(5, date, Types.TIMESTAMP);

        setDual(statement, 6, 10, Types.TIMESTAMP, expiry);
        expectedExpiry(statement, expiry); // populates slot 7
        setDual(statement, 8, 11, Types.VARCHAR, message.orElse(null));

        statement.addBatch();
    }

    /**
     * Sets the expected expiry value in the given {@link PreparedStatement}.
     * <p>
     * If the expiry value is not null, it sets the value at index 7; otherwise,
     * it sets the value at index 7 to {@code NULL} with the appropriate SQL type.
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
            statement.setObject(7, expiry, Types.TIMESTAMP);
            return;
        }

        statement.setNull(8, Types.TIMESTAMP);
    }

    @Override
    public @NonNull CompletableFuture<@NonNull Collection<PlayerClient>> findTargets(@NonNull UUID linkId)
    {
        String sql = "SELECT id, username, first_login, last_login, time_played, locale FROM ksvp_player WHERE " +
                "link_id = ?;";
        InternalRepository<UUID, PlayerClient> playerRepository =
                (InternalPlayerRepository) KissenCore.getInstance().playerRepository();
        return CompletableFuture.supplyAsync(() -> assumeNotNull(query(sql, (statement ->
        {
            statement.setString(1, String.valueOf(linkId));

            try (ResultSet resultSet = statement.executeQuery())
            {
                Set<PlayerClient> clients = new HashSet<>();
                while (resultSet.next())
                {
                    clients.add(playerRepository.toEntity(UUID.fromString(resultSet.getString("id")), resultSet));
                }
                return Collections.unmodifiableSet(clients);
            }
        }))));
    }

    @Override
    public @NonNull CompletableFuture<@NonNull Collection<UUID>> findTargetIds(@NonNull UUID linkId)
    {
        String sql = "SELECT id FROM ksvp_player WHERE link_id = ?;";
        return CompletableFuture.supplyAsync(() -> assumeNotNull(query(sql, (statement ->
        {

            statement.setString(1, String.valueOf(linkId));

            try (ResultSet resultSet = statement.executeQuery())
            {
                Set<UUID> uuidSet = new HashSet<>();
                while (resultSet.next())
                {
                    uuidSet.add(UUID.fromString(resultSet.getString("id")));
                }
                return Collections.unmodifiableSet(uuidSet);
            }
        }))));
    }

    @Override
    public @NonNull CompletableFuture<@NonNull Collection<PunishmentSubscription>> findSubscriptions(@NonNull UUID linkId)
    {
        String sql = "SELECT * FROM ksvp_punishment_subscription WHERE link_id = ?;";
        return CompletableFuture.supplyAsync(() -> assumeNotNull(query(sql, retrieveSubscriptions(linkId))));
    }

    @Override
    public @NonNull CompletableFuture<@NonNull Collection<PunishmentSubscription>> findSubscriptionsByUserId(
            @NonNull UUID userId
    )
    {
        String sql = "SELECT ps.* FROM ksvp_punishment_subscription ps JOIN ksvp_player p ON ps.link_id = p.link_id " +
                "WHERE p.id = ?;";
        return CompletableFuture.supplyAsync(() -> assumeNotNull(query(sql, retrieveSubscriptions(userId))));
    }

    private @NonNull QueryExecutor<Collection<PunishmentSubscription>> retrieveSubscriptions(UUID uuid)
    {
        return (statement ->
        {
            statement.setString(1, String.valueOf(uuid));

            Collection<PunishmentSubscription> subscriptions = new HashSet<>();
            try (ResultSet resultSet = statement.executeQuery())
            {
                while (resultSet.next())
                {
                    subscriptions.add(toEntity(resultSet));
                }
                return Collections.unmodifiableCollection(subscriptions);
            }
        });
    }

    private static @NonNull LocalDateTime toDateTime(@NonNull Instant instant)
    {
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    private static void operator(@NonNull PreparedStatement statement, @NonNull PunishmentSubscription subscription) throws SQLException
    {
        UUID operator = ((InternalPunishmentSubscription) subscription).rawOperator();
        if (Objects.nonNull(operator))
        {
            statement.setString(4, String.valueOf(operator));
            return;
        }

        statement.setNull(5, Types.VARCHAR);
    }
}
