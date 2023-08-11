package net.kissenpvp.core.command.argument.parser;

import net.kissenpvp.core.api.command.ArgumentParser;
import org.jetbrains.annotations.NotNull;

public class IntegerArgumentParser implements ArgumentParser<Integer> {
    @Override
    public @NotNull String serialize(@NotNull Integer object) {
        return String.valueOf(object);
    }

    @Override
    public @NotNull Integer deserialize(@NotNull String input) {
        return Integer.parseInt(input);
    }
}
