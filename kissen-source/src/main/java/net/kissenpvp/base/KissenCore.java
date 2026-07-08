package net.kissenpvp.base;

import net.kissenpvp.api.base.Kissen;
import net.kissenpvp.api.localization.GlobalLocaleRegistry;
import net.kissenpvp.api.network.actor.ConsoleClient;
import net.kissenpvp.database.AsyncDatabaseQueue;
import net.kissenpvp.database.ConnectionRegistry;
import net.kissenpvp.localization.GlobalLocaleRegistryImpl;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.UUID;

public abstract class KissenCore implements Kissen
{
    private static final Path UUID_FILE_PATH = Path.of(".server_uid");

    private UUID serverUid;

    private static final Logger log = LoggerFactory.getLogger(KissenCore.class);
    private static KissenCore instance;
    private GlobalLocaleRegistry localeRegistry;
    private boolean started;

    private AsyncDatabaseQueue databaseQueue;
    private ConnectionRegistry connectionRegistry;

    public static @NonNull KissenCore getInstance()
    {
        if (Objects.isNull(instance))
        {
            throw new IllegalStateException("Kissen has not been initiated yet!");
        }
        return instance;
    }

    public abstract ConsoleClient console();

    protected void init()
    {
        instance = this;
        localeRegistry = new GlobalLocaleRegistryImpl();
        databaseQueue = new AsyncDatabaseQueue();
        connectionRegistry = new ConnectionRegistry();

        try
        {
            serverUid = loadOrCreateServerUid();
        }
        catch (IOException exception)
        {
            log.warn("Can't create serverid.txt directory!", exception);
        }

        started = true;
    }

    private static @NonNull UUID loadOrCreateServerUid() throws IOException
    {
        if (Files.notExists(UUID_FILE_PATH)) {
            UUID newUUID = UUID.randomUUID();
            Files.writeString(
                    UUID_FILE_PATH,
                    newUUID.toString(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.WRITE
            );
            return newUUID;
        }

        String content = Files.readString(UUID_FILE_PATH).trim();
        return UUID.fromString(content);
    }

    @Override public @NonNull AsyncDatabaseQueue databaseQueue()
    {
        return databaseQueue;
    }

    @Override public @NonNull GlobalLocaleRegistry localeRegistry()
    {
        return localeRegistry;
    }

    @Override public boolean started()
    {
        return started;
    }

    @Override public @NonNull UUID serverUid()
    {
        return serverUid;
    }

    public @NonNull ConnectionRegistry getConnectionRegistry()
    {
        return connectionRegistry;
    }
}
