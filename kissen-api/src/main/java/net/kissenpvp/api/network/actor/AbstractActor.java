package net.kissenpvp.api.network.actor;

import net.kissenpvp.api.network.NetworkEntity;
import org.jetbrains.annotations.NotNull;

public interface AbstractActor extends NetworkEntity
{
    @NotNull String name();
}
