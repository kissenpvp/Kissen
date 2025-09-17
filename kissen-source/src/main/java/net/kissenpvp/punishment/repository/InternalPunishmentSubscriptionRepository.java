package net.kissenpvp.punishment.repository;

import net.kissenpvp.api.network.actor.PlayerClient;
import net.kissenpvp.api.punishment.Punishment;
import net.kissenpvp.api.punishment.PunishmentSubscription;
import net.kissenpvp.api.punishment.PunishmentSubscriptionRepository;
import net.kissenpvp.api.temporal.WritableTemporalObject;
import net.kissenpvp.base.KissenCore;
import net.kissenpvp.database.InternalCachedRepository;
import net.kissenpvp.database.InternalRepository;
import net.kissenpvp.network.actor.InternalPlayerRepository;
import net.kissenpvp.punishment.InternalPunishmentSubscription;
import net.kissenpvp.temporal.InternalWritableTemporalObject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.sql.*;
import java.sql.Date;
import java.time.Instant;
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
    /**
     * Constructs a new instance of PunishmentSubscriptionRepository.
     * This initializes the repository with a predefined table name, connection,
     * and query string to fetch punishment subscription data by ID.
     *
     * @param connection The database connection to be used by this repository. Must not be null.
     * @throws NullPointerException If the provided connection is null.
     */
    public InternalPunishmentSubscriptionRepository(@NotNull Connection connection) throws NullPointerException
    {
        super("ksvp_punishment_subscription", connection,
                "SELECT link_id, parent_id, operator_id, start_time, expiry, expected_expiry, message FROM ksvp_punishment_subscription WHERE id = ?;",
                "SELECT id, link_id, parent_id, operator_id, start_time, expiry, expected_expiry, message FROM ksvp_punishment_subscription;",
                "SELECT id, link_id, parent_id, operator_id, start_time, expiry, expected_expiry, message FROM ksvp_punishment_subscription WHERE id IN (?);");
    }

    @Override
    public @NotNull InternalPunishmentSubscription toEntity(@NotNull String id, @NotNull ResultSet resultSet) throws SQLException, NullPointerException
    {
        Objects.requireNonNull(id, "The id cannot be null.");
        Objects.requireNonNull(resultSet, "The result set cannot be null.");

        Component message = null; String messageString = resultSet.getString("message"); if (!resultSet.wasNull())
    {
        message = GsonComponentSerializer.gson().deserialize(messageString);
    }
        int parentId = resultSet.getInt("parent_id");
        UUID linkId = UUID.fromString(resultSet.getString("link_id"));
        UUID operatorId = convertSafely(UUID::fromString, resultSet.getString("operator_id"));

        Instant start = Instant.ofEpochMilli(resultSet.getDate("start_time").getTime()); // expected to be not null

        Instant expiry = convertSafely(date -> Instant.ofEpochMilli(date.getTime()), resultSet.getDate("expiry"));
        Instant expectedExpiry = convertSafely(date -> Instant.ofEpochMilli(date.getTime()), resultSet.getDate("expected_expiry"));

        WritableTemporalObject temporal = new InternalWritableTemporalObject(start, expiry, expectedExpiry);

        return new InternalPunishmentSubscription(id, parentId, linkId, operatorId, temporal, message);
    }

    @Override
    public @NotNull InternalPunishmentSubscription toEntity(@NotNull ResultSet resultSet) throws SQLException, NullPointerException
    {
        Objects.requireNonNull(resultSet, "The result set cannot be null.");

        return toEntity(resultSet.getString("id"), resultSet);
    }

    @Override
    public @NotNull CompletableFuture<Void> saveAll(@NotNull Iterable<PunishmentSubscription> id) throws NullPointerException
    {
        Objects.requireNonNull(id, "The iterable of subscriptions cannot be null.");

        String sql = "INSERT INTO ksvp_punishment_subscription (id, link_id, parent_id, parent_signature, operator_id, start_time, expiry, expected_expiry, message) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE link_id = ?, parent_signature = ?, expiry = ?, message = ?; ";
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
     * This method prepares the statement with the necessary values from the given subscription, including optional fields,
     * and invokes helper methods to set both primary and duplicate indices.
     *
     * @param statement    The {@link PreparedStatement} to which the subscription's data will be added. Must not be null.
     * @param subscription The {@link PunishmentSubscription} containing the data to be added to the batch. Must not be null.
     * @throws SQLException         If an issue occurs while setting values in the {@link PreparedStatement}.
     * @throws NullPointerException If the {@link PreparedStatement} or {@link PunishmentSubscription} is null.
     */
    private void addBatch(@NotNull PreparedStatement statement, @NotNull PunishmentSubscription subscription) throws SQLException, NullPointerException
    {
        Objects.requireNonNull(statement, "The prepared statement cannot be null.");
        Objects.requireNonNull(subscription, "The subscription cannot be null.");

        statement.setString(1, subscription.id());

        Date date = Date.valueOf(subscription.temporal().start().atZone(ZoneId.systemDefault()).toLocalDate());
        Long expiry = subscription.temporal().expiry().map(Instant::getEpochSecond).orElse(null);
        Optional<String> message = subscription.message().map(JSONComponentSerializer.json()::serialize);

        setDual(statement, 2, 10, Types.VARCHAR, String.valueOf(subscription.linkId()));
        statement.setInt(3, subscription.parentId());
        setDual(statement, 4, 11, Types.INTEGER, subscription.parentSignature());

        operator(statement, subscription); // populates slot 5

        statement.setDate(6, date);

        setDual(statement, 7, 12, Types.BIGINT, expiry);
        expectedExpiry(statement, expiry); // populates slot 8
        setDual(statement, 9, 13, Types.VARCHAR, message.orElse(null));

        overrideSignature(subscription);
        statement.addBatch();
    }

    private static void operator(@NotNull PreparedStatement statement, @NotNull PunishmentSubscription subscription) throws SQLException
    {
        UUID operator = ((InternalPunishmentSubscription) subscription).rawOperator();
        if (Objects.nonNull(operator))
        {
            statement.setString(5, String.valueOf(operator));
            return;
        }

        statement.setNull(5, Types.VARCHAR);
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
    private void expectedExpiry(@NotNull PreparedStatement statement, @Nullable Long expiry) throws SQLException, NullPointerException
    {
        Objects.requireNonNull(statement, "The prepared statement cannot be null.");

        if (Objects.nonNull(expiry))
        {
            statement.setLong(8, expiry);
            return;
        }

        statement.setNull(8, Types.BIGINT);
    }

    @Override
    public @NotNull CompletableFuture<@NotNull @UnmodifiableView Collection<PlayerClient>> findTargets(@NotNull UUID linkId)
    {
        String sql = "SELECT id, username, first_login, last_login, time_played, locale FROM ksvp_player WHERE link_id = ?;";
        InternalRepository<UUID, PlayerClient> playerRepository = (InternalPlayerRepository) KissenCore.getInstance().playerRepository();
        return CompletableFuture.supplyAsync(() -> Objects.requireNonNull(query(sql, (statement ->
        {
            statement.setString(1, String.valueOf(linkId));

            try (ResultSet resultSet = statement.executeQuery())
            {
                Set<PlayerClient> clients = new HashSet<>();
                while(resultSet.next())
                {
                    clients.add(playerRepository.toEntity(UUID.fromString(resultSet.getString("id")), resultSet));
                }
                return Collections.unmodifiableSet(clients);
            }
        }))));
    }

    @Override
    public @NotNull CompletableFuture<@NotNull @UnmodifiableView Collection<UUID>> findTargetIds(@NotNull UUID linkId)
    {
        String sql = "SELECT id FROM ksvp_player WHERE link_id = ?;";
        return CompletableFuture.supplyAsync(() -> Objects.requireNonNull(query(sql, (statement ->
        {

            statement.setString(1, String.valueOf(linkId));

            try (ResultSet resultSet = statement.executeQuery())
            {
                Set<UUID> uuidSet = new HashSet<>();
                while(resultSet.next())
                {
                    uuidSet.add(UUID.fromString(resultSet.getString("id")));
                }
                return Collections.unmodifiableSet(uuidSet);
            }
        }))));
    }

    @Override
    public @NotNull CompletableFuture<@NotNull @UnmodifiableView Collection<PunishmentSubscription>> findSubscriptions(@NotNull UUID linkId)
    {
        String sql = "SELECT * FROM ksvp_punishment_subscription WHERE link_id = ?;";
        return CompletableFuture.supplyAsync(() -> Objects.requireNonNull(query(sql, (statement ->
        {
            statement.setString(1, String.valueOf(linkId));

            Collection<PunishmentSubscription> subscriptions = new HashSet<>();

            try (ResultSet resultSet = statement.executeQuery())
            {
                while(resultSet.next())
                {
                    subscriptions.add(toEntity(resultSet));
                }
                return Collections.unmodifiableCollection(subscriptions);
            }
        }))));
    }

    @Override
    public @NotNull CompletableFuture<@NotNull @UnmodifiableView Collection<PunishmentSubscription>> findSubscriptionsByUserId(@NotNull UUID userId)
    {
        String sql = "SELECT ps.* FROM ksvp_punishment_subscription ps JOIN ksvp_player p ON ps.link_id = p.link_id WHERE p.id = %s;";
        return CompletableFuture.supplyAsync(() -> Objects.requireNonNull(query(sql, (statement ->
        {
            statement.setString(1, String.valueOf(userId));

            Collection<PunishmentSubscription> subscriptions = new HashSet<>();
            try (ResultSet resultSet = statement.executeQuery())
            {
                while(resultSet.next())
                {
                    subscriptions.add(toEntity(resultSet));
                }
                return Collections.unmodifiableCollection(subscriptions);
            }
        }))));
    }
}
