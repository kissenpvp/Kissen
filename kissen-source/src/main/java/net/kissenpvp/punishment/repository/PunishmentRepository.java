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

                Optional<String> message = punishment.defaultMessage().map(JSONComponentSerializer.json()::serialize);

                setDual(statement, 2, 5, Types.TINYINT, punishment.punishmentType().ordinal());

                if (punishment.timeSpan() instanceof DefinedTimeSpan definedTimeSpan)
                {
                    setDual(statement, 3, 6, Types.BIGINT, definedTimeSpan.get(ChronoUnit.MILLIS));
                }

                setDual(statement, 4, 7, Types.VARCHAR, message.orElse(null));

                overrideSignature(punishment);
                statement.addBatch();
            }

            statement.executeBatch();
            return null;
        })));
    }
}
