package net.kissenpvp.core.api.base;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface ExceptionHandler<T extends Throwable> {

    boolean handle(@NotNull T throwable);

}
