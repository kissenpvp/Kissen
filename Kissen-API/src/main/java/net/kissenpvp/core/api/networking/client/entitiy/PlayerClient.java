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

import net.kissenpvp.core.api.ban.Ban;
import net.kissenpvp.core.api.ban.Punishment;
import net.kissenpvp.core.api.database.meta.BackendException;
import net.kissenpvp.core.api.event.EventCancelledException;
import net.kissenpvp.core.api.permission.Permission;
import net.kissenpvp.core.api.time.AccurateDuration;
import net.kissenpvp.core.api.user.User;
import net.kissenpvp.core.api.user.rank.PlayerRank;
import net.kissenpvp.core.api.user.rank.Rank;
import net.kissenpvp.core.api.user.suffix.Suffix;
import net.kissenpvp.core.api.user.usersetttings.PlayerSetting;
import net.kissenpvp.core.api.user.usersetttings.BoundPlayerSetting;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface PlayerClient<P extends Permission, R extends PlayerRank<?>, B extends Punishment<?>> extends ServerEntity {

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

    @NotNull UUID getTotalID();

    @NotNull @Unmodifiable List<R> getRankHistory();

    @NotNull R getRank();

    @NotNull R grantRank(@NotNull Rank rank);

    @NotNull R grantRank(@NotNull Rank rank, @Nullable AccurateDuration accurateDuration);

    @NotNull B punish(@NotNull Ban ban, @NotNull ServerEntity banOperator) throws BackendException;

    @NotNull B punish(@NotNull Ban ban, @NotNull ServerEntity banOperator, @Nullable Component reason) throws BackendException;

    @NotNull Optional<B> getPunishment(@NotNull String id) throws BackendException;

    /**
     * Returns a {@link List} containing all {@link Punishment} this player had.
     * These are sorted after the time they were created.
     *
     * @return a list containing all bans this player ever had.
     */
    @NotNull @Unmodifiable List<B> getPunishmentHistory() throws BackendException;

    @NotNull Component displayName();

    @NotNull Component styledRankName();

    @NotNull Set<Suffix> getSuffixSet();

    @NotNull Optional<Suffix> getSuffix(@NotNull String name);

    @NotNull Suffix grantSuffix(@NotNull String name, @NotNull Component content) throws EventCancelledException;

    @NotNull Suffix grantSuffix(@NotNull String name, @NotNull Component content, @Nullable AccurateDuration accurateDuration) throws EventCancelledException;

    boolean revokeSuffix(@NotNull String name);

    @NotNull Optional<Suffix> getSelectedSuffix();

    long getOnlineTime();

    long getLastPlayed();

    @NotNull User getUser();

    <X> @NotNull BoundPlayerSetting<X> getUserSetting(@NotNull Class<? extends PlayerSetting<X>> settingClass);


}
