package net.kissenpvp.core.user.rank.event;

import lombok.Getter;
import lombok.Setter;
import net.kissenpvp.core.api.user.rank.AbstractRank;
import net.kissenpvp.core.api.user.rank.event.AbstractAsyncRankCreateEvent;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class InternalAsyncRankCreateEvent<T extends AbstractRank> extends InternalRankEvent<T> implements AbstractAsyncRankCreateEvent<T> {

    private boolean cancelled;

    public InternalAsyncRankCreateEvent(@NotNull T rankTemplate) {
        super(rankTemplate);
    }
}
