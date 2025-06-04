package net.kissenpvp.database;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface QueryExecutor<X>
{
    @Nullable X executeQuery(@NotNull PreparedStatement statement) throws SQLException;
}
