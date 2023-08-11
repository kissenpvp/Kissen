package net.kissenpvp.core.command.argument.parser;

import net.kissenpvp.core.api.command.ArgumentParser;
import org.jetbrains.annotations.NotNull;

public class StringArgumentParser implements ArgumentParser<String> {
    @Override
    public @NotNull String serialize(@NotNull String object) {
        return object;
    }

    @Override
    public @NotNull String deserialize(@NotNull String input) {
        return input;
    }
}
