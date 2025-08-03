package net.kissenpvp.api.database;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public interface ConnectionProvider
{
    @NotNull Optional<Connection> connection();

    void connect(@NotNull String connectionString) throws IllegalStateException, SQLException, URISyntaxException, IOException;

    void disconnect() throws IllegalStateException;
}
