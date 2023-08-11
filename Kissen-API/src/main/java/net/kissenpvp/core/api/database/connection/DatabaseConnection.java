package net.kissenpvp.core.api.database.connection;

import net.kissenpvp.core.api.database.meta.BackendException;
import net.kissenpvp.core.api.database.meta.Meta;
import net.kissenpvp.core.api.database.meta.ObjectMeta;
import org.jetbrains.annotations.NotNull;

public interface DatabaseConnection {

    @NotNull String getConnectionID();

    @NotNull String getConnectionString();

    @NotNull DatabaseDriver getDriver();

    boolean isConnected();

    void connect() throws BackendException;

    void disconnect() throws BackendException;

    @NotNull ObjectMeta createObjectMeta(@NotNull String table);
    
    @NotNull Meta createMeta(@NotNull String table, @NotNull String uuidColumn, @NotNull String keyColumn, @NotNull String valueColumn);
}
