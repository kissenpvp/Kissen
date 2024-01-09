package net.kissenpvp.core.api.user.rank.event;

import net.kissenpvp.core.api.event.Cancellable;
import net.kissenpvp.core.api.event.EventClass;
import net.kissenpvp.core.api.user.rank.Rank;
import org.jetbrains.annotations.NotNull;

public interface RankEvent<T extends Rank> extends EventClass, Cancellable
{
    @NotNull T getRank();

}
