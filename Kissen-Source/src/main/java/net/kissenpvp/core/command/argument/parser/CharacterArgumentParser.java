package net.kissenpvp.core.command.argument.parser;

import net.kissenpvp.core.api.command.ArgumentParser;
import org.jetbrains.annotations.NotNull;

public class CharacterArgumentParser implements ArgumentParser<Character> {
    @Override
    public @NotNull String serialize(@NotNull Character object) {
        return String.valueOf(object);
    }

    @Override
    public @NotNull Character deserialize(@NotNull String input) {
        return input.charAt(0);
    }
}
