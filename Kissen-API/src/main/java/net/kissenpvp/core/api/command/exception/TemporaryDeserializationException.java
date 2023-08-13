package net.kissenpvp.core.api.command.exception;

import org.jetbrains.annotations.NotNull;

public class TemporaryDeserializationException extends CommandException {

    public TemporaryDeserializationException(@NotNull Throwable cause) {
        super("The provided argument of type <Type/Class Name> cannot be deserialized. Please ensure it conforms to the expected structure for deserialization.", cause);
    }
}
