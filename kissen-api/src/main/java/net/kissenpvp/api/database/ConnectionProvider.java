package net.kissenpvp.api.database;

import javax.sql.DataSource;

public interface ConnectionProvider extends DataSource
{
    void disconnect() throws IllegalStateException;
}
