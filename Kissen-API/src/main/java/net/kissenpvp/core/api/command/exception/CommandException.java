package net.kissenpvp.core.api.command.exception;

import org.jetbrains.annotations.NotNull;

public class CommandException extends RuntimeException {

    public CommandException(String message) {
        super(message);
    }

    public CommandException(@NotNull String message, @NotNull Throwable cause) {
        super(message, cause);
    }

    public CommandException(@NotNull Throwable cause) {
        super(cause);
    }
}
