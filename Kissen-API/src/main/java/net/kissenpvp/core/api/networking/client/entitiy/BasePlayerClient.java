package net.kissenpvp.core.api.networking.client.entitiy;

import net.kissenpvp.core.api.ban.Ban;
import net.kissenpvp.core.api.ban.PlayerBan;
import net.kissenpvp.core.api.user.User;
import net.kissenpvp.core.api.user.suffix.Suffix;
import net.kissenpvp.core.api.user.usersetttings.PlayerSetting;
import net.kissenpvp.core.api.user.usersetttings.UserSetting;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface BasePlayerClient {

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
    @NotNull @Unmodifiable Set<UUID> getAltAccounts();

    @NotNull UUID getTotalID();

    @NotNull PlayerBan ban(@NotNull Ban ban, @NotNull Component banOperator);

    @NotNull PlayerBan ban(@NotNull Ban ban, @NotNull Component banOperator, @Nullable Component reason);

    @NotNull Optional<@Nullable PlayerBan> getBan(@NotNull String id);

    /**
     * Returns a {@link List} containing all {@link PlayerBan} this player had.
     * These are sorted after the time they were created.
     *
     * @return a list containing all bans this player ever had.
     */
    @NotNull @Unmodifiable List<PlayerBan> getBanHistory();

    @NotNull Component displayName();

    @NotNull Component styledRankName();

    @NotNull Set<Suffix> getSuffixSet();

    @NotNull Optional<@Nullable Suffix> getSuffix(@NotNull String name);

    @NotNull Optional<@Nullable Suffix> setSuffix(@NotNull String name, @NotNull Component content);

    boolean deleteSuffix(@NotNull String name);

    @NotNull Optional<@Nullable Suffix> getSelectedSuffix();

    void setSelectedSuffix(@NotNull String name) throws NullPointerException;

    long getOnlineTime();

    long getLastPlayed();

    @NotNull User getUser();

    <X> @NotNull UserSetting<X> getUserSetting(Class<? extends PlayerSetting<X>> settingClass);


}
