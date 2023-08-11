package net.kissenpvp.core.api.command.executor;

import net.kissenpvp.core.api.command.CommandPayload;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

@FunctionalInterface
public interface TabCompleterExecutor<SENDER> {

    @NotNull Set<String> execute(@NotNull CommandPayload<SENDER> commandPayload);

}
