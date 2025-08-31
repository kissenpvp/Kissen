package net.kissenpvp.network.actor.operator;

import net.kissenpvp.database.InternalRepository;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.UUID;
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
    /**
     * Constructs a new {@code InternalOperatorRepository} instance with the specified table name and database connection.
     * This class provides repository functionalities for managing operator information in a specified database table.
     *
     * @param table      the name of the database table to use; must not be null, blank, or invalid.
     * @param connection the database connection to use; must not be null.
     * @throws NullPointerException     if the {@code table} or {@code connection} is {@code null}.
     * @throws IllegalArgumentException if the {@code table} name is blank, too long, or does not match the required pattern.
     */
    public InternalOperatorRepository(@NotNull String table, @NotNull Connection connection) throws NullPointerException
    {
        super(table, connection, "SELECT username, op_level, can_bypass_player_limit FROM %s WHERE id = ?;");
    }

    @Override
    protected abstract @NotNull OperatorInfo toEntity(@NotNull UUID id, @NotNull ResultSet resultSet) throws SQLException, NullPointerException;

    @Override
    protected @NotNull OperatorInfo toEntity(@NotNull ResultSet resultSet) throws SQLException, NullPointerException
    {
        return toEntity(UUID.fromString(resultSet.getString("id")), resultSet);
    }

    @Override
    public @NotNull CompletableFuture<Void> saveAll(@NotNull Iterable<OperatorInfo> id) throws NullPointerException
    {
        String sql = "INSERT INTO %s (id, username, op_level, can_bypass_player_limit) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE username = ?, op_level = ?, can_bypass_player_limit = ?;";
        return CompletableFuture.supplyAsync(() -> query(sql, (statement ->
        {
            for (OperatorInfo operatorInfo : id)
            {
                statement.setString(1, String.valueOf(operatorInfo.id()));

                setDual(statement, 2, 5, Types.VARCHAR, operatorInfo.name());
                setDual(statement, 3, 6, Types.INTEGER, operatorInfo.getLevel());
                setDual(statement, 4, 7, Types.BOOLEAN, operatorInfo.getBypassesPlayerLimit());

                statement.addBatch();
            }

            statement.executeBatch();
            return null;
        })));
    }
}
