package net.kissenpvp.core.api.command.exception;

import org.jetbrains.annotations.NotNull;

public class IllegalReturnValueException extends IllegalTypeException {
    public IllegalReturnValueException(@NotNull Class<?> value, @NotNull Class<?>... allowed) {
        super(value, allowed);
    }
}
