package net.kissenpvp.core.api.networking.client.entitiy;

import net.kissenpvp.core.api.ban.Punishment;
import net.kissenpvp.core.api.permission.Permission;
import net.kissenpvp.core.api.user.rank.PlayerRank;
import net.kissenpvp.core.api.user.rank.Rank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.time.Duration;
import java.util.List;

public interface PlayerClient<P extends Permission, R extends PlayerRank<?>, B extends Punishment<?>> extends BasePlayerClient<P, R, B> {

    @NotNull @Unmodifiable List<R> getRankHistory();

    @NotNull R getRank();

    @NotNull R grantRank(@NotNull Rank rank);

    @NotNull R grantRank(@NotNull Rank rank, @Nullable Duration duration);
}
