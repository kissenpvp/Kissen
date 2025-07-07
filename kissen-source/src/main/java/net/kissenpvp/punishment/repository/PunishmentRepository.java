package net.kissenpvp.punishment.repository;

import net.kissenpvp.api.punishment.Punishment;
import net.kissenpvp.api.punishment.PunishmentType;
import net.kissenpvp.api.temporal.timespan.DefinedTimeSpan;
import net.kissenpvp.database.InternalCachedRepository;
import net.kissenpvp.punishment.InternalPunishment;
import net.kissenpvp.temporal.timespan.InternalDefinedTimeSpan;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.sql.*;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * The {@code PunishmentRepository} class is responsible for managing and persisting {@link Punishment} entities.
 * It extends {@code InternalCachedRepository}, leveraging its internal caching mechanisms and database interactions
 * to efficiently access and store punishment data. The repository is specifically designed to handle data operations
 * for the "ksvp_punishment" table.
 * <p>
 * This class provides methods for saving {@link Punishment} instances to the database, converting database records
 * into {@link InternalPunishment} entities, and handling serialized data like timespans and default messages.
 *
 * @see InternalCachedRepository
 * @see Punishment
 * @see net.kissenpvp.api.punishment.PunishmentSubscription
 *
 * @author Ivo Quiring
 */
public class PunishmentRepository extends InternalCachedRepository<Integer, Punishment>
{
    /**
     * Constructs a new {@code PunishmentRepository} instance, initializing it with the specified database connection.
     *
     * @param connection The {@link Connection} to the database. Must not be {@code null}.
     * @throws NullPointerException If the provided {@code connection} is {@code null}.
     */
    public PunishmentRepository(@NotNull Connection connection) throws NullPointerException
    {
        super("ksvp_punishment", connection, "SELECT punishment_type, time_span, message FROM %s WHERE id = ?;");
    }

    /**
     * Sets the message values in the provided {@link PreparedStatement} based on the {@link Punishment}
     * instance. The method retrieves the default message component if available and sets it
     * to the designated indices in the {@link PreparedStatement}. If no default message is present, it
     * sets {@code NULL} values for the respective columns.
     *
     * @param statement  The {@link PreparedStatement} where the message values are to be set. Must not be {@code null}.
     * @param punishment The {@link Punishment} instance containing the message information. Must not be {@code null}.
     * @throws SQLException         If an SQL error occurs while interacting with the {@link PreparedStatement}.
     * @throws NullPointerException If either {@code statement} or {@code punishment} is {@code null}.
     */
    private static void message(@NotNull PreparedStatement statement, @NotNull Punishment punishment) throws SQLException, NullPointerException
    {
        Objects.requireNonNull(statement, "The prepared statement cannot be null.");
        Objects.requireNonNull(punishment, "The punishment cannot be null.");

        Optional<String> message = punishment.defaultMessage().map(JSONComponentSerializer.json()::serialize);
        if (message.isPresent())
        {
            statement.setString(4, message.get());
            statement.setString(7, message.get());
            return;
        }

        int type = Types.VARCHAR;
        statement.setNull(4, type);
        statement.setNull(7, type);
    }

    /**
     * Sets the timespan values in the provided {@link PreparedStatement} based on the {@link Punishment} instance.
     * If the timespan is a {@link DefinedTimeSpan}, it retrieves the time in milliseconds and sets it
     * to the designated indices in the {@link PreparedStatement}. If no defined timespan is present,
     * the method sets {@code NULL} values for the respective columns.
     *
     * @param statement  The {@link PreparedStatement} where the timespan values are to be set. Must not be {@code null}.
     * @param punishment The {@link Punishment} instance containing the timespan information. Must not be {@code null}.
     * @throws SQLException         If an SQL error occurs while interacting with the {@link PreparedStatement}.
     * @throws NullPointerException If either {@code statement} or {@code punishment} is {@code null}.
     */
    private static void timespan(@NotNull PreparedStatement statement, @NotNull Punishment punishment) throws SQLException, NullPointerException
    {
        Objects.requireNonNull(statement, "The prepared statement cannot be null.");
        Objects.requireNonNull(punishment, "The punishment cannot be null.");

        if (punishment.timeSpan() instanceof DefinedTimeSpan definedTimeSpan)
        {
            long millis = definedTimeSpan.get(ChronoUnit.MILLIS);
            statement.setLong(3, definedTimeSpan.get(ChronoUnit.MILLIS));
            statement.setLong(6, definedTimeSpan.get(ChronoUnit.MILLIS));
            return;
        }

        int type = Types.BIGINT;
        statement.setNull(3, type);
        statement.setNull(6, type);
    }

    @Override
    protected @NotNull @UnmodifiableView InternalPunishment toEntity(@NotNull Integer id, @NotNull ResultSet resultSet) throws SQLException, NullPointerException
    {
        Objects.requireNonNull(id, "The id cannot be null.");
        Objects.requireNonNull(resultSet, "The result set cannot be null.");

        PunishmentType type = PunishmentType.fromOrdinal(resultSet.getInt("punishment_type"));

        if (Objects.isNull(type))
        {
            String message = String.format("The punishment type of the punishment %s is null. This should not happen.", id);
            throw new SQLException(new NullPointerException(message));
        }

        InternalDefinedTimeSpan timeSpan = new InternalDefinedTimeSpan(resultSet.getLong("time_span"));
        return new InternalPunishment(id, type, timeSpan);
    }

    @Override
    protected @NotNull @UnmodifiableView InternalPunishment toEntity(@NotNull ResultSet resultSet) throws SQLException, NullPointerException
    {
        Objects.requireNonNull(resultSet, "The result set cannot be null.");

        return toEntity(resultSet.getInt("id"), resultSet);
    }

    @Override public @NotNull CompletableFuture<Void> save(@NotNull Punishment id) throws NullPointerException
    {
        Objects.requireNonNull(id, "The punishment cannot be null.");

        return saveAll(Collections.singleton(id));
    }

    @Override
    public @NotNull CompletableFuture<Void> saveAll(@NotNull Iterable<Punishment> id) throws NullPointerException
    {
        Objects.requireNonNull(id, "The punishment iterable cannot be null.");

        String sql = "INSERT INTO %s (id, punishment_type, time_span, message) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE punishment_type = ?, time_span = ?, message = ?;";
        return CompletableFuture.supplyAsync(() -> query(sql, (statement ->
        {
            for (Punishment punishment : id)
            {
                statement.setInt(1, punishment.id());

                int punishmentOrdinal = punishment.punishmentType().ordinal();
                statement.setInt(2, punishmentOrdinal);
                statement.setInt(5, punishmentOrdinal);

                timespan(statement, punishment);
                message(statement, punishment);

                overrideSignature(punishment);
                statement.addBatch();
            }

            statement.executeBatch();
            return null;
        })));
    }
}
