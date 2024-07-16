package net.kissenpvp.core.user.rank.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kissenpvp.core.api.user.rank.AbstractPlayerRank;
import net.kissenpvp.core.api.user.rank.event.AbstractPostAsyncRankExpireEvent;
import org.jetbrains.annotations.NotNull;


@Getter @RequiredArgsConstructor
public class InternalPostAsyncRankExpireEvent<T extends AbstractPlayerRank<?>> implements AbstractPostAsyncRankExpireEvent<T> {
    private final @NotNull ExpiryCause expiryCause;
    private final @NotNull T playerRank;

    @Override
    public boolean isAsynchronous() {
        return true;
    }
}
