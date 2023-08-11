package net.kissenpvp.core.api.command;

import net.kissenpvp.core.api.base.serializer.TextSerializer;
import org.jetbrains.annotations.NotNull;

public interface ArgumentParser<T> extends TextSerializer<T> {

    @Override
    @NotNull T deserialize(@NotNull String input);

    default void processError(@NotNull String string, @NotNull Exception exception) {}
}
