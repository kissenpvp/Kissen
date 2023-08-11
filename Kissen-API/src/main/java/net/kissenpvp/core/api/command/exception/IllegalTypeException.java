package net.kissenpvp.core.api.command.exception;

import org.jetbrains.annotations.NotNull;

public class IllegalTypeException extends IllegalStateException {

    private final Class<?> value;
    private final Class<?>[] allowed;

    public IllegalTypeException(@NotNull Class<?> value, @NotNull Class<?>... allowed) {
        super(new IllegalStateException());
        this.value = value;
        this.allowed = allowed;
    }

    public Class<?> getValue() {
        return value;
    }

    public Class<?>[] getAllowed() {
        return allowed;
    }
}
