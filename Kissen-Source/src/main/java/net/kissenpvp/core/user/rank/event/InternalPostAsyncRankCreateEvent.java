package net.kissenpvp.core.user.rank.event;

import net.kissenpvp.core.api.user.rank.AbstractRank;
import net.kissenpvp.core.api.user.rank.event.AbstractPostAsyncRankCreateEvent;
import org.jetbrains.annotations.NotNull;

public class InternalPostAsyncRankCreateEvent<T extends AbstractRank> extends InternalRankEvent<T> implements AbstractPostAsyncRankCreateEvent<T> {

    public InternalPostAsyncRankCreateEvent(@NotNull T rankTemplate) {
        super(rankTemplate);
    }
}
