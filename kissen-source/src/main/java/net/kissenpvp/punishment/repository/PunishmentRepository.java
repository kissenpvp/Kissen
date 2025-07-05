package net.kissenpvp.punishment.repository;

import net.kissenpvp.api.punishment.PunishmentType;
import net.kissenpvp.database.InternalCachedRepository;
import net.kissenpvp.punishment.InternalPunishment;
import net.kissenpvp.temporal.timespan.InternalDefinedTimeSpan;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class PunishmentRepository extends InternalCachedRepository<Integer, InternalPunishment>
{
    public PunishmentRepository(@NotNull String table, @NotNull Connection connection)
    {
        super(table, connection);
    }

    @Override protected @NotNull String createTableQuery()
    {
        return "CREATE TABLE IF NOT EXISTS %s (id INT PRIMARY KEY NOT NULL, punishment_type TINYINT NOT NULL, time_span BIGINT NULL);";
    }

    @Override
    protected @NotNull @UnmodifiableView InternalPunishment toEntity(@NotNull Integer id, @NotNull ResultSet resultSet) throws SQLException
    {
        Objects.requireNonNull(resultSet, "The result set cannot be null.");
        Objects.requireNonNull(id, "The id cannot be null.");

        PunishmentType type = PunishmentType.fromOrdinal(resultSet.getInt("punishment_type"));

        if(Objects.isNull(type))
        {
            String message = String.format("The punishment type of the punishment %s is null. This should not happen.", id);
            throw new SQLException(new NullPointerException(message));
        }

        InternalDefinedTimeSpan timeSpan = new InternalDefinedTimeSpan(resultSet.getLong("time_span"));
        return new InternalPunishment(id, type, timeSpan);
    }

    @Override
    protected @NotNull @UnmodifiableView InternalPunishment toEntity(@NotNull ResultSet resultSet) throws SQLException
    {
        return toEntity(resultSet.getInt("id"), resultSet);
    }

    @Override protected @NotNull String findQuery()
    {
        return "SELECT punishment_type, time_span FROM %s WHERE id = ?;";
    }

    @Override public @NotNull CompletableFuture<Void> save(@NotNull InternalPunishment id)
    {
        return saveAll(Collections.singleton(id));
    }

    @Override public @NotNull CompletableFuture<Void> saveAll(@NotNull Iterable<InternalPunishment> id)
    {
        String sql = "INSERT INTO %s (id, punishment_type, time_span) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE punishment_type = ?, time_span = ?;";
        return CompletableFuture.supplyAsync(() -> query(sql, (statement ->
        {
            for (InternalPunishment punishment : id)
            {
                statement.setInt(1, punishment.id());

                int punishmentOrdinal = punishment.punishmentType().ordinal();
                statement.setInt(2, punishmentOrdinal);
                statement.setInt(4, punishmentOrdinal);

                if (punishment.timeSpan() instanceof net.kissenpvp.api.temporal.timespan.DefinedTimeSpan definedTimeSpan)
                {
                    long millis = definedTimeSpan.get(ChronoUnit.MILLIS);
                    statement.setLong(3, millis);
                    statement.setLong(5, millis);
                }
                punishment.overrideSignature();
                statement.addBatch();
            }

            statement.executeBatch();
            return null;
        })));
    }
}
