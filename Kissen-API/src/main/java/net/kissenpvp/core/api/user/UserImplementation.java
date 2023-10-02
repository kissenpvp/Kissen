/*
 * Copyright (C) 2023 KissenPvP
 *
 * This program is licensed under the Apache License, Version 2.0.
 *
 * This software may be redistributed and/or modified under the terms
 * of the Apache License as published by the Apache Software Foundation,
 * either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the Apache
 * License, Version 2.0 for the specific language governing permissions
 * and limitations under the License.
 *
 * You should have received a copy of the Apache License, Version 2.0
 * along with this program. If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

package net.kissenpvp.core.api.user;

import net.kissenpvp.core.api.base.Implementation;
import net.kissenpvp.core.api.database.meta.BackendException;
import net.kissenpvp.core.api.networking.client.entitiy.PlayerClient;
import net.kissenpvp.core.api.user.usersetttings.PlayerSetting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * The `UserImplementation` interface represents the implementation of a system component responsible for managing
 * user accounts.
 * It extends the `Implementation` interface, providing additional methods and declarations for user management
 * operations.
 *
 * <p>The `UserImplementation` interface serves as a contract for implementing classes to define the behavior and
 * functionality
 * related to user management in a specific system or application. It encapsulates the logic and operations necessary
 * for
 * creating, retrieving as well as other user management functionality.
 *
 * <p>Implementations of the `UserImplementation` interface should provide concrete implementations for all the
 * methods defined
 * in the `Implementation` interface, along with any additional methods and declarations specific to user management
 * operations.
 *
 * <p>Developers should implement the `UserImplementation` interface when creating a user management system or
 * integrating
 * user account functionality into an existing system. By implementing this interface, developers can define the
 * behavior and
 * implementation details specific to user management operations, ensuring that user accounts are properly created,
 * maintained,
 * and secured.
 *
 * <p>Implementing classes should ensure thread safety, data integrity, and handle any necessary data persistence or
 * integration
 * with external systems to fulfill the user management operations. They should provide efficient and reliable
 * methods for
 * creating, retrieving, updating, and deleting user accounts, as well as any other functionality required to manage
 * user
 * accounts within the system.
 */
public interface UserImplementation extends Implementation {
    /**
     * Returns a set of all users that are currently cached because they are online on the network.
     *
     * <p>This method retrieves a {@link Set} of {@link User} objects that represent the users currently online on
     * the network.
     * These user objects contain important information about the player stats on the server. The returned set serves
     * as an
     * interface to the associated table and provides a convenient way to access and interact with the online users'
     * data.
     * The returned set is unmodifiable to maintain data integrity and prevent unauthorized modifications.
     *
     * @return A {@link Set} of {@link User} objects representing the users currently online on the network. The set
     * contains
     * all the online users and allows access to their respective data and statistics.
     */
    @NotNull @Unmodifiable Set<User> getOnlineUser();

    /**
     * Returns the user associated with the specified UUID if they are currently online on the network.
     *
     * <p>This method retrieves the user object for the player identified by the given UUID. It is important to note
     * that the player
     * must be currently online on the network for this method to return a non-null value. If the player is not
     * online, the method
     * will return {@code null}. This method is optimized for performance, as it avoids unnecessary loading of users
     * who are not
     * currently active on the network.
     *
     * @param uuid The UUID of the player to retrieve. It should be a non-null {@link UUID} representing the unique
     *             identifier of the
     *             player. The UUID is used to identify the player in the network's system. Examples of valid UUIDs
     *             can be
     *             "c065ab21-3a43-4f8e-80df-9d6833f30e75", "a1f6be9b-87a0-47e2-b1b0-4cbf26e1693d", etc.
     * @return An {@link Optional} containing the {@link User} object associated with the specified UUID if the
     * player is
     * currently online, or an empty {@link Optional} if the player is not online.
     * @see #getUser(UUID)
     */
    @NotNull Optional<User> getOnlineUser(@NotNull UUID uuid);

    /**
     * Checks whether a player with the specified UUID has a valid user account in the network's table.
     *
     * <p>
     * This method verifies if the player identified by the given UUID has a valid user account in the associated
     * table of the network.
     * It should be noted that having an account does not necessarily imply that the player has actively played or
     * participated in the network.
     * Accounts can be created through various means, such as banning the user or assigning permissions.
     * Therefore, it is not safe to assume that the player has actually played before solely based on the account's
     * existence.
     * </p>
     *
     * <p>
     * If you need to determine if a player has never played before, it is suggested to perform two separate checks:
     * first, use the {@code isValid(UUID)} method to validate the account's existence, and then check the player's
     * online time.
     * For example: {@code isValid(UUID)} and {@link PlayerClient#getOnlineTime()} &gt; 0.
     * This approach ensures greater reliability when determining if a player has played at least once.
     * </p>
     *
     * @param uuid the UUID of the player to check.
     *             It should be a non-null {@link UUID} representing the unique identifier of the player.
     *             The UUID is used to identify the player in the network's system.
     *             Examples of valid UUIDs can be "c065ab21-3a43-4f8e-80df-9d6833f30e75",
     *             "a1f6be9b-87a0-47e2-b1b0-4cbf26e1693d", etc.
     * @return {@code true} if the player with the specified UUID has a valid account in the network's table, {@code
     * false} otherwise.
     * @throws Exception if an error occurs while executing the database query.
     *                   In such cases, a {@link Exception} will be thrown to indicate the failure.
     * @see #isValid(String)
     * @see PlayerClient#getOnlineTime()
     */
    boolean isValid(@NotNull UUID uuid);

    /**
     * Checks whether a player with the specified name has a valid user account in the network's table.
     *
     * <p>
     * This method verifies if the given player name has a valid user account in the associated table of the network.
     * It should be noted that having an account does not necessarily imply that the player has actively played or
     * participated in the network.
     * Accounts can be created through various means, such as banning the user or assigning permissions.
     * Therefore, it is not safe to assume that the player has actually played before solely based on the account's
     * existence.
     * </p>
     *
     * <p>
     * It is generally recommended to use the player's UUID instead of their name when checking account validity,
     * as names can change while UUIDs remain constant. Therefore, the method {@link #isValid(UUID)} provides a more
     * reliable approach.
     * </p>
     *
     * <p>
     * If you need to determine if a player has never played before, it is suggested to perform two separate checks:
     * first, use the {@code isValid(String)} method to validate the account's existence, and then check the player's
     * online time.
     * For example: {@code isValid(String)} and {@link PlayerClient#getOnlineTime()} &gt; 0.
     * This approach ensures greater reliability when determining if a player has played at least once.
     * </p>
     *
     * @param name the name of the user to check.
     *             It should be a non-null {@link String} representing the player's name.
     *             The player name is used to identify the account in the network's table.
     * @return {@code true} if the player name has a valid account in the network's table, {@code false} otherwise.
     * @throws Exception if an error occurs while executing the database query.
     *                   In such cases, a {@link Exception} will be thrown to indicate the failure.
     * @see #isValid(UUID)
     * @see PlayerClient#getOnlineTime()
     */
    boolean isValid(@NotNull String name);

    /**
     * Returns a user account based on the provided UUID. If the user is not currently online, their account data
     * will be
     * loaded from the table. If the user has never played before, a new account will be created.
     *
     * <p>This method can have performance implications when creating multiple profiles at once. It is recommended to
     * load
     * profiles asynchronously whenever possible.
     *
     * <p>When the player is online, their account will be retrieved from the `getOnlineUser(UUID)` method, which saves
     * resources.
     *
     * @param uuid The UUID of the player to retrieve the user account for.
     * @return The user account, which depends on whether the player is online.
     * @see #getOnlineUser(UUID)
     */
    @NotNull User getUser(@NotNull UUID uuid);

    @NotNull User getUser(@NotNull String name) throws BackendException;


    /**
     * Returns the names of all players who have ever played on the server.
     *
     * <p>This method retrieves a set of usernames belonging to players who have played on the network before. The
     * list is always
     * up-to-date and avoids the need for database access, providing a convenient way to access player names.
     *
     * <p>It is recommended to avoid saving player names in the future to reduce storage requirements and potential
     * privacy concerns.
     * Instead, consider using other unique identifiers like UUIDs to identify players.
     *
     * @return A set of all usernames who have played on the network before.
     * @see User
     */
    @Unmodifiable @NotNull Set<UserInfo> getUserProfiles();

    @Unmodifiable @NotNull Optional<UserInfo> getCachedUserProfile(@NotNull String name);

    @Unmodifiable @NotNull Optional<UserInfo> getCachedUserProfile(@NotNull UUID uuid);

    /**
     * Registers a {@link PlayerSetting} that represents a user-configurable setting, such as the primary theme color.
     * These settings can be accessed using {@link PlayerClient#getUserSetting(Class)} to retrieve the user's settings.
     * If a setting with the same key already exists, the old setting will be deleted.
     *
     * <p>Note that this method should generally be called on server start and is not designed to inject new settings
     * while
     * the server is running.
     *
     * @param playerSetting The player setting to register, which provides information about the setting that the
     *                      user can control.
     * @param <T>           The type of the setting that can be adjusted.
     * @return A set containing all player settings that were deleted while adding this setting.
     */
    <T> @Unmodifiable @NotNull Set<PlayerSetting<?>> registerUserSetting(@NotNull PlayerSetting<T> playerSetting);

    /**
     * Retrieves all user settings that have been registered.
     *
     * <p>This method returns a set of player settings that have been registered in the user management system. These
     * settings
     * represent user-configurable options that users can adjust, such as the primary theme color or notification
     * preferences.
     *
     * <p>The returned set is unmodifiable to ensure that the registered user settings cannot be modified directly.
     * Any changes
     * to user settings should be done through appropriate methods provided by the user management system
     * implementation.
     *
     * @return A set of player settings that have been registered.
     * @throws UnsupportedOperationException if the operation to retrieve user settings is not supported by the
     *                                       implementation.
     * @see PlayerSetting
     * @see #registerUserSetting(PlayerSetting)
     */
    @NotNull @Unmodifiable Set<PlayerSetting<?>> getUserSettings();

}
