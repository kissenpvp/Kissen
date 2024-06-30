package net.kissenpvp.core.user.rank.event;

import net.kissenpvp.core.api.user.rank.AbstractPlayerRank;
import net.kissenpvp.core.api.user.rank.event.AbstractRankGrantEvent;
import org.jetbrains.annotations.NotNull;

public class InternalRankGrantEvent<T extends AbstractPlayerRank<?>> extends InternalPlayerRankEvent<T> implements AbstractRankGrantEvent<T> {

    private T rank;

    public InternalRankGrantEvent(@NotNull T oldRank, @NotNull T rank) {
        super(oldRank);
        this.rank = rank;
    }

    @Override
    public @NotNull T getPreviousRank() {
        return super.getPlayerRank();
    }

    @Override
    public @NotNull T getPlayerRank() {
        return rank;
    }

    @Override
    public void setPlayerRank(@NotNull T rank) {
        this.rank = rank;
    }
}
