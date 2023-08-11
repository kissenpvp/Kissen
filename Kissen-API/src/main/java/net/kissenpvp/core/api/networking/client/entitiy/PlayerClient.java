package net.kissenpvp.core.api.networking.client.entitiy;

import net.kissenpvp.core.api.user.rank.PlayerRank;
import net.kissenpvp.core.api.user.rank.Rank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.time.Duration;
import java.util.List;

public interface PlayerClient<PLAYERRANK extends PlayerRank<?>> extends ServerEntity, BasePlayerClient {

    @NotNull @Unmodifiable List<PLAYERRANK> getRankHistory();

    @NotNull PLAYERRANK getRank();

    @NotNull PLAYERRANK grantRank(@NotNull Rank rank);

    @NotNull PLAYERRANK grantRank(@NotNull Rank rank, @Nullable Duration duration);
}
