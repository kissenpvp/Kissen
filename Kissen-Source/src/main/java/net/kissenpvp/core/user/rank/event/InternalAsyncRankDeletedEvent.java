package net.kissenpvp.core.user.rank.event;

import net.kissenpvp.core.api.user.rank.AbstractRank;
import net.kissenpvp.core.api.user.rank.event.AbstractPostAsyncRankDeleteEvent;
import org.jetbrains.annotations.NotNull;

public class InternalAsyncRankDeletedEvent<T extends AbstractRank> extends InternalRankEvent<T> implements AbstractPostAsyncRankDeleteEvent<T> {

    public InternalAsyncRankDeletedEvent(@NotNull T rankTemplate) {
        super(rankTemplate);
    }
}
