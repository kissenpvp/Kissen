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

package net.kissenpvp.core.api.networking.client.entitiy;

import net.kissenpvp.core.api.ban.AbstractBanTemplate;
import net.kissenpvp.core.api.ban.AbstractPunishment;
import net.kissenpvp.core.api.database.meta.BackendException;
import net.kissenpvp.core.api.permission.AbstractPermission;
import net.kissenpvp.core.api.time.AccurateDuration;
import net.kissenpvp.core.api.user.User;
import net.kissenpvp.core.api.user.rank.AbstractPlayerRank;
import net.kissenpvp.core.api.user.rank.AbstractRank;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface PlayerClient<R extends AbstractPlayerRank<?>, B extends AbstractPunishment<?>> extends ServerEntity {

    /**
     * Returns the UUID of the player this object is pointing to.
     * <p>
     * When the player was never seen on the network before, and therefore has not a created profile, this system tries to fetch the uuid using the mojang api.
     *
     * @return the uuid of this player.
     */
    @NotNull UUID getUniqueId();

    /**
     * Returns a {@link Set} of all accounts sharing the same {@link #getTotalID()}.
     * These are received from the database and therefore this can take some performance.
     *
     * @return a {@link Set} of all accounts sharing the same {@link #getTotalID()}.
     */
    @NotNull @Unmodifiable Set<UUID> getAltAccounts() throws BackendException;

    /**
     * Retrieves the total ID associated with an account for clustering purposes, such as bans.
     *
     * <p>The {@code getTotalID()} method returns the unique identifier (UUID) that connects accounts to cluster them.
     * This identifier is useful for operations involving multiple accounts, such as bans or other collective actions.</p>
     *
     * @return the {@link UUID} representing the total ID associated with an account
     */
    @NotNull UUID getTotalID();

    /**
     * Retrieves the rank history associated with an entity in an unmodifiable list.
     *
     * <p>The {@code getRankHistory()} method returns an unmodifiable {@link List} containing the rank history of the entity.
     * The list provides a historical perspective of rank changes over time. It is guaranteed to be unmodifiable, ensuring
     * that the rank history remains consistent and cannot be altered externally.</p>
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * {@code
     * SomeEntity entity = new SomeEntity();
     * List<Rank> rankHistory = entity.getRankHistory();
     * System.out.println("Rank History: " + rankHistory);
     * }
     * </pre>
     *
     * @return an unmodifiable {@link List} of type {@code R} representing the rank history of the entity
     */
    @NotNull @Unmodifiable List<R> getRankHistory();

    /**
     * Retrieves the current rank associated with an entity.
     *
     * <p>The {@code getRank()} method returns the current rank of the entity. This method provides the current snapshot
     * of the entity's rank without considering the historical changes.</p>
     *
     * @return the current rank of the entity
     */
    @NotNull R getRank();

    /**
     * Grants the specified rank to an entity.
     *
     * <p>The {@code grantRank(Rank)} method assigns the provided rank to the entity. This method is used to set the
     * entity's rank without specifying a duration for the assignment. The rank is granted indefinitely unless modified
     * through subsequent operations.</p>
     *
     * @param rank the {@link AbstractRank} to be granted to the entity
     * @return the updated rank of the entity after granting the specified rank
     */
    @NotNull R grantRank(@NotNull AbstractRank rank);

    /**
     * Grants the specified rank to an entity for a specific duration.
     *
     * <p>The {@code grantRank(Rank rank, AccurateDuration accurateDuration)} method assigns the provided rank to the entity
     * for the specified duration. This method allows the rank to be granted temporarily, and the entity's rank will revert
     * to its previous state after the specified duration elapses.</p>
     *
     * @param rank              the {@link AbstractRank} to be granted to the entity
     * @param accurateDuration the duration for which the rank is granted (nullable for indefinite duration)
     * @return the updated rank of the entity after granting the specified rank
     */
    @NotNull R grantRank(@NotNull AbstractRank rank, @Nullable AccurateDuration accurateDuration);

    /**
     * Applies a punishment based on the provided ban information and the server entity performing the ban.
     *
     * <p>This method is specifically designed for cases where a ban and ban operator are provided without a reason.</p>
     *
     * @param ban the {@link AbstractBanTemplate} information for the punishment
     * @param banOperator the {@link ServerEntity} performing the ban
     * @return the punishment object representing the applied punishment
     * @throws NullPointerException if either the ban or banOperator is {@code null}
     */
    @NotNull B punish(@NotNull AbstractBanTemplate ban, @NotNull ServerEntity banOperator);

    /**
     * Applies a punishment based on the provided ban information and the server entity performing the ban.
     *
     * <p>This method is overloaded to support two scenarios:</p>
     * <ol>
     *     <li>When a ban and ban operator are specified without a reason.</li>
     *     <li>When a ban, ban operator, and an optional reason are provided.</li>
     * </ol>
     *
     * @param ban the {@link AbstractBanTemplate} information for the punishment
     * @param banOperator the {@link ServerEntity} performing the ban
     * @param reason (optional) the reason for the ban, can be {@code null} if not provided
     * @return the punishment object representing the applied punishment
     * @throws NullPointerException if either the ban or banOperator is {@code null}
     */
    @NotNull B punish(@NotNull AbstractBanTemplate ban, @NotNull ServerEntity banOperator, @Nullable Component reason);

    @NotNull Optional<B> getPunishment(@NotNull String id) throws BackendException;

    /**
     * Returns a {@link List} containing all {@link AbstractPunishment} this player had.
     * These are sorted after the time they were created.
     *
     * @return a list containing all bans this player ever had.
     */
    @NotNull @Unmodifiable List<B> getPunishmentHistory() throws BackendException;

    @NotNull Component displayName();

    @NotNull
    AccurateDuration getOnlineTime();

    @NotNull User getUser();
}
