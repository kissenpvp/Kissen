package net.kissenpvp.core.user.rank.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kissenpvp.core.api.user.rank.AbstractPlayerRank;
import net.kissenpvp.core.api.user.rank.event.AbstractAsyncRankExpiredEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;


@Getter @RequiredArgsConstructor
public class InternalAsyncRankExpiredEvent<T extends AbstractPlayerRank<?>> implements AbstractAsyncRankExpiredEvent<T> {
    private final @NotNull ExpiryCause expiryCause;
    private final @NotNull T playerRank;

    @Override
    public boolean isAsynchronous() {
        return true;
    }
}
