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
import org.jetbrains.annotations.Nullable;
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
        super("ksvp_punishment_subscription", connection, "SELECT parent_id, parent_signature, start_time, time_span, message FROM %s WHERE id = ?;");
    }

    /**
     * Updates the provided {@link PreparedStatement} with timespan data from the given {@link PunishmentSubscription}.
     * If the {@code timeSpan} in the subscription is an instance of {@link DefinedTimeSpan}, its duration in milliseconds
     * is set at the specified positions in the statement. If not, the corresponding positions are set to {@code NULL}.
     *
     * @param statement    The {@link PreparedStatement} to be updated with timespan data. Must not be {@code null}.
     * @param subscription The {@link PunishmentSubscription} containing the timespan data. Must not be {@code null}.
     * @param pos1         The index in the statement where the first instance of the timespan or {@code NULL} is to be set.
     * @param pos2         The index in the statement where the second instance of the timespan or {@code NULL} is to be set.
     * @throws SQLException         If an SQL error occurs while setting the data in the statement.
     * @throws NullPointerException If the provided {@link PreparedStatement} or {@link PunishmentSubscription} is {@code null}.
     */
    @SuppressWarnings("SameParameterValue")
    private static void timespan(@NotNull PreparedStatement statement, @NotNull PunishmentSubscription subscription, int pos1, int pos2) throws SQLException, NullPointerException
    {
        Objects.requireNonNull(statement, "The prepared statement cannot be null.");
        Objects.requireNonNull(subscription, "The subscription cannot be null.");


        setDual(statement, pos1, pos2, Types.BIGINT, null);
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

        String sql = "INSERT INTO %s (id, parent_id, parent_signature, start_time, time_span, message) VALUES (?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE parent_id = ?, parent_signature = ?, start_time = ?, time_span = ?, message = ?;";
        return CompletableFuture.supplyAsync(() -> query(sql, (statement ->
        {
            for (PunishmentSubscription subscription : id)
            {
                statement.setString(1, String.valueOf(subscription.id()));

                Optional<String> message = subscription.message().map(JSONComponentSerializer.json()::serialize);
                Date date = Date.valueOf(subscription.start().atZone(ZoneId.systemDefault()).toLocalDate());

                setDual(statement, 2, 7, Types.INTEGER, subscription.parentId());
                setDual(statement, 3, 8, Types.INTEGER, subscription.parentSignature());
                setDual(statement, 4, 9, Types.DATE, date);

                if (subscription.timeSpan() instanceof DefinedTimeSpan definedTimeSpan)
                {
                    setDual(statement, 5, 10, Types.BIGINT, definedTimeSpan.get(ChronoUnit.MILLIS) );
                }

                setDual(statement, 6, 11, Types.VARCHAR, message.orElse(null));


                overrideSignature(subscription);
                statement.addBatch();
            }

            statement.executeBatch();
            return null;
        })));
    }
}
