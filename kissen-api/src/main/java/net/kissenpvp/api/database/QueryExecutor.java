package net.kissenpvp.api.database;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface QueryExecutor<X>
{
    @Nullable X executeQuery(@NonNull PreparedStatement statement) throws SQLException;
}
