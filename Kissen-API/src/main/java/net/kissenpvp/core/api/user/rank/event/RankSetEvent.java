package net.kissenpvp.core.api.user.rank.event;

import net.kissenpvp.core.api.user.rank.Rank;
import org.jetbrains.annotations.NotNull;

public interface RankSetEvent<T extends Rank, V> extends RankEvent<T>
{
    @NotNull V getOld();

    @NotNull V getUpdated();

    void setUpdated(@NotNull V update);

}
