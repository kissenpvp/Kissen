package net.kissenpvp.punishment.repository;

import net.kissenpvp.api.punishment.Punishment;
import net.kissenpvp.api.punishment.PunishmentType;
import net.kissenpvp.api.temporal.timespan.DefinedTimeSpan;
import net.kissenpvp.database.InternalCachedRepository;
import net.kissenpvp.punishment.InternalPunishment;
import net.kissenpvp.temporal.timespan.InternalDefinedTimeSpan;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.jspecify.annotations.NonNull;


import javax.sql.DataSource;
import java.sql.*;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
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
 * @author Ivo Quiring
 * @see InternalCachedRepository
 * @see Punishment
 * @see net.kissenpvp.api.punishment.PunishmentSubscription
 */
public class InternalPunishmentRepository extends InternalCachedRepository<Integer, Punishment>
{

    public InternalPunishmentRepository(@NonNull DataSource dataSource) throws NullPointerException
    {
        super(dataSource);
    }

    @Override protected @NonNull CompletableFuture<Optional<Punishment>> findUncached(@NonNull Integer id) throws NullPointerException
    {
        String sql = "SELECT punishment_type, time_span, message FROM ksvp_punishment WHERE id = ?;";
        return CompletableFuture.supplyAsync(() -> query(sql, statement ->
        {
            statement.setInt(1, id);
            return collectResults(id, statement).stream().findFirst();
        }));
    }

    @Override protected @NonNull CompletableFuture< Collection<Punishment>> findAllUncached(@NonNull Iterable<Integer> id) throws NullPointerException
    {
        String placeHolders = String.join(", ", Collections.nCopies(computeIterableSize(id), "?"));
        String sql = "SELECT id, punishment_type, time_span, message FROM ksvp_punishment WHERE id IN (" + placeHolders + ");";
        return CompletableFuture.supplyAsync(() -> query(sql, statement ->
        {
            int index = 1;
            for (int current : id)
            {
                statement.setInt(index++, current);
            }

            return collectResults(statement);
        }));
    }

    @Override public @NonNull CompletableFuture< Collection<Punishment>> findAll()
    {
        String sql = "SELECT id, punishment_type, time_span, message FROM ksvp_punishment;";
        return CompletableFuture.supplyAsync(() -> query(sql, this::collectResults));
    }

    @Override public @NonNull CompletableFuture<Boolean> has(@NonNull Integer id) throws NullPointerException
    {
        String sql = "SELECT id FROM ksvp_punishment WHERE id = ?;";
        return CompletableFuture.supplyAsync(() -> query(sql, statement ->
        {
            statement.setInt(1, id);
            return hasResult(statement);
        }));
    }

    @Override protected @NonNull InternalPunishment toCachedEntity(@NonNull Integer id, @NonNull ResultSet resultSet) throws SQLException, NullPointerException
    {
        Objects.requireNonNull(id, "The id cannot be null.");
        Objects.requireNonNull(resultSet, "The result set cannot be null.");

        PunishmentType type = PunishmentType.fromOrdinal(resultSet.getInt("punishment_type"));
        InternalDefinedTimeSpan timeSpan = new InternalDefinedTimeSpan(resultSet.getLong("time_span"));
        return new InternalPunishment(id, type, timeSpan);
    }

    @Override protected @NonNull InternalPunishment toCachedEntity(@NonNull ResultSet resultSet) throws SQLException, NullPointerException
    {
        return toCachedEntity(resultSet.getInt("id"), resultSet);
    }

    @Override public @NonNull CompletableFuture<Void> save(@NonNull Punishment id) throws NullPointerException
    {
        Objects.requireNonNull(id, "The punishment cannot be null.");

        return saveAll(Collections.singleton(id));
    }

    @Override public @NonNull CompletableFuture<Void> saveAll(@NonNull Iterable<Punishment> id) throws NullPointerException
    {
        Objects.requireNonNull(id, "The punishment iterable cannot be null.");

        String sql = "INSERT INTO ksvp_punishment (id, punishment_type, time_span, message) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE punishment_type = ?, time_span = ?, message = ?;";
        return CompletableFuture.supplyAsync(() -> query(sql, (statement ->
        {
            for (Punishment punishment : id)
            {
                addBatch(statement, punishment);
            }

            statement.executeBatch();
            return null;
        })));
    }

    /**
     * Adds a batch to a provided {@link PreparedStatement} for the given {@link Punishment}.
     * This method prepares the {@link PreparedStatement} by setting the required parameters
     * based on the properties defined in the {@link Punishment} instance and calls {@code addBatch()}
     * to include it in the batch execution.
     *
     * @param statement  The {@link PreparedStatement} to which the batch is added. Must not be null.
     * @param punishment The {@link Punishment} instance containing the data to prepare the statement. Must not be null.
     * @throws SQLException         If an error occurs while interacting with the {@link PreparedStatement}.
     * @throws NullPointerException If either {@code statement} or {@code punishment} is null.
     */
    private void addBatch(@NonNull PreparedStatement statement, @NonNull Punishment punishment) throws SQLException, NullPointerException
    {
        Objects.requireNonNull(statement, "The prepared statement cannot be null.");
        Objects.requireNonNull(punishment, "The punishment cannot be null.");

        statement.setInt(1, punishment.id());

        Optional<String> message = punishment.defaultMessage().map(JSONComponentSerializer.json()::serialize);

        setDual(statement, 2, 5, Types.TINYINT, punishment.punishmentType().ordinal());

        if (punishment.timeSpan() instanceof DefinedTimeSpan definedTimeSpan)
        {
            setDual(statement, 3, 6, Types.BIGINT, definedTimeSpan.get(ChronoUnit.MILLIS));
        }
        else
        {
            setDual(statement, 3, 6, Types.BIGINT, null);
        }

        setDual(statement, 4, 7, Types.VARCHAR, message.orElse(null));

        overrideSignature(punishment);
        statement.addBatch();
    }
}
