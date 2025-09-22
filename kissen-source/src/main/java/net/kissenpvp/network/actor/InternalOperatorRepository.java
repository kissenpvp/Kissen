package net.kissenpvp.network.actor;

import net.kissenpvp.api.network.actor.OperatorInfo;
import net.kissenpvp.database.InternalRepository;
import org.jspecify.annotations.NonNull;


import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * An abstract repository class specifically designed to manage {@link OperatorInfo} entities
 * within a relational database.
 * <p>
 * This class provides fundamental data access operations
 * for a table containing operator information and handles the mapping between database rows
 * and {@link OperatorInfo} objects.
 * <p>
 * This repository extends the {@link InternalRepository} class, inheriting base functionality
 * for database interaction, while overriding and implementing specific behavior to
 * transform database rows into {@link OperatorInfo} entities.
 *
 * @author Ivo Quiring
 */
public abstract class InternalOperatorRepository extends InternalRepository<UUID, OperatorInfo>
{
    public InternalOperatorRepository(@NonNull DataSource dataSource) throws NullPointerException
    {
        super(dataSource);
    }

    @Override public @NonNull CompletableFuture<@NonNull Optional<OperatorInfo>> find(@NonNull UUID id) throws NullPointerException
    {
        String sql = "SELECT o.id, p.username AS username, o.operator_level, o.can_bypass_player_limit FROM ksvp_operators o JOIN ksvp_player p ON o.id = p.id WHERE o.id = ?;";
        return CompletableFuture.supplyAsync(() -> Objects.requireNonNull(query(sql, statement ->
        {
            statement.setString(1, String.valueOf(id));
            return collectResults(id, statement).stream().findFirst();
        })));
    }

    @Override public @NonNull CompletableFuture< Collection<OperatorInfo>> findAll(@NonNull Iterable<UUID> id) throws NullPointerException
    {
        String placeHolders = String.join(", ", Collections.nCopies(computeIterableSize(id), "?"));
        String sql = "SELECT o.id, p.username AS username, o.operator_level, o.can_bypass_player_limit FROM ksvp_operators o JOIN ksvp_player p ON o.id = p.id WHERE o.id IN (" + placeHolders + ");";
        return CompletableFuture.supplyAsync(() -> query(sql, statement ->
        {
            int index = 1;
            for (UUID current : id)
            {
                statement.setString(index++, String.valueOf(current));
            }

            return collectResults(statement);
        }));
    }

    @Override public @NonNull CompletableFuture< Collection<OperatorInfo>> findAll()
    {
        String sql = "SELECT o.id, p.username AS username, o.operator_level, o.can_bypass_player_limit FROM ksvp_operators o JOIN ksvp_player p ON o.id = p.id;";
        return CompletableFuture.supplyAsync(() -> query(sql, this::collectResults));
    }

    @Override public @NonNull CompletableFuture<Boolean> has(@NonNull UUID id) throws NullPointerException
    {
        String sql = "SELECT id FROM ksvp_operators WHERE id = ?;";
        return CompletableFuture.supplyAsync(() -> query(sql, statement ->
        {
            statement.setString(1, String.valueOf(id));
            return hasResult(statement);
        }));
    }

    @Override
    public abstract @NonNull OperatorInfo toEntity(@NonNull UUID id, @NonNull ResultSet resultSet) throws SQLException, NullPointerException;

    @Override
    public @NonNull OperatorInfo toEntity(@NonNull ResultSet resultSet) throws SQLException, NullPointerException
    {
        return toEntity(UUID.fromString(resultSet.getString("id")), resultSet);
    }

    @Override
    public @NonNull CompletableFuture<Void> saveAll(@NonNull Iterable<OperatorInfo> id) throws NullPointerException
    {
        String sql = "INSERT INTO ksvp_operators (id, operator_level, can_bypass_player_limit) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE operator_level = ?, can_bypass_player_limit = ?;";
        return CompletableFuture.supplyAsync(() -> query(sql, (statement ->
        {
            for (OperatorInfo operatorInfo : id)
            {
                statement.setString(1, String.valueOf(operatorInfo.id()));

                setDual(statement, 2, 4, Types.INTEGER, operatorInfo.getLevel());
                setDual(statement, 3, 5, Types.BOOLEAN, operatorInfo.getBypassesPlayerLimit());

                statement.addBatch();
            }

            statement.executeBatch();
            return null;
        })));
    }
}
