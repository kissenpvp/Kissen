package net.kissenpvp.core.command.argument.parser;

import net.kissenpvp.core.api.command.ArgumentParser;
import org.jetbrains.annotations.NotNull;

public class ShortArgumentParser implements ArgumentParser<Short> {
    @Override
    public @NotNull String serialize(@NotNull Short object) {
        return String.valueOf(object);
    }

    @Override
    public @NotNull Short deserialize(@NotNull String input) {
        return Short.parseShort(input);
    }
}
