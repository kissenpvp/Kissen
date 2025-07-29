package net.kissenpvp.api.network.actor;

import net.kissenpvp.api.network.NetworkEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public interface AbstractActor extends NetworkEntity
{
    @NotNull String username();

    @NotNull Locale locale();

    boolean isOp();
}
