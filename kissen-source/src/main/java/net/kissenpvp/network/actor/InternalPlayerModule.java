package net.kissenpvp.network.actor;

import net.kissenpvp.api.database.Repository;
import net.kissenpvp.api.network.actor.OperatorInfo;
import net.kissenpvp.api.network.actor.PlayerModule;
import net.kissenpvp.api.network.actor.PlayerRepository;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record InternalPlayerModule(@NotNull PlayerRepository playerRepository, @NotNull Repository<UUID, OperatorInfo> operatorRepository) implements PlayerModule {}
