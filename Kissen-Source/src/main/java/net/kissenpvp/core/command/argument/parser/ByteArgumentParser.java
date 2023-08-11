package net.kissenpvp.core.command.argument.parser;

import net.kissenpvp.core.api.command.ArgumentParser;
import org.jetbrains.annotations.NotNull;

public class ByteArgumentParser implements ArgumentParser<Byte> {
    @Override
    public @NotNull String serialize(@NotNull Byte object) {
        return String.valueOf(object);
    }

    @Override
    public @NotNull Byte deserialize(@NotNull String input) {
        return Byte.parseByte(input);
    }
}
