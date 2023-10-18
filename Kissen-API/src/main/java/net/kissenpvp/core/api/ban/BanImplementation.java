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

package net.kissenpvp.core.api.ban;

import net.kissenpvp.core.api.base.Implementation;
import net.kissenpvp.core.api.database.meta.BackendException;
import net.kissenpvp.core.api.networking.client.entitiy.PlayerClient;
import net.kissenpvp.core.api.networking.client.entitiy.ServerEntity;
import net.kissenpvp.core.api.time.AccurateDuration;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

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
     * Returns an unmodifiable set containing all bans created using {@link #createBan(int, Map)} or {@link #createBan(int, String, BanType, AccurateDuration)}.
     * This set cannot be modified. To remove a {@link Ban} from this set, use {@link Ban#delete()} instead.
     *
     * @return An unmodifiable {@link Set} containing all bans.
     * @see Ban
     * @see #createBan(int, Map)
     * @see #createBan(int, String, BanType, AccurateDuration)
     * @see Ban#delete()
     */
    @NotNull @Unmodifiable Set<B> getBanSet();

    /**
     * Returns an {@link Optional} containing a previously created ban with the given ID using {@link #createBan(int, Map)} or {@link #createBan(int, String, BanType, AccurateDuration)}.
     * <p>
     * If {@link Optional#isEmpty()} returns {@code true}, a {@link Ban} with the given ID does not exist in the system and can be added using {@link #createBan(int, Map)} or {@link #createBan(int, String, BanType, AccurateDuration)}.
     * <p>
     * To get all available {@link Ban}, use the method {@link #getBanSet()} which returns an unmodifiable set containing all {@link Ban}.
     *
     * @param id The ID of the ban to return.
     * @return An {@link Optional} containing the {@link Ban} with the given ID, or an empty one if no such {@link Ban} exists.
     * @see #createBan(int, Map)
     * @see #createBan(int, String, BanType, AccurateDuration)
     * @see #getBanSet()
     */
    @NotNull Optional<B> getBan(int id);

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

    @NotNull B createBan(int id, @NotNull String name, @NotNull BanType banType) throws BackendException;

    /**
     * Creates a new ban with the given ID, name, type, and duration. The ban is returned as a {@link Ban} object.
     *
     * @param id       The ID of the new ban.
     * @param name     The name of the player being banned.
     * @param banType  The type of the ban.
     * @param accurateDuration The duration of the ban, or null for permanent bans.
     * @return The new {@link Ban}.
     * @throws NullPointerException if the name or banType is null.
     * @see Ban
     */
    @NotNull B createBan(int id, @NotNull String name, @NotNull BanType banType, @Nullable AccurateDuration accurateDuration) throws BackendException;

    /**
     * Applies the specified {@link Ban} to the player with the given UUID, without providing a reason for the ban.
     * The team member who applies the ban is identified by the given banner.
     * <p>
     * It is recommended to use {@link #punish(UUID, Ban, ServerEntity, Component)} to provide a reason for the ban.
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
     * @see #punish(UUID, Ban, ServerEntity, Component)
     */
    @NotNull P punish(@NotNull UUID totalID, @NotNull B ban, @NotNull ServerEntity banOperator) throws BackendException;

    /**
     * Applies the specified {@link Ban} to the player identified by the given {@code totalID} and returns a {@link Punishment} object that represents the newly applied ban.
     * If a reason for the ban is provided, it will be included in the {@link Punishment} object for future reference.
     * <p>
     * If no reason is given, use {@link #punish(UUID, Ban, ServerEntity)} instead.
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
     * @see #punish(UUID, Ban, ServerEntity)
     */
    @NotNull P punish(@NotNull UUID totalID, @NotNull B ban, @NotNull ServerEntity banOperator, @Nullable Component reason) throws BackendException;

    @NotNull P punish(@NotNull UUID totalID, @NotNull B ban, @NotNull ServerEntity banOperator, boolean apply) throws BackendException;

    @NotNull P punish(@NotNull UUID totalID, @NotNull B ban, @NotNull ServerEntity banOperator, boolean apply, @Nullable Component reason) throws BackendException;


    /**
     * Returns the most recent valid ban associated with the player having the specified {@link PlayerClient#getTotalID()}.
     * If no valid bans are found, an empty {@link Optional} is returned.
     *
     * <p>
     * Bans can be applied to players using {@link #punish(UUID, Ban, ServerEntity, Component)} or {@link #punish(UUID, Ban, ServerEntity)}.
     * </p>
     *
     * <p>
     * To filter for a specific {@link BanType}, use the {@link #getLatestPunishment(UUID, BanType)} method instead.
     * </p>
     *
     * @param totalID the UUID of the player for whom to search for bans.
     * @return an {@link Optional} containing the most recent {@link Punishment} object representing a valid ban for the specified player,
     * or an empty {@link Optional} if no valid bans are found.
     * @throws NullPointerException if {@code totalID} is null.
     * @see Punishment#isValid()
     * @see #punish(UUID, Ban, ServerEntity, Component)
     * @see #punish(UUID, Ban, ServerEntity)
     * @see #getLatestPunishment(UUID, BanType)
     */
    @NotNull Optional<P> getLatestPunishment(@NotNull UUID totalID) throws BackendException;

    /**
     * Returns the most recent valid ban of the specified type associated with the player having the specified {@link PlayerClient#getTotalID()}.
     * If no valid bans of the specified type are found, an empty {@link Optional} is returned.
     *
     * <p>
     * Bans can be applied to players using {@link #punish(UUID, Ban, ServerEntity, Component)} or {@link #punish(UUID, Ban, ServerEntity)}.
     * </p>
     *
     * <p>
     * If no specific {@link BanType} is needed, use the method {@link #getLatestPunishment(UUID)} instead.
     * </p>
     *
     * @param totalID the UUID of the player for whom to search for bans.
     * @param banType the type of ban to search for.
     * @return an {@link Optional} containing the most recent {@link Punishment} object representing a valid ban of the specified type for the specified player.
     * Returns an empty {@link Optional} if no valid bans of the specified type are found.
     * @throws NullPointerException if {@code totalID} or {@code banType} are null.
     * @see Punishment#isValid()
     * @see #getLatestPunishment(UUID)
     * @see #punish(UUID, Ban, ServerEntity, Component)
     * @see #punish(UUID, Ban, ServerEntity)
     */
    @NotNull Optional<P> getLatestPunishment(@NotNull UUID totalID, @NotNull BanType banType) throws BackendException;

    /**
     * Returns an unmodifiable set of all the bans associated with the player having the specified {@link PlayerClient#getTotalID()}.
     * These bans include both valid and invalid bans.
     * <p>
     * To get the most recent valid ban of a player, use the {@link #getLatestPunishment(UUID)} or the {@link #getLatestPunishment(UUID, BanType)} method.
     *
     * <p>
     * Note that this method returns an unmodifiable set, meaning that it cannot be modified directly.
     * Attempting to modify the set will result in an {@link UnsupportedOperationException} being thrown.
     * </p>
     *
     * @param totalID the UUID of the player for whom to retrieve the set of bans.
     * @return an unmodifiable {@link Set} of all the {@link Punishment} objects associated with the specified player.
     * @throws NullPointerException if {@code totalID} is null.
     * @see #getLatestPunishment(UUID)
     * @see #getLatestPunishment(UUID, BanType)
     */
    @NotNull @Unmodifiable Set<P> getPunishmentSet(@NotNull UUID totalID) throws BackendException;

    /**
     * Returns an unmodifiable set of all the bans associated with any player.
     * These bans include both valid and invalid bans.
     * <p>
     * To get the bans associated with a specific player, use the {@link #getPunishmentSet(UUID)} method.
     * To get the most recent valid ban of a player, use the {@link #getLatestPunishment(UUID)} or the {@link #getLatestPunishment(UUID, BanType)} method.
     *
     * <p>
     * Note that this method returns an unmodifiable set, meaning that it cannot be modified directly.
     * Attempting to modify the set will result in an {@link UnsupportedOperationException} being thrown.
     * </p>
     *
     * @return an unmodifiable {@link Set} of all the {@link Punishment} objects associated with any player.
     * @see #getPunishmentSet(UUID)
     * @see #getLatestPunishment(UUID)
     * @see #getLatestPunishment(UUID, BanType)
     */
    @NotNull @Unmodifiable Set<P> getPunishmentSet() throws BackendException;

}
