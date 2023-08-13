package net.kissenpvp.core.command.argument;

import lombok.Builder;
import net.kissenpvp.core.api.command.ArgumentParser;

@Builder
public record Argument<T, S>(String name, Class<?> type, T defaultValue,
                          ArgumentParser<T, S> argumentParser, boolean isNullable, ArgumentType argumentType,
                          boolean ignoreQuote) {
}
