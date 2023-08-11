package net.kissenpvp.core.command.argument.parser;

import net.kissenpvp.core.api.command.ArgumentParser;
import org.jetbrains.annotations.NotNull;

public class LongArgumentParser implements ArgumentParser<Long> {
    @Override
    public @NotNull String serialize(@NotNull Long object) {
        return String.valueOf(object);
    }

    @Override
    public @NotNull Long deserialize(@NotNull String input) {
        return Long.parseLong(input);
    }
}
