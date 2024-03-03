package net.kissenpvp.core.user.rank.event;

import lombok.Getter;
import lombok.Setter;
import net.kissenpvp.core.api.user.rank.Rank;
import net.kissenpvp.core.api.user.rank.event.RankSetEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter @Setter
public class KissenRankSetEvent<T extends Rank, V> implements RankSetEvent<T, V>
{
    private final T rank;
    private final V old;
    private V updated;
    private boolean cancelled;

    public KissenRankSetEvent(@NotNull T rank, @Nullable V old, @Nullable V updated)
    {
        this.rank = rank;
        this.old = old;
        this.updated = updated;
    }

    @Override
    public boolean volatileEvent() {
        return true;
    }
}
