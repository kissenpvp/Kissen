package net.kissenpvp.core.api.user.exception;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * The UnauthorizedException class is a custom exception class that extends IllegalAccessException.
 * UnauthorizedException indicates that a particular user doesn't have the required authorization or permission to access a resource or perform a certain action.
 * This exception class stores the UUID of the user and the string representation of the permission that the user lacks.
 */
public class UnauthorizedException extends IllegalAccessException {
    private final UUID user;
    private final String permission;

    /**
     * This constructor takes a user's UUID and a string representation of a permission as parameters.
     * It creates a new instance of UnauthorizedException using the provided parameters.
     *
     * @param user a UUID that represents user's unique identifier. It should not be null.
     * @param permission a string that represents the lacking permission. It should not be null.
     */
    public UnauthorizedException(@NotNull UUID user, @NotNull String permission) {
        this.user = user;
        this.permission = permission;
    }

    /**
     * This constructor takes a custom message, a user's UUID and a string representation of a permission as parameters.
     * It creates a new instance of UnauthorizedException using the provided parameters.
     *
     * @param message a string that contains a customized exception message.
     * @param user a UUID that represents user's unique identifier. It should not be null.
     * @param permission a string that represents the permission that the user lacks. It should not be null.
     */
    public UnauthorizedException(@NotNull String message, @NotNull UUID user, @NotNull String permission) {
        super(message);
        this.user = user;
        this.permission = permission;
    }

    /**
     * This getUser method is used to retrieve the UUID of the user associated with this UnauthorizedException.
     *
     * @return a UUID representing the unique identifier of the user.
     */
    public @NotNull UUID getUser() {
        return user;
    }

    /**
     * This getPermission method is used to retrieve the string representation of the permission that the user lacks,
     * which caused this UnauthorizedException to be thrown.
     *
     * @return a string representation of the permission that the user lacks.
     */
    public @NotNull String getPermission() {
        return permission;
    }
}
