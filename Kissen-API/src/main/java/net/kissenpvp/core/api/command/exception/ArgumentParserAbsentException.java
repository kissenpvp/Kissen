package net.kissenpvp.core.api.command.exception;

import org.jetbrains.annotations.NotNull;

public class ArgumentParserAbsentException extends CommandException {

    private final Class<?> argument;

    public ArgumentParserAbsentException(@NotNull Class<?> argument) {
        super(String.format("The argument '%s' does not have a valid parser.", argument.getName()), new IllegalStateException());
        this.argument = argument;
    }

    public Class<?> getArgument() {
        return argument;
    }
}
