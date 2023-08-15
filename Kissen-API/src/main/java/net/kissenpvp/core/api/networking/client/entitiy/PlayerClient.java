package net.kissenpvp.core.api.networking.client.entitiy;

import net.kissenpvp.core.api.ban.Ban;
import net.kissenpvp.core.api.ban.BanOperator;
import net.kissenpvp.core.api.ban.Punishment;
import net.kissenpvp.core.api.database.meta.BackendException;
import net.kissenpvp.core.api.permission.GroupablePermissionEntry;
import net.kissenpvp.core.api.permission.Permission;
import net.kissenpvp.core.api.user.User;
import net.kissenpvp.core.api.user.rank.PlayerRank;
import net.kissenpvp.core.api.user.rank.Rank;
import net.kissenpvp.core.api.user.suffix.Suffix;
import net.kissenpvp.core.api.user.usersetttings.PlayerSetting;
import net.kissenpvp.core.api.user.usersetttings.UserSetting;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface PlayerClient<P extends Permission, R extends PlayerRank<?>, B extends Punishment<?>> extends ServerEntity, GroupablePermissionEntry<P>, MessageReceiver {

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

    @NotNull R grantRank(@NotNull Rank rank, @Nullable Duration duration);

    @NotNull B punish(@NotNull Ban ban, @NotNull BanOperator banOperator) throws BackendException;

    @NotNull B punish(@NotNull Ban ban, @NotNull BanOperator banOperator, @Nullable Component reason) throws BackendException;

    @NotNull Optional<B> getBan(@NotNull String id) throws BackendException;

    /**
     * Returns a {@link List} containing all {@link Punishment} this player had.
     * These are sorted after the time they were created.
     *
     * @return a list containing all bans this player ever had.
     */
    @NotNull @Unmodifiable List<B> getBanHistory() throws BackendException;

    @NotNull Component displayName();

    @NotNull Component styledRankName();

    @NotNull Set<Suffix> getSuffixSet();

    @NotNull Optional<Suffix> getSuffix(@NotNull String name);

    @NotNull Optional<Suffix> setSuffix(@NotNull String name, @NotNull Component content);

    boolean deleteSuffix(@NotNull String name);

    @NotNull Optional<Suffix> getSelectedSuffix();

    void setSelectedSuffix(@NotNull String name) throws NullPointerException;

    long getOnlineTime();

    long getLastPlayed();

    @NotNull User getUser();

    <X> @NotNull UserSetting<X> getUserSetting(Class<? extends PlayerSetting<X>> settingClass);


}
