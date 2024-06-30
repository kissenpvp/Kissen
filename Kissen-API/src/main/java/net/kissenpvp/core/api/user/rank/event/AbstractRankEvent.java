package net.kissenpvp.core.api.user.rank.event;

import net.kissenpvp.core.api.user.rank.AbstractRank;
import org.jetbrains.annotations.NotNull;

public interface AbstractRankEvent<T extends AbstractRank> extends UndefinedAbstractRankEvent<T>
{
    @NotNull T getRankTemplate();

}
