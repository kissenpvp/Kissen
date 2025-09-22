package net.kissenpvp.network.actor;

import net.kissenpvp.api.network.actor.PlayerClient;
import net.kissenpvp.database.InternalPersistableEntity;
import org.jspecify.annotations.NonNull;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public abstract class InternalPlayerClient extends InternalPersistableEntity<UUID> implements PlayerClient
{
    private final UUID linkId;

    protected InternalPlayerClient(@NonNull UUID linkId)
    {
        this.linkId = linkId;
    }

    @Override public final boolean isClient()
    {
        return true;
    }

    @Override public @NonNull UUID id()
    {
        return getUniqueId();
    }

    @Override public @NonNull UUID linkId()
    {
        return linkId;
    }

    @Override public int signature()
    {
        return Objects.hash(id(), linkId());
    }

    @Override public @NonNull Instant lastLogin()
    {
        return Instant.ofEpochMilli(getLastLogin());
    }

    // Minecraft links
    public abstract @NonNull UUID getUniqueId();

    public abstract long getLastLogin();
}
