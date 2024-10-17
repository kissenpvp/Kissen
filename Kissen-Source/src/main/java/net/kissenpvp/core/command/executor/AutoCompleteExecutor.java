package net.kissenpvp.core.command.executor;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kissenpvp.core.api.command.CommandPayload;
import net.kissenpvp.core.api.command.executor.TabCompleterExecutor;
import net.kissenpvp.core.api.networking.client.entitiy.ServerEntity;
import net.kissenpvp.core.command.CommandHolder;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

@RequiredArgsConstructor @Getter(AccessLevel.PROTECTED)
public class AutoCompleteExecutor<S extends ServerEntity> implements TabCompleterExecutor<S> {

    private final CommandHolder<S, ?> parent;

    @Override
    public @NotNull Collection<String> execute(@NotNull CommandPayload<S> commandPayload) {
        int index = commandPayload.getArgumentCount();
        //TODO
        return Collections.emptyList();
    }
}
