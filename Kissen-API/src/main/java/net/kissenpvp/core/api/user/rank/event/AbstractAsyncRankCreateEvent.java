package net.kissenpvp.core.api.user.rank.event;

import net.kissenpvp.core.api.event.Cancellable;
import net.kissenpvp.core.api.user.rank.AbstractRank;

public interface AbstractAsyncRankCreateEvent<T extends AbstractRank> extends AbstractRankEvent<T>, Cancellable {
}
