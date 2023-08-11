package net.kissenpvp.core.api.command.exception;

import org.jetbrains.annotations.NotNull;

public class IllegalParameterException extends IllegalTypeException {
    public IllegalParameterException(@NotNull Class<?> value, @NotNull Class<?>... allowed) {
        super(value, allowed);
    }
}
