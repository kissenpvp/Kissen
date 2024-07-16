package net.kissenpvp.core.user.rank.event;

import lombok.Getter;
import lombok.Setter;
import net.kissenpvp.core.api.user.rank.AbstractRank;
import net.kissenpvp.core.api.user.rank.event.AbstractAsyncRankDeleteEvent;
import net.kissenpvp.core.api.user.rank.event.AbstractPostAsyncRankDeleteEvent;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class InternalAsyncRankDeleteEvent<T extends AbstractRank> extends InternalRankEvent<T> implements AbstractAsyncRankDeleteEvent<T> {

    private boolean cancelled;

    public InternalAsyncRankDeleteEvent(@NotNull T rankTemplate) {
        super(rankTemplate);
    }
}
