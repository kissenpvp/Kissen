package net.kissenpvp.core.api.user.rank.event;

import net.kissenpvp.core.api.user.rank.AbstractRank;

public interface AbstractRankPriorityChangeEvent<T extends AbstractRank> extends AbstractRankEvent<T>
{

    int getPreviousPriority();

    int getPriority();

    void setPriority(int priority);

}
