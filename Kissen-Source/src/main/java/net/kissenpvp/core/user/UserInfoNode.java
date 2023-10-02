package net.kissenpvp.core.user;

import net.kissenpvp.core.api.user.UserInfo;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Represents the UserInfoNode record with two non-nullable fields: uuid and name.
 * <p>
 * The {@link UserInfoNode} record stores information related to a user, including their unique ID (UUID) and their name,
 * both of which are guaranteed to be non-null. Any operations involving this record that may potentially involve null values should be
 * designed with this constraint in mind and ensure that null values are never assigned to these fields.
 *
 * @param uuid unique user id. Never null. Represents the unique identifier for the user. This is typically a universally unique identifier (UUID).
 * @param name non-nullable username. Represents the name of the user as a string. This name may be a full name, a username, or another identifier provided by the user, but it can't be null.
 */
public record UserInfoNode(@NotNull UUID uuid, @NotNull String name) {

    /**
     * Returns an instance of {@link UserInfo}, which includes the user's unique identifier (UUID) and name.
     * <p>
     * The {@link UUID} and {@link String} name are encapsulated within an anonymous inner class that implements the {@link UserInfo} interface.
     * The UUID and name values are carried forward from the enclosing record's fields.
     * <p>
     * Note that the returned {@link UserInfo} is always a new instance, ensuring that external changes to the returned object
     * do not modify the original data stored in the {@link UserInfoNode} record.
     *
     * @return a new {@link UserInfo} instance that encapsulates the user's UUID and name information.
     */
    public @NotNull UserInfo getUserInfo() {
        return new UserInfo() {
            @Override
            public @NotNull UUID getUUID() {
                return uuid;
            }

            @Override
            public @NotNull String getName() {
                return name;
            }
        };
    }
}
