package net.kissenpvp.core.user.rank.event;

import net.kissenpvp.core.api.user.rank.AbstractRank;
import net.kissenpvp.core.api.user.rank.event.AbstractRankPriorityChangeEvent;
import org.jetbrains.annotations.NotNull;

public class InternalRankPriorityChangeEvent<T extends AbstractRank> extends InternalRankEvent<T> implements AbstractRankPriorityChangeEvent<T> {

    private final int previousPriority;
    private int priority;

    public InternalRankPriorityChangeEvent(@NotNull T rank, int previousPriority, int priority) {
        super(rank);
        this.previousPriority = previousPriority;
        this.priority = priority;
    }

    @Override
    public int getPreviousPriority() {
        return previousPriority;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public void setPriority(int priority) {
        this.priority = priority;
    }
}
