package net.kissenpvp.core.api.command.exception;

import org.jetbrains.annotations.NotNull;

public class TemporaryDeserializationException extends CommandException {

    public TemporaryDeserializationException(@NotNull Throwable cause) {
        super("The argument is currently not deserializable.", cause);
    }
}
