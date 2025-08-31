package net.kissenpvp.network.actor.operator;

import net.kissenpvp.database.InternalRepository;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class InternalOperatorRepository extends InternalRepository<UUID, OperatorInfo>
{
    public InternalOperatorRepository(@NotNull String table, @NotNull Connection connection) throws NullPointerException
    {
        super(table, connection, "SELECT op_level, can_bypass_player_limit FROM %s WHERE id = ?;");
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
        String sql = "INSERT INTO %s (id, op_level, can_bypass_player_limit) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE op_level = ?, can_bypass_player_limit = ?;";
        return CompletableFuture.supplyAsync(() -> query(sql, (statement ->
        {
            for(OperatorInfo operatorInfo : id)
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
