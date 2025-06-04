package net.kissenpvp.database;

import net.kissenpvp.api.database.PersistableEntity;
import net.kissenpvp.api.database.Repository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class InternalCachedRepository<P, T extends PersistableEntity<P>> extends InternalRepository<P, T>
{
    private final Map<P, T> cachedEntries;

    public InternalCachedRepository(@NotNull String table, @NotNull Connection connection)
    {
        super(table, connection);
        this.cachedEntries = new HashMap<>();
    }


}
