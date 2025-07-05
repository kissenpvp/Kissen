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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PunishmentSubscriptionRepository extends InternalCachedRepository<UUID, PunishmentSubscription> implements SubscriptionRepository<UUID, Integer, Punishment, PunishmentSubscription>
{

    public PunishmentSubscriptionRepository(@NotNull String table, @NotNull Connection connection)
    {
        super(table, connection);
    }

    @Override
    protected @NotNull @UnmodifiableView InternalPunishmentSubscription toEntity(@NotNull UUID id, @NotNull ResultSet resultSet) throws SQLException
    {
        Component message = GsonComponentSerializer.gson().deserialize(resultSet.getString("message"));
        TimeSpan timeSpan = new PermanentTimeSpan();

        long timeSpanLength = resultSet.getLong("time_span");
        if(!resultSet.wasNull())
        {
            timeSpan = new InternalDefinedTimeSpan(timeSpanLength);
        }

        int parentId = resultSet.getInt("parent_id"), parentSignature = resultSet.getInt("parent_signature");
        return new InternalPunishmentSubscription(id, parentId, parentSignature, timeSpan, message);
    }

    @Override
    protected @NotNull @UnmodifiableView InternalPunishmentSubscription toEntity(@NotNull ResultSet resultSet) throws SQLException
    {
        return toEntity(UUID.fromString(resultSet.getString("id")), resultSet);
    }

    @Override protected @NotNull String createTableQuery()
    {
        return """
                CREATE TABLE IF NOT EXISTS %s (\
                id VARCHAR(36) NOT NULL, \
                parent_id INT NOT NULL, \
                parent_signature INT NOT NULL, \
                time_span BIGINT NULL, \
                message TEXT NULL, \
                PRIMARY KEY (id));
                """;
    }

    @Override protected @NotNull String findQuery()
    {
        return "SELECT parent_id, parent_signature, time_span, message FROM %s WHERE id = ?;";
    }

    @Override public @NotNull CompletableFuture<Void> saveAll(@NotNull Iterable<PunishmentSubscription> id)
    {
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

                statement.addBatch();
            }

            statement.executeBatch();
            return null;
        })));
    }

    /**
     * Sets the message values in the provided {@link PreparedStatement} based on the {@link PunishmentSubscription}
     * instance. The method retrieves the serialized message component if available and sets it to the
     * designated indices in the {@link PreparedStatement}. If no message is present, it sets NULL values
     * for the respective columns.
     *
     * @param statement The {@link PreparedStatement} where the message values are to be set.
     * @param subscription The {@link PunishmentSubscription} containing the message information.
     * @throws SQLException If an SQL error occurs while interacting with the {@link PreparedStatement}.
     */
    private static void message(@NotNull PreparedStatement statement, @NotNull PunishmentSubscription subscription) throws SQLException
    {
        Optional<String> message = subscription.message().map(JSONComponentSerializer.json()::serialize);
        if(message.isPresent())
        {
            statement.setString(5, message.get());
            statement.setString(9, message.get());
            return;
        }

        statement.setNull(5, java.sql.Types.VARCHAR);
        statement.setNull(9, java.sql.Types.VARCHAR);
    }

    /**
     * Sets the timespan values in the provided {@link PreparedStatement} based on the
     * {@link PunishmentSubscription} instance. The method checks if the timespan is an instance of
     * {@link DefinedTimeSpan} and retrieves its value in milliseconds. If no such instance exists,
     * it sets NULL values for the respective columns.
     *
     * @param statement The {@link PreparedStatement} where the timespan values are to be set.
     * @param subscription The {@link PunishmentSubscription} containing the timespan information.
     * @throws SQLException If an SQL error occurs while interacting with the statement.
     */
    private static void timespan(@NotNull PreparedStatement statement, @NotNull PunishmentSubscription subscription) throws SQLException
    {
        if(subscription.timeSpan() instanceof DefinedTimeSpan definedTimeSpan)
        {
            statement.setLong(4, definedTimeSpan.get(ChronoUnit.MILLIS));
            statement.setLong(8, definedTimeSpan.get(ChronoUnit.MILLIS));
            return;
        }

        statement.setNull(4, java.sql.Types.BIGINT);
        statement.setNull(8, java.sql.Types.BIGINT);
    }
}
