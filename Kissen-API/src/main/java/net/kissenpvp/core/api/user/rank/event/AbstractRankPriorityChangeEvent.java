package net.kissenpvp.core.api.user.rank.event;

import net.kissenpvp.core.api.event.Cancellable;
import net.kissenpvp.core.api.user.rank.AbstractRank;

public interface AbstractRankPriorityChangeEvent<T extends AbstractRank> extends AbstractRankEvent<T>, Cancellable
{

    int getPreviousPriority();

    int getPriority();

    void setPriority(int priority);

}
