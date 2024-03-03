package net.kissenpvp.core.api.command.exception;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;


public class OperationException extends RuntimeException {

    private final Component component;

    public OperationException()
    {
        this(null);
    }

    public OperationException(@Nullable Component component) {
        this.component = component;
    }

    public OperationException(@Nullable Component component, @Nullable Throwable cause) {
        super(cause == null ? null : cause.getMessage(), cause);
        this.component = component;
    }

    public OperationException(@Nullable Component component, @Nullable Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(cause == null ? null : cause.getMessage(), cause, enableSuppression, writableStackTrace);
        this.component = component;
    }

    public @NotNull Optional<Component> getComponent() {
        return Optional.ofNullable(component);
    }
}
