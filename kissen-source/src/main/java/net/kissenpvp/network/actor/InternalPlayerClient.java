package net.kissenpvp.network.actor;

import net.kissenpvp.api.network.actor.PlayerClient;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public abstract class InternalPlayerClient implements PlayerClient
{
    private final UUID linkId;

    protected InternalPlayerClient(UUID linkId) {this.linkId = linkId;}

    @Override public final boolean isClient()
    {
        return true;
    }

    @Override public @NotNull UUID linkId()
    {
        return linkId;
    }

    @Override public @NotNull UUID id()
    {
        return getUniqueId();
    }

    @Override public int signature()
    {
        return Objects.hash(id(), linkId());
    }

    @Override public boolean unsaved()
    {
        return false;
    }

    public abstract @NotNull UUID getUniqueId();
}
