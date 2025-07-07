package net.kissenpvp.punishment.repository;

import net.kissenpvp.api.database.SubscriptionRepository;
import net.kissenpvp.api.punishment.Punishment;
import net.kissenpvp.api.punishment.PunishmentSubscription;
import net.kissenpvp.api.temporal.timespan.DefinedTimeSpan;
import net.kissenpvp.api.temporal.timespan.TimeSpan;
import net.kissenpvp.database.InternalCachedRepository;
import net.kissenpvp.punishment.InternalPunishmentSubscription;
import net.kissenpvp.temporal.timespan.InternalDefinedTimeSpan;
import net.kissenpvp.temporal.timespan.PermanentTimeSpan;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.sql.*;
import java.time.temporal.ChronoUnit;
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
 * @see InternalCachedRepository
 * @see PunishmentSubscription
 * @see Punishment
 *
 * @author Ivo Quiring
 */
public class PunishmentSubscriptionRepository extends InternalCachedRepository<UUID, PunishmentSubscription> implements SubscriptionRepository<UUID, Integer, Punishment, PunishmentSubscription>
{

    /**
     * Constructs a new instance of PunishmentSubscriptionRepository.
     * This initializes the repository with a predefined table name, connection,
     * and query string to fetch punishment subscription data by ID.
     *
     * @param connection The database connection to be used by this repository. Must not be null.
     * @throws NullPointerException If the provided connection is null.
     */
    public PunishmentSubscriptionRepository(@NotNull Connection connection) throws NullPointerException
    {
        super("ksvp_punishment_subscription", connection, "SELECT parent_id, parent_signature, time_span, message FROM %s WHERE id = ?;");
    }

    /**
     * Updates the provided {@link PreparedStatement} with the message data from the given {@link PunishmentSubscription}.
     * If the subscription contains a message, it serializes the message into a JSON string and sets it in the
     * statement at specific indices. If no message is present, it sets the corresponding columns to {@code NULL}.
     *
     * @param statement    The {@link PreparedStatement} where the message data is to be set. Must not be {@code null}.
     * @param subscription The {@link PunishmentSubscription} containing the message data. Must not be {@code null}.
     * @throws SQLException         If an SQL error occurs while setting the data in the statement.
     * @throws NullPointerException If the provided {@link PreparedStatement} or {@link PunishmentSubscription} is {@code null}.
     */
    private static void message(@NotNull PreparedStatement statement, @NotNull PunishmentSubscription subscription) throws SQLException, NullPointerException
    {
        Objects.requireNonNull(statement, "The prepared statement cannot be null.");
        Objects.requireNonNull(subscription, "The subscription cannot be null.");

        Optional<String> message = subscription.message().map(JSONComponentSerializer.json()::serialize);
        if (message.isPresent())
        {
            statement.setString(5, message.get());
            statement.setString(9, message.get());
            return;
        }

        int type = Types.VARCHAR;
        statement.setNull(4, type);
        statement.setNull(7, type);
    }

    /**
     * Updates the provided {@link PreparedStatement} with the time span data from the given {@link PunishmentSubscription}.
     * If the subscription's time span is a defined time span, its duration in milliseconds is set in the statement
     * at specific indices. If the subscription's time span is undefined, the corresponding columns in the statement are set to {@code NULL}.
     *
     * @param statement    The {@link PreparedStatement} to be updated with the time span data. Must not be {@code null}.
     * @param subscription The {@link PunishmentSubscription} containing the time span data. Must not be {@code null}.
     * @throws SQLException         If an SQL error occurs while setting the data in the statement.
     * @throws NullPointerException If the provided {@link PreparedStatement} or {@link PunishmentSubscription} is {@code null}.
     */
    private static void timespan(@NotNull PreparedStatement statement, @NotNull PunishmentSubscription subscription) throws SQLException, NullPointerException
    {
        Objects.requireNonNull(statement, "The prepared statement cannot be null.");
        Objects.requireNonNull(subscription, "The subscription cannot be null.");

        if (subscription.timeSpan() instanceof DefinedTimeSpan definedTimeSpan)
        {
            long millis = definedTimeSpan.get(ChronoUnit.MILLIS);
            statement.setLong(4, definedTimeSpan.get(ChronoUnit.MILLIS));
            statement.setLong(8, definedTimeSpan.get(ChronoUnit.MILLIS));
            return;
        }

        int type = Types.BIGINT;
        statement.setNull(3, type);
        statement.setNull(6, type);
    }

    @Override
    protected @NotNull @UnmodifiableView InternalPunishmentSubscription toEntity(@NotNull UUID id, @NotNull ResultSet resultSet) throws SQLException, NullPointerException
    {
        Objects.requireNonNull(id, "The id cannot be null.");
        Objects.requireNonNull(resultSet, "The result set cannot be null.");

        Component message = GsonComponentSerializer.gson().deserialize(resultSet.getString("message"));
        TimeSpan timeSpan = new PermanentTimeSpan();

        long timeSpanLength = resultSet.getLong("time_span");
        if (!resultSet.wasNull())
        {
            timeSpan = new InternalDefinedTimeSpan(timeSpanLength);
        }

        int parentId = resultSet.getInt("parent_id"), parentSignature = resultSet.getInt("parent_signature");
        return new InternalPunishmentSubscription(id, parentId, parentSignature, timeSpan, message);
    }

    @Override
    protected @NotNull @UnmodifiableView InternalPunishmentSubscription toEntity(@NotNull ResultSet resultSet) throws SQLException, NullPointerException
    {
        Objects.requireNonNull(resultSet, "The result set cannot be null.");

        return toEntity(UUID.fromString(resultSet.getString("id")), resultSet);
    }

    @Override public @NotNull CompletableFuture<Void> saveAll(@NotNull Iterable<PunishmentSubscription> id) throws NullPointerException
    {
        Objects.requireNonNull(id, "The iterable of subscriptions cannot be null.");

        String sql = "INSERT INTO %s (id, parent_id, parent_signature, time_span, message) VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE parent_id = ?, parent_signature = ?, time_span = ?, message = ?;";
        return CompletableFuture.supplyAsync(() -> query(sql, (statement ->
        {
            for (PunishmentSubscription subscription : id)
            {
                statement.setString(1, String.valueOf(subscription.id()));

                statement.setInt(2, subscription.parentId());
                statement.setInt(6, subscription.parentId());

                statement.setInt(3, subscription.parentSignature());
                statement.setInt(7, subscription.parentSignature());

                timespan(statement, subscription);
                message(statement, subscription);

                overrideSignature(subscription);

                statement.addBatch();
            }

            statement.executeBatch();
            return null;
        })));
    }
}
