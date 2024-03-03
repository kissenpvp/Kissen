package net.kissenpvp.core.api.command.exception;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class ExecutionException extends RuntimeException {

    private final Component component;

    public ExecutionException(@NotNull Component component) {
        this.component = component;
    }

    public ExecutionException(@NotNull Component component, @NotNull Throwable cause) {
        super(cause.getMessage(), cause);
        this.component = component;
    }

    public ExecutionException(@NotNull Component component, @NotNull Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(cause.getMessage(), cause, enableSuppression, writableStackTrace);
        this.component = component;
    }

    public @NotNull Component getComponent() {
        return component;
    }
}
