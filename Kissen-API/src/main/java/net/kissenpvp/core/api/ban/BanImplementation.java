/*
 * Copyright 2023 KissenPvP
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.kissenpvp.core.api.ban;

import net.kissenpvp.core.api.base.Implementation;
import net.kissenpvp.core.api.database.meta.BackendException;
import net.kissenpvp.core.api.networking.client.entitiy.PlayerClient;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * The {@link BanImplementation} interface defines the methods that a controller for managing player bans should implement.
 * It extends the {@link Implementation} interface, which specifies that it is an implementation of a system component.
 * <p>
 * The {@link BanImplementation} interface provides methods for creating and retrieving player bans.
 * Bans can be created with either a map of ban properties or with individual parameters for the ban's reason, type, and duration.
 * Bans can be retrieved either individually by ID or as an unmodifiable set of all current bans.
 * <p>
 * This interface is designed to be implemented by a class that manages player bans for a system.
 * It can be used by other components of the system to interact with the ban controller and enforce bans on players.
 * <p>
 * This interface should be used in conjunction with the {@link Ban} class, which represents a single ban and provides methods for modifying or deleting the ban.
 *
 * @see Implementation
 * @see Ban
 * @see Punishment
 * @see BanType
 */
public interface BanImplementation<B extends Ban, P extends Punishment<?>> extends Implementation {
    
    /**
     * Returns an unmodifiable set containing all bans created using {@link #createBan(int, Map)} or {@link #createBan(int, String, BanType, Duration)}.
     * This set cannot be modified. To remove a {@link Ban} from this set, use {@link Ban#delete()} instead.
     *
     * @return An unmodifiable {@link Set} containing all bans.
     * @see Ban
     * @see #createBan(int, Map)
     * @see #createBan(int, String, BanType, Duration)
     * @see Ban#delete()
     */
    @NotNull @Unmodifiable Set<B> getBanSet();

    /**
     * Returns an {@link Optional} containing a previously created ban with the given ID using {@link #createBan(int, Map)} or {@link #createBan(int, String, BanType, Duration)}.
     * <p>
     * If {@link Optional#isEmpty()} returns {@code true}, a {@link Ban} with the given ID does not exist in the system and can be added using {@link #createBan(int, Map)} or {@link #createBan(int, String, BanType, Duration)}.
     * <p>
     * To get all available {@link Ban}, use the method {@link #getBanSet()} which returns an unmodifiable set containing all {@link Ban}.
     *
     * @param id The ID of the ban to return.
     * @return An {@link Optional} containing the {@link Ban} with the given ID, or an empty one if no such {@link Ban} exists.
     * @see #createBan(int, Map)
     * @see #createBan(int, String, BanType, Duration)
     * @see #getBanSet()
     */
    @NotNull Optional<@Nullable B> getBan(int id);

    /**
     * Creates a new ban with the given ID and data. The data is a map of ban properties such as reason, type, and duration.
     * The ban is returned as a {@link Ban} object.
     *
     * @param id   The ID of the new ban.
     * @param data A map of ban properties.
     * @return The new {@link Ban}.
     * @throws NullPointerException if the data map is null.
     * @see Ban
     */
    @NotNull B createBan(int id, @NotNull Map<String, String> data) throws BackendException;

    /**
     * Creates a new ban with the given ID, name, type, and duration. The ban is returned as a {@link Ban} object.
     *
     * @param id       The ID of the new ban.
     * @param name     The name of the player being banned.
     * @param banType  The type of the ban.
     * @param duration The duration of the ban, or null for permanent bans.
     * @return The new {@link Ban}.
     * @throws NullPointerException if the name or banType is null.
     * @see Ban
     */
    @NotNull B createBan(int id, @NotNull String name, @NotNull BanType banType, @Nullable Duration duration) throws BackendException;

    /**
     * Applies the specified {@link Ban} to the player with the given UUID, without providing a reason for the ban.
     * The team member who applies the ban is identified by the given banner.
     * <p>
     * It is recommended to use {@link #ban(UUID, Ban, BanOperator, Component)} to provide a reason for the ban.
     * </p>
     * <p>
     * Depending on the {@link Ban}, this punishment will be executed immediately and will affect all players who have the same {@link PlayerClient#getTotalID()} as the provided UUID.
     * </p>
     *
     * @param totalID     the UUID of the player to be banned.
     * @param ban         the {@link Ban} object containing details of the ban to be applied.
     * @param banOperator the name of the team member who is applying the ban.
     * @return a {@link Punishment} object representing the newly applied ban.
     * @throws NullPointerException if either totalID, ban or banner are null.
     * @see Ban
     * @see Punishment
     * @see #ban(UUID, Ban, BanOperator, Component)
     */
    @NotNull P ban(@NotNull UUID totalID, @NotNull B ban, @NotNull BanOperator banOperator) throws BackendException;

    /**
     * Applies the specified {@link Ban} to the player identified by the given {@code totalID} and returns a {@link Punishment} object that represents the newly applied ban.
     * If a reason for the ban is provided, it will be included in the {@link Punishment} object for future reference.
     * <p>
     * If no reason is given, use {@link #ban(UUID, Ban, BanOperator)} instead.
     * </p>
     * <p>
     * The punishment specified by the {@link Ban} object will be executed immediately and will affect all players who have the same {@link PlayerClient#getTotalID()} as the one provided.
     * </p>
     *
     * @param totalID     the UUID of the player to be banned
     * @param ban         the {@link Ban} object containing details of the ban to be applied
     * @param banOperator the name of the team member who is applying the ban
     * @param reason      an optional reason for the ban, which will be included in the {@link Punishment} object
     * @return a {@link Punishment} object representing the newly applied ban
     * @throws NullPointerException if either totalID, ban, or banner is null
     * @see Ban
     * @see Punishment
     * @see #ban(UUID, Ban, BanOperator)
     */
    @NotNull P ban(@NotNull UUID totalID, @NotNull B ban, @NotNull BanOperator banOperator, @Nullable Component reason) throws BackendException;

    /**
     * Returns the most recent valid ban associated with the player having the specified {@link PlayerClient#getTotalID()}.
     * If no valid bans are found, an empty {@link Optional} is returned.
     *
     * <p>
     * Bans can be applied to players using {@link #ban(UUID, Ban, BanOperator, Component)} or {@link #ban(UUID, Ban, BanOperator)}.
     * </p>
     *
     * <p>
     * To filter for a specific {@link BanType}, use the {@link #getCurrentBan(UUID, BanType)} method instead.
     * </p>
     *
     * @param totalID the UUID of the player for whom to search for bans.
     * @return an {@link Optional} containing the most recent {@link Punishment} object representing a valid ban for the specified player,
     * or an empty {@link Optional} if no valid bans are found.
     * @throws NullPointerException if {@code totalID} is null.
     * @see Punishment#isValid()
     * @see #ban(UUID, Ban, BanOperator, Component)
     * @see #ban(UUID, Ban, BanOperator)
     * @see #getCurrentBan(UUID, BanType)
     */
    @NotNull Optional<@Nullable P> getCurrentBan(@NotNull UUID totalID) throws BackendException;

    /**
     * Returns the most recent valid ban of the specified type associated with the player having the specified {@link PlayerClient#getTotalID()}.
     * If no valid bans of the specified type are found, an empty {@link Optional} is returned.
     *
     * <p>
     * Bans can be applied to players using {@link #ban(UUID, Ban, BanOperator, Component)} or {@link #ban(UUID, Ban, BanOperator)}.
     * </p>
     *
     * <p>
     * If no specific {@link BanType} is needed, use the method {@link #getCurrentBan(UUID)} instead.
     * </p>
     *
     * @param totalID the UUID of the player for whom to search for bans.
     * @param banType the type of ban to search for.
     * @return an {@link Optional} containing the most recent {@link Punishment} object representing a valid ban of the specified type for the specified player.
     * Returns an empty {@link Optional} if no valid bans of the specified type are found.
     * @throws NullPointerException if {@code totalID} or {@code banType} are null.
     * @see Punishment#isValid()
     * @see #getCurrentBan(UUID)
     * @see #ban(UUID, Ban, BanOperator, Component)
     * @see #ban(UUID, Ban, BanOperator)
     */
    @NotNull Optional<@Nullable P> getCurrentBan(@NotNull UUID totalID, @NotNull BanType banType) throws BackendException;

    /**
     * Returns an unmodifiable set of all the bans associated with the player having the specified {@link PlayerClient#getTotalID()}.
     * These bans include both valid and invalid bans.
     * <p>
     * To get the most recent valid ban of a player, use the {@link #getCurrentBan(UUID)} or the {@link #getCurrentBan(UUID, BanType)} method.
     *
     * <p>
     * Note that this method returns an unmodifiable set, meaning that it cannot be modified directly.
     * Attempting to modify the set will result in an {@link UnsupportedOperationException} being thrown.
     * </p>
     *
     * @param totalID the UUID of the player for whom to retrieve the set of bans.
     * @return an unmodifiable {@link Set} of all the {@link Punishment} objects associated with the specified player.
     * @throws NullPointerException if {@code totalID} is null.
     * @see #getCurrentBan(UUID)
     * @see #getCurrentBan(UUID, BanType)
     */
    @NotNull @Unmodifiable Set<P> getPlayerBanSet(@NotNull UUID totalID) throws BackendException;

    /**
     * Returns an unmodifiable set of all the bans associated with any player.
     * These bans include both valid and invalid bans.
     * <p>
     * To get the bans associated with a specific player, use the {@link #getPlayerBanSet(UUID)} method.
     * To get the most recent valid ban of a player, use the {@link #getCurrentBan(UUID)} or the {@link #getCurrentBan(UUID, BanType)} method.
     *
     * <p>
     * Note that this method returns an unmodifiable set, meaning that it cannot be modified directly.
     * Attempting to modify the set will result in an {@link UnsupportedOperationException} being thrown.
     * </p>
     *
     * @return an unmodifiable {@link Set} of all the {@link Punishment} objects associated with any player.
     * @see #getPlayerBanSet(UUID)
     * @see #getCurrentBan(UUID)
     * @see #getCurrentBan(UUID, BanType)
     */
    @NotNull @Unmodifiable Set<P> getPlayerBanSet() throws BackendException;

}
