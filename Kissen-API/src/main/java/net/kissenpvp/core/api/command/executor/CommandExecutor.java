package net.kissenpvp.core.api.command.executor;

import net.kissenpvp.core.api.command.CommandPayload;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface CommandExecutor<SENDER> {

    boolean execute(@NotNull CommandPayload<SENDER> commandPayload);

}
