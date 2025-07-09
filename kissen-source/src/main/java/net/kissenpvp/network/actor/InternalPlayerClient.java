package net.kissenpvp.network.actor;

import net.kissenpvp.api.network.actor.PlayerClient;
import net.kissenpvp.database.InternalPersistableEntity;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public abstract class InternalPlayerClient extends InternalPersistableEntity<UUID> implements PlayerClient
{
    private final UUID linkId;
    private Locale locale;

    protected InternalPlayerClient(@NotNull UUID linkId)
    {
        this.linkId = linkId;
    }

    @Override public final boolean isClient()
    {
        return true;
    }

    @Override public @NotNull UUID id()
    {
        return getUniqueId();
    }

    @Override public @NotNull UUID linkId()
    {
        return linkId;
    }

    @Override public int signature()
    {
        return Objects.hash(id(), linkId());
    }

    @Override public @NotNull Instant lastLogin()
    {
        return Instant.ofEpochMilli(getLastLogin());
    }

    // Minecraft links
    public abstract @NotNull UUID getUniqueId();

    public abstract long getLastLogin();
}
