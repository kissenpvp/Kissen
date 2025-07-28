package net.kissenpvp.network.actor;

import net.kissenpvp.api.network.actor.PlayerModule;
import net.kissenpvp.api.network.actor.PlayerRepository;
import org.jetbrains.annotations.NotNull;

public record InternalPlayerModule(@NotNull PlayerRepository playerRepository) implements PlayerModule {}
