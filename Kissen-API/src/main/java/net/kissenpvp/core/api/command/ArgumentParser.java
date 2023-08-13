package net.kissenpvp.core.api.command;

import net.kissenpvp.core.api.base.serializer.TextSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

public interface ArgumentParser<T, S> extends TextSerializer<T> {

    @Override
    @NotNull T deserialize(@NotNull String input);

    default @NotNull Collection<String> tabCompletion(@NotNull CommandPayload<S> commandPayload) { return Collections.emptySet(); };

    default void processError(@NotNull String string, @NotNull Exception exception) {}
}
