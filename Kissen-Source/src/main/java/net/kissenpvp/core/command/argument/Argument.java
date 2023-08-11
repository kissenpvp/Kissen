package net.kissenpvp.core.command.argument;

import lombok.Builder;
import net.kissenpvp.core.api.command.ArgumentParser;

@Builder
public record Argument<T>(String name, Class<?> type, T defaultValue,
                          ArgumentParser<T> argumentParser, boolean isNullable, boolean isArray,
                          boolean ignoreQuote) {
}
