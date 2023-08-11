package net.kissenpvp.core.api.command.exception;


import org.jetbrains.annotations.NotNull;

public class UnauthorizedException extends IllegalAccessException {

    private final String permission;

    public UnauthorizedException(@NotNull String permission) {
        this.permission = permission;
    }

    public @NotNull String getPermission() {
        return permission;
    }
}
