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
 * The {@link AbstractBanImplementation} interface defines the methods that a controller for managing player bans should implement.
 * It extends the {@link Implementation} interface, which specifies that it is an implementation of a system component.
 * <p>
 * The {@link AbstractBanImplementation} interface provides methods for creating and retrieving player bans.
 * Bans can be created with either a map of ban properties or with individual parameters for the ban's reason, type, and duration.
 * Bans can be retrieved either individually by ID or as an unmodifiable set of all current bans.
 * <p>
 * This interface is designed to be implemented by a class that manages player bans for a system.
 * It can be used by other components of the system to interact with the ban controller and enforce bans on players.
 * <p>
 * This interface should be used in conjunction with the {@link AbstractBan} class, which represents a single ban and provides methods for modifying or deleting the ban.
 *
 * @see Implementation
 * @see AbstractBan
 * @see AbstractPunishment
 * @see BanType
 */
public interface AbstractBanImplementation<B extends AbstractBan, P extends AbstractPunishment<?>> extends Implementation {
    
    /**
     * Returns an unmodifiable set containing all bans created using {@link #createBan(int, Map)} or {@link #createBan(int, String, BanType, AccurateDuration)}.
     * This set cannot be modified. To remove a {@link AbstractBan} from this set, use {@link AbstractBan#delete()} instead.
     *
     * @return An unmodifiable {@link Set} containing all bans.
     * @see AbstractBan
     * @see #createBan(int, Map)
     * @see #createBan(int, String, BanType, AccurateDuration)
     * @see AbstractBan#delete()
     */
    @NotNull @Unmodifiable Set<B> getBanSet();

    /**
     * Returns an {@link Optional} containing a previously created ban with the given ID using {@link #createBan(int, Map)} or {@link #createBan(int, String, BanType, AccurateDuration)}.
     * <p>
     * If {@link Optional#isEmpty()} returns {@code true}, a {@link AbstractBan} with the given ID does not exist in the system and can be added using {@link #createBan(int, Map)} or {@link #createBan(int, String, BanType, AccurateDuration)}.
     * <p>
     * To get all available {@link AbstractBan}, use the method {@link #getBanSet()} which returns an unmodifiable set containing all {@link AbstractBan}.
     *
     * @param id The ID of the ban to return.
     * @return An {@link Optional} containing the {@link AbstractBan} with the given ID, or an empty one if no such {@link AbstractBan} exists.
     * @see #createBan(int, Map)
     * @see #createBan(int, String, BanType, AccurateDuration)
     * @see #getBanSet()
     */
    @NotNull Optional<B> getBan(int id);

    /**
     * Creates a new ban with the given ID, name, and type.
     * The ban is returned as a {@link AbstractBan} object. If a ban with the same ID already exists, it will be overridden.
     * The newly created ban is saved within the database.
     *
     * @param id       The ID of the new ban.
     * @param name     The name of the ban.
     * @param banType  The type of the ban.
     * @return The new {@link AbstractBan}.
     * @throws NullPointerException if either name or banType is null.
     * @see AbstractBan
     */
    @NotNull B createBan(int id, @NotNull String name, @NotNull BanType banType);

    /**
     * Creates a new ban with the given ID, name, type, and duration. The ban is returned as a {@link AbstractBan} object.
     *
     * @param id       The ID of the new ban.
     * @param name     The name of the player being banned.
     * @param banType  The type of the ban.
     * @param accurateDuration The duration of the ban, or null for permanent bans.
     * @return The new {@link AbstractBan}.
     * @throws NullPointerException if the name or banType is null.
     * @see AbstractBan
     */
    @NotNull B createBan(int id, @NotNull String name, @NotNull BanType banType, @Nullable AccurateDuration accurateDuration);

    /**
     * Applies the specified {@link AbstractBan} to the player with the given UUID, without providing a reason for the ban.
     * The team member who applies the ban is identified by the given banner.
     * <p>
     * It is recommended to use {@link #punish(UUID, AbstractBan, ServerEntity, Component)} to provide a reason for the ban.
     * </p>
     * <p>
     * Depending on the {@link AbstractBan}, this punishment will be executed immediately and will affect all players who have the same {@link PlayerClient#getTotalID()} as the provided UUID.
     * </p>
     *
     * @param totalID     the UUID of the player to be banned.
     * @param ban         the {@link AbstractBan} object containing details of the ban to be applied.
     * @param banOperator the name of the team member who is applying the ban.
     * @return a {@link AbstractPunishment} object representing the newly applied ban.
     * @throws NullPointerException if either totalID, ban or banner are null.
     * @see AbstractBan
     * @see AbstractPunishment
     * @see #punish(UUID, AbstractBan, ServerEntity, Component)
     */
    @NotNull P punish(@NotNull UUID totalID, @NotNull B ban, @NotNull ServerEntity banOperator) ;

    /**
     * Applies the specified {@link AbstractBan} to the player identified by the given {@code totalID} and returns a {@link AbstractPunishment} object that represents the newly applied ban.
     * If a reason for the ban is provided, it will be included in the {@link AbstractPunishment} object for future reference.
     * <p>
     * If no reason is given, use {@link #punish(UUID, AbstractBan, ServerEntity)} instead.
     * </p>
     * <p>
     * The punishment specified by the {@link AbstractBan} object will be executed immediately and will affect all players who have the same {@link PlayerClient#getTotalID()} as the one provided.
     * </p>
     *
     * @param totalID     the UUID of the player to be banned
     * @param ban         the {@link AbstractBan} object containing details of the ban to be applied
     * @param banOperator the name of the team member who is applying the ban
     * @param reason      an optional reason for the ban, which will be included in the {@link AbstractPunishment} object
     * @return a {@link AbstractPunishment} object representing the newly applied ban
     * @throws NullPointerException if either totalID, ban, or banner is null
     * @see AbstractBan
     * @see AbstractPunishment
     * @see #punish(UUID, AbstractBan, ServerEntity)
     */
    @NotNull P punish(@NotNull UUID totalID, @NotNull B ban, @NotNull ServerEntity banOperator, @Nullable Component reason);

    /**
     * Applies the specified {@link AbstractBan} to the player identified by the given {@code totalID} and returns a {@link AbstractPunishment} object that represents the newly applied ban.
     * This method allows controlling whether to apply the punishment immediately or not.
     *
     * @param totalID     the UUID of the player to be banned
     * @param ban         the {@link AbstractBan} object containing details of the ban to be applied
     * @param banOperator the name of the team member who is applying the ban
     * @param apply       a boolean indicating whether to apply the punishment immediately
     * @return a {@link AbstractPunishment} object representing the newly applied ban
     * @throws NullPointerException if either totalID, ban, or banOperator is null
     * @see AbstractBan
     * @see AbstractPunishment
     * @see #punish(UUID, AbstractBan, ServerEntity)
     */
    @NotNull P punish(@NotNull UUID totalID, @NotNull B ban, @NotNull ServerEntity banOperator, boolean apply);

    /**
     * Applies the specified {@link AbstractBan} to the player identified by the given {@code totalID} and returns a {@link AbstractPunishment} object that represents the newly applied ban.
     * This method allows controlling whether to apply the punishment immediately or not, and includes an optional reason for the ban.
     *
     * @param totalID     the UUID of the player to be banned
     * @param ban         the {@link AbstractBan} object containing details of the ban to be applied
     * @param banOperator the name of the team member who is applying the ban
     * @param apply       a boolean indicating whether to apply the punishment immediately
     * @param reason      an optional reason for the ban, which will be included in the {@link AbstractPunishment} object
     * @return a {@link AbstractPunishment} object representing the newly applied ban
     * @throws NullPointerException if either totalID, ban, or banOperator is null
     * @see AbstractBan
     * @see AbstractPunishment
     * @see #punish(UUID, AbstractBan, ServerEntity)
     */
    @NotNull P punish(@NotNull UUID totalID, @NotNull B ban, @NotNull ServerEntity banOperator, boolean apply, @Nullable Component reason);

    /**
     * Returns the most recent valid ban associated with the player having the specified {@link PlayerClient#getTotalID()}.
     * If no valid bans are found, an empty {@link Optional} is returned.
     *
     * <p>
     * Bans can be applied to players using {@link #punish(UUID, AbstractBan, ServerEntity, Component)} or {@link #punish(UUID, AbstractBan, ServerEntity)}.
     * </p>
     *
     * <p>
     * To filter for a specific {@link BanType}, use the {@link #getLatestPunishment(UUID, BanType)} method instead.
     * </p>
     *
     * @param totalID the UUID of the player for whom to search for bans.
     * @return an {@link Optional} containing the most recent {@link AbstractPunishment} object representing a valid ban for the specified player,
     * or an empty {@link Optional} if no valid bans are found.
     * @throws NullPointerException if {@code totalID} is null.
     * @see AbstractPunishment#isValid()
     * @see #punish(UUID, AbstractBan, ServerEntity, Component)
     * @see #punish(UUID, AbstractBan, ServerEntity)
     * @see #getLatestPunishment(UUID, BanType)
     */
    @NotNull Optional<P> getLatestPunishment(@NotNull UUID totalID) ;

    /**
     * Returns the most recent valid ban of the specified type associated with the player having the specified {@link PlayerClient#getTotalID()}.
     * If no valid bans of the specified type are found, an empty {@link Optional} is returned.
     *
     * <p>
     * Bans can be applied to players using {@link #punish(UUID, AbstractBan, ServerEntity, Component)} or {@link #punish(UUID, AbstractBan, ServerEntity)}.
     * </p>
     *
     * <p>
     * If no specific {@link BanType} is needed, use the method {@link #getLatestPunishment(UUID)} instead.
     * </p>
     *
     * @param totalID the UUID of the player for whom to search for bans.
     * @param banType the type of ban to search for.
     * @return an {@link Optional} containing the most recent {@link AbstractPunishment} object representing a valid ban of the specified type for the specified player.
     * Returns an empty {@link Optional} if no valid bans of the specified type are found.
     * @throws NullPointerException if {@code totalID} or {@code banType} are null.
     * @see AbstractPunishment#isValid()
     * @see #getLatestPunishment(UUID)
     * @see #punish(UUID, AbstractBan, ServerEntity, Component)
     * @see #punish(UUID, AbstractBan, ServerEntity)
     */
    @NotNull Optional<P> getLatestPunishment(@NotNull UUID totalID, @NotNull BanType banType) ;

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
     * @return an unmodifiable {@link Set} of all the {@link AbstractPunishment} objects associated with the specified player.
     * @throws NullPointerException if {@code totalID} is null.
     * @see #getLatestPunishment(UUID)
     * @see #getLatestPunishment(UUID, BanType)
     */
    @NotNull @Unmodifiable Set<P> getPunishmentSet(@NotNull UUID totalID) ;

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
     * @return an unmodifiable {@link Set} of all the {@link AbstractPunishment} objects associated with any player.
     * @see #getPunishmentSet(UUID)
     * @see #getLatestPunishment(UUID)
     * @see #getLatestPunishment(UUID, BanType)
     */
    @NotNull @Unmodifiable Set<P> getPunishmentSet() ;

}
