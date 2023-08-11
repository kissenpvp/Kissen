package net.kissenpvp.core.command.argument.parser;

import net.kissenpvp.core.api.command.ArgumentParser;
import org.jetbrains.annotations.NotNull;

public class FloatArgumentParser implements ArgumentParser<Float> {
    @Override
    public @NotNull String serialize(@NotNull Float object) {
        return String.valueOf(object);
    }

    @Override
    public @NotNull Float deserialize(@NotNull String input) {
        return Float.parseFloat(input);
    }
}
