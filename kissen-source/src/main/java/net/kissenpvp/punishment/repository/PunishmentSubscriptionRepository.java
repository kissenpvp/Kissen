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
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.sql.*;
import java.time.ZoneId;
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
 * @author Ivo Quiring
 * @see InternalCachedRepository
 * @see PunishmentSubscription
 * @see Punishment
 */
public class PunishmentSubscriptionRepository extends InternalCachedRepository<String, PunishmentSubscription> implements SubscriptionRepository<String, Integer, Punishment, PunishmentSubscription>
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
        super("ksvp_punishment_subscription", connection, "SELECT parent_id, parent_signature, start_time, time_span, message FROM %s WHERE id = ?;");
    }

    @Override
    protected @NotNull @UnmodifiableView InternalPunishmentSubscription toEntity(@NotNull String id, @NotNull ResultSet resultSet) throws SQLException, NullPointerException
    {
        Objects.requireNonNull(id, "The id cannot be null.");
        Objects.requireNonNull(resultSet, "The result set cannot be null.");

        TimeSpan timeSpan = new PermanentTimeSpan();

        long timeSpanLength = resultSet.getLong("time_span");
        if (!resultSet.wasNull())
        {
            timeSpan = new InternalDefinedTimeSpan(timeSpanLength * 1000); // turn seconds into millis
        }

        return new InternalPunishmentSubscription(id, //
                UUID.fromString(resultSet.getString("link_id")),  //
                resultSet.getInt("parent_id"), //
                resultSet.getInt("parent_signature"), //
                resultSet.getDate("start_time").toInstant(), //
                timeSpan, //
                GsonComponentSerializer.gson().deserialize(resultSet.getString("message")) //
        );
    }

    @Override
    protected @NotNull @UnmodifiableView InternalPunishmentSubscription toEntity(@NotNull ResultSet resultSet) throws SQLException, NullPointerException
    {
        Objects.requireNonNull(resultSet, "The result set cannot be null.");

        return toEntity(resultSet.getString("id"), resultSet);
    }

    @Override
    public @NotNull CompletableFuture<Void> saveAll(@NotNull Iterable<PunishmentSubscription> id) throws NullPointerException
    {
        Objects.requireNonNull(id, "The iterable of subscriptions cannot be null.");

        String sql = """
                    INSERT INTO %s (id, link_id, parent_id, parent_signature, start_time, time_span, message)\s
                    VALUES\s
                        (?, ?, ?, ?, ?, ?, ?)\s
                    ON DUPLICATE KEY UPDATE\s
                        link_id = ?, parent_id = ?, parent_signature = ?, start_time = ?, time_span = ?, message = ?;
                """;

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

        Date date = Date.valueOf(subscription.start().atZone(ZoneId.systemDefault()).toLocalDate());
        Optional<String> message = subscription.message().map(JSONComponentSerializer.json()::serialize);

        setDual(statement, 2, 8, Types.VARCHAR, String.valueOf(subscription.linkId()));
        setDual(statement, 3, 9, Types.INTEGER, subscription.parentId());
        setDual(statement, 4, 10, Types.INTEGER, subscription.parentSignature());
        setDual(statement, 5, 11, Types.DATE, date);

        if (subscription.timeSpan() instanceof DefinedTimeSpan definedTimeSpan)
        {
            // We're storing the seconds instead of the millis
            setDual(statement, 6, 12, Types.BIGINT, definedTimeSpan.get(ChronoUnit.SECONDS));
        }

        setDual(statement, 7, 13, Types.VARCHAR, message.orElse(null));

        overrideSignature(subscription);
        statement.addBatch();
    }
}
