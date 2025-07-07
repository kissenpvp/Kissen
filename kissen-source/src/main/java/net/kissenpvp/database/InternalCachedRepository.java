package net.kissenpvp.database;

import net.kissenpvp.api.database.PersistableEntity;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public abstract class InternalCachedRepository<P, T extends PersistableEntity<P>> extends InternalRepository<P, T>
{
    private final Map<P, T> cachedEntries;

    public InternalCachedRepository(@NotNull String table, @NotNull Connection connection, @NotNull String findQuery) throws NullPointerException
    {
        super(table, connection, findQuery);
        this.cachedEntries = new HashMap<>();
    }
}
