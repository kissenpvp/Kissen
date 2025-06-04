package net.kissenpvp.api.network.actor;

import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;

public interface MessageReceiver
{
    @NotNull Audience audience();
}
