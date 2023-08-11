package net.kissenpvp.core.api.database.connection;

import net.kissenpvp.core.api.database.meta.BackendException;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;

public interface MYSQLDatabaseConnection extends DatabaseConnection {
    @NotNull PreparedStatement getPreparedStatement(@NotNull String query) throws BackendException;
}
