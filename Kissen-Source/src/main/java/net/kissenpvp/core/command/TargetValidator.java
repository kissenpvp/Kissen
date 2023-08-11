package net.kissenpvp.core.command;

import net.kissenpvp.core.api.command.CommandTarget;
import net.kissenpvp.core.api.networking.client.entitiy.ServerEntity;
import org.jetbrains.annotations.NotNull;

public interface TargetValidator {

    boolean validate(@NotNull CommandTarget commandTarget, @NotNull ServerEntity sender);

    @NotNull CommandTarget parseSender(@NotNull ServerEntity sender);

}
