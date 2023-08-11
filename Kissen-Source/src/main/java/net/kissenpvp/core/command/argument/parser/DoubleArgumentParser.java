package net.kissenpvp.core.command.argument.parser;

import net.kissenpvp.core.api.command.ArgumentParser;
import org.jetbrains.annotations.NotNull;

public class DoubleArgumentParser implements ArgumentParser<Double> {
    @Override
    public @NotNull String serialize(@NotNull Double object) {
        return String.valueOf(object);
    }

    @Override
    public @NotNull Double deserialize(@NotNull String input) {
        return Double.parseDouble(input);
    }
}
