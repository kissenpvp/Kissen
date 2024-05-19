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
 * This interface should be used in conjunction with the {@link AbstractBanTemplate} class, which represents a single ban and provides methods for modifying or deleting the ban.
 *
 * @see Implementation
 * @see AbstractBanTemplate
 * @see AbstractPunishment
 * @see BanType
 */
public interface AbstractBanImplementation<B extends AbstractBanTemplate, P extends AbstractPunishment<?>> extends Implementation {

    /**
     * Returns an unmodifiable set containing all bans.
     * <p>
     * This method returns an {@link Unmodifiable} {@link Set} containing {@link B}.
     * As the {@link B} are cached it is not a problem to call this method more often.
     * <p>
     * Note that this list is updated when {@link #createBanTemplate(int, String, BanType)}, {@link #createBanTemplate(int, String, BanType, AccurateDuration)} or {@link B#delete()} is called.
     *
     * @return An unmodifiable {@link Set} containing all bans.
     * @see AbstractBanTemplate
     * @see #createBanTemplate(int, String, BanType, AccurateDuration)
     * @see AbstractBanTemplate#delete()
     */
    @NotNull
    @Unmodifiable
    Set<B> getBanTemplates();

    /**
     * Returns an {@link Optional} containing a previously created ban with the given ID.
     * <p>
     * If the {@link Optional} is empty, a {@link B} with the given ID does not exist within the system and can be added using {@link #createBanTemplate(int, String, BanType)} or
     * {@link #createBanTemplate(int, String, BanType, AccurateDuration)}.
     * <p>
     * To get all available {@link B}, use the method {@link #getBanTemplates()} which returns an unmodifiable set containing all {@link B}.
     *
     * @param id The ID of the ban to return.
     * @return An {@link Optional} containing the {@link B} with the given ID, or an empty one if no such {@link B} exists.
     * @see #createBanTemplate(int, String, BanType)
     * @see #createBanTemplate(int, String, BanType, AccurateDuration)
     * @see #getBanTemplates()
     */
    @NotNull
    Optional<B> getBanTemplate(int id);

    /**
     * Creates a new ban with the given ID, name, and type.
     * <p>
     * The ban is returned as a {@link B} object. If a ban with the same ID already exists, it will be overridden.
     * The newly created ban is saved within the database.
     * <p>
     * Note that as the duration is not set, the ban will be permanent.
     *
     * @param id      The ID of the new ban.
     * @param name    The name of the ban.
     * @param banType The type of the ban.
     * @return The newly created {@link B}.
     */
    @NotNull
    B createBanTemplate(int id, @NotNull String name, @NotNull BanType banType);

    /**
     * Creates a new ban with the given ID, name, type, and duration.
     * <p>
     * The ban is returned as a {@link B} object. If a ban with the same ID already exists, it will be overridden.
     * The newly created ban is saved within the database.
     *
     * @param id               The ID of the new ban.
     * @param name             The name of the player being banned.
     * @param banType          The type of the ban.
     * @param accurateDuration The duration of the ban, or {@code null} for permanent bans.
     * @return The new {@link AbstractBanTemplate}.
     * @see #createBanTemplate(int, String, BanType)
     * @see #createBanTemplate(int, String, BanType, AccurateDuration)
     */
    @NotNull
    B createBanTemplate(int id, @NotNull String name, @NotNull BanType banType, @Nullable AccurateDuration accurateDuration);

    /**
     * Appends the specified {@link B} to the players where {@link PlayerClient#getTotalID()} equals to {@link P#getTotalID()}.
     * This can affect multiple entries.
     * <p>
     * Note that this ban will instantly be applied.
     *
     * @param totalID     the UUID of the player to be banned.
     * @param ban         the {@link AbstractBanTemplate} object containing details of the ban to be applied.
     * @param banOperator the name of the team member who is applying the ban.
     * @return a {@link P} object representing the newly applied ban.
     * @see B
     * @see P
     * @see #punish(UUID, B, ServerEntity, Component)
     * @see #punish(UUID, B, ServerEntity, boolean)
     * @see #punish(UUID, B, ServerEntity, boolean, Component)
     *
     */
    @NotNull
    P punish(@NotNull UUID totalID, @NotNull B ban, @NotNull ServerEntity banOperator);

    /**
     * Appends the specified {@link B} to the players where {@link PlayerClient#getTotalID()} equals to {@link P#getTotalID()}.
     * This can affect multiple entries.
     * If a reason for the ban is provided, it will be included in the {@link P} object for future reference.
     * <p>
     * Note that this ban will instantly be applied.
     *
     * @param totalID     the UUID of the player to be banned
     * @param ban         the {@link B} object containing details of the ban to be applied
     * @param banOperator the name of the team member who is applying the ban
     * @param reason      an optional reason for the ban, which will be included in the {@link P} object  If it is {@code null} the {@link P} won't have a reason.
     * @return a {@link P} object representing the newly applied ban
     * @see B
     * @see P
     * @see #punish(UUID, B, ServerEntity)
     * @see #punish(UUID, B, ServerEntity, boolean)
     * @see #punish(UUID, B, ServerEntity, boolean, Component)
     */
    @NotNull
    P punish(@NotNull UUID totalID, @NotNull B ban, @NotNull ServerEntity banOperator, @Nullable Component reason);

    /**
     * Appends the specified {@link B} to the players where {@link PlayerClient#getTotalID()} equals to {@link P#getTotalID()}.
     * This can affect multiple entries.
     *
     * @param totalID     the UUID of the player to be banned
     * @param ban         the {@link B} object containing details of the ban to be applied
     * @param banOperator the name of the team member who is applying the ban
     * @param apply       a boolean indicating whether to apply the punishment immediately
     * @return a {@link P} object representing the newly applied ban
     * @see B
     * @see P
     * @see #punish(UUID, B, ServerEntity)
     * @see #punish(UUID, B, ServerEntity, Component)
     * @see #punish(UUID, B, ServerEntity, boolean, Component)
     */
    @NotNull
    P punish(@NotNull UUID totalID, @NotNull B ban, @NotNull ServerEntity banOperator, boolean apply);

    /**
     * Appends the specified {@link B} to the players where {@link PlayerClient#getTotalID()} equals to {@link P#getTotalID()}.
     * This can affect multiple entries.
     *
     * @param totalID     the UUID of the player to be banned
     * @param ban         the {@link B} object containing details of the ban to be applied
     * @param banOperator the name of the team member who is applying the ban
     * @param apply       a boolean indicating whether to apply the punishment immediately
     * @param reason      an optional reason for the ban, which will be included in the {@link P} object. If it is {@code null} the {@link P} won't have a reason.
     * @return a {@link P} object representing the newly applied ban
     * @see B
     * @see P
     * @see #punish(UUID, B, ServerEntity)
     * @see #punish(UUID, B, ServerEntity, Component)
     * @see #punish(UUID, B, ServerEntity, boolean)
     */
    @NotNull
    P punish(@NotNull UUID totalID, @NotNull B ban, @NotNull ServerEntity banOperator, boolean apply, @Nullable Component reason);

    /**
     * Returns the most recent valid ban associated with the given id.
     * If no valid bans are found, an empty {@link Optional} is returned.
     *
     * <p>
     * To filter for a specific {@link BanType}, use the {@link #getLatestPunishment(UUID, BanType)} method instead.
     * </p>
     *
     * @param totalID the total id of the players for whom to search for bans.
     * @return an {@link Optional} containing the most recent {@link P} object representing a valid ban associated with the given id.
     * or an empty {@link Optional} if no valid bans are found.
     * @see AbstractPunishment#isValid()
     * @see #punish(UUID, B, ServerEntity, Component)
     * @see #punish(UUID, B, ServerEntity)
     * @see #getLatestPunishment(UUID, BanType)
     */
    @NotNull
    Optional<P> getLatestPunishment(@NotNull UUID totalID);

    /**
     * Returns the most recent valid punishments of the specified type associated with the given id.
     * If no valid punishments of the specified type are found, an empty {@link Optional} is returned.
     *
     * <p>
     * If no specific {@link BanType} is needed, use the method {@link #getLatestPunishment(UUID)} instead.
     * </p>
     *
     * @param totalID the total id of the players for whom to search for punishments.
     * @param banType the type of ban to search for.
     * @return an {@link Optional} containing the most recent {@link P} object representing a valid punishments associated with the given id.
     * Returns an empty {@link Optional} if no valid bans of the specified type are found.
     * @see AbstractPunishment#isValid()
     * @see #getLatestPunishment(UUID)
     * @see #punish(UUID, B, ServerEntity, Component)
     * @see #punish(UUID, B, ServerEntity)
     */
    @NotNull
    Optional<P> getLatestPunishment(@NotNull UUID totalID, @NotNull BanType banType);

    /**
     * Returns an unmodifiable set of all the punishments associated with the given id.
     * This list contains all punishments not regarding their {@link P#isValid()} state.
     *
     * @param totalID the UUID of the player for whom to retrieve the set of punishments.
     * @return an unmodifiable {@link Set} of all the {@link P} objects associated with the given id.
     * @see #getLatestPunishment(UUID)
     * @see #getLatestPunishment(UUID, BanType)
     */
    @NotNull
    @Unmodifiable
    Set<P> getPunishmentSet(@NotNull UUID totalID);

    /**
     * Returns an unmodifiable set of all the bans associated with any id.
     * These bans include both valid and invalid bans.
     *
     * @return an unmodifiable {@link Set} of all the {@link P} objects associated with any player.
     * @see #getPunishmentSet(UUID)
     * @see #getLatestPunishment(UUID)
     * @see #getLatestPunishment(UUID, BanType)
     */
    @NotNull
    @Unmodifiable
    Set<P> getPunishmentSet();

}
