package net.kissenpvp.punishment.repository;

import net.kissenpvp.api.database.Repository;
import net.kissenpvp.api.punishment.Punishment;
import net.kissenpvp.api.punishment.PunishmentSubscription;
import net.kissenpvp.api.temporal.WritableTemporalObject;
import net.kissenpvp.database.InternalCachedRepository;
import net.kissenpvp.database.InternalRepository;
import net.kissenpvp.punishment.InternalPunishmentSubscription;
import net.kissenpvp.temporal.InternalWritableTemporalObject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
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
public class InternalPunishmentSubscriptionRepository extends InternalRepository<String, PunishmentSubscription> implements Repository<String, PunishmentSubscription> {

    /**
     * Constructs a new instance of PunishmentSubscriptionRepository.
     * This initializes the repository with a predefined table name, connection,
     * and query string to fetch punishment subscription data by ID.
     *
     * @param connection The database connection to be used by this repository. Must not be null.
     * @throws NullPointerException If the provided connection is null.
     */
    public InternalPunishmentSubscriptionRepository(@NotNull Connection connection) throws NullPointerException {
        super("ksvp_punishment_subscription", connection, "SELECT parent_id, parent_signature, start_time, expiry, expected_expiry, time_span, message FROM ksvp_punishment_subscription WHERE id = ?;");
    }

    @Override
    protected @NotNull InternalPunishmentSubscription toEntity(@NotNull String id, @NotNull ResultSet resultSet) throws SQLException, NullPointerException {
        Objects.requireNonNull(id, "The id cannot be null.");
        Objects.requireNonNull(resultSet, "The result set cannot be null.");

        Component message = null;
        String messageString = resultSet.getString("message");
        if (!resultSet.wasNull()) {
            message = GsonComponentSerializer.gson().deserialize(messageString);
        }

        int parentId = resultSet.getInt("parent_id");
        UUID linkId = UUID.fromString(resultSet.getString("link_id"));

        Instant start = resultSet.getDate("start_time").toInstant(); // expected to be not null

        Instant expiry = convertSafely(Date::toInstant, resultSet.getDate("expiry"));
        Instant expectedExpiry = convertSafely(Date::toInstant, resultSet.getDate("expected_expiry"));

        WritableTemporalObject temporal = new InternalWritableTemporalObject(start, expiry, expectedExpiry);

        return new InternalPunishmentSubscription(id, parentId, linkId, temporal, message);
    }

    @Override
    protected @NotNull InternalPunishmentSubscription toEntity(@NotNull ResultSet resultSet) throws SQLException, NullPointerException {
        Objects.requireNonNull(resultSet, "The result set cannot be null.");

        return toEntity(resultSet.getString("id"), resultSet);
    }

    @Override
    public @NotNull CompletableFuture<Void> saveAll(@NotNull Iterable<PunishmentSubscription> id) throws NullPointerException {
        Objects.requireNonNull(id, "The iterable of subscriptions cannot be null.");

        String sql = "INSERT INTO ksvp_punishment_subscription (id, link_id, parent_id, parent_signature, start_time, expiry, expected_expiry, message) VALUES (?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE link_id = ?, parent_id = ?, parent_signature = ?, expiry = ?, message = ?; ";
        return CompletableFuture.supplyAsync(() -> query(sql, (statement ->
        {
            for (PunishmentSubscription subscription : id) {
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
    private void addBatch(@NotNull PreparedStatement statement, @NotNull PunishmentSubscription subscription) throws SQLException, NullPointerException {
        Objects.requireNonNull(statement, "The prepared statement cannot be null.");
        Objects.requireNonNull(subscription, "The subscription cannot be null.");

        statement.setString(1, subscription.id());

        Date date = Date.valueOf(subscription.temporal().start().atZone(ZoneId.systemDefault()).toLocalDate());
        Long expiry = subscription.temporal().expiry().map(Instant::getEpochSecond).orElse(null);
        Optional<String> message = subscription.message().map(JSONComponentSerializer.json()::serialize);

        setDual(statement, 2, 9, Types.VARCHAR, String.valueOf(subscription.linkId()));
        setDual(statement, 3, 10, Types.INTEGER, subscription.parentId());
        setDual(statement, 4, 11, Types.INTEGER, subscription.parentSignature());

        statement.setDate(5, date);

        setDual(statement, 6, 12, Types.BIGINT, expiry);
        expectedExpiry(statement, expiry);
        setDual(statement, 8, 13, Types.VARCHAR, message.orElse(null));

        overrideSignature(subscription);
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
    private void expectedExpiry(@NotNull PreparedStatement statement, @Nullable Long expiry) throws SQLException, NullPointerException {
        Objects.requireNonNull(statement, "The prepared statement cannot be null.");

        if (Objects.nonNull(expiry)) {
            statement.setLong(7, expiry);
            return;
        }

        statement.setNull(7, Types.BIGINT);
    }
}
