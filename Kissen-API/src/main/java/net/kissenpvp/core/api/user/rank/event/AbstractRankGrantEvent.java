package net.kissenpvp.core.api.user.rank.event;

import net.kissenpvp.core.api.user.rank.AbstractPlayerRank;
import org.jetbrains.annotations.NotNull;

public interface AbstractRankGrantEvent<T extends AbstractPlayerRank<?>> extends AbstractPlayerRankEvent<T>
{
    @NotNull T getPreviousRank();

    void setPlayerRank(@NotNull T update);

}
