package net.kissenpvp.core.user.rank.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kissenpvp.core.api.time.AccurateDuration;
import net.kissenpvp.core.api.user.rank.AbstractPlayerRank;
import net.kissenpvp.core.api.user.rank.event.AbstractAsyncRankExpireEvent;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;
import java.time.Period;
import java.util.Objects;

@RequiredArgsConstructor @Getter
public class InternalAsyncRankExpireEvent<T extends AbstractPlayerRank<?>> implements AbstractAsyncRankExpireEvent<T> {

    private final ExpiryCause expiryCause;
    private final T playerRank;
    private Instant cancelled;

    @Override
    public void setCancelled(@NotNull Instant expiration) {
        if(Instant.now().isAfter(expiration))
        {
            throw new IllegalArgumentException("Instant must be after now.");
        }
        this.cancelled = expiration;
    }

    @Override
    public void setCancelled(@NotNull Duration expiration) {
        setCancelled(Instant.now().plus(expiration));
    }

    @Override
    public void setCancelled(@NotNull Period expiration) {
        setCancelled(Instant.now().plus(expiration));
    }

    @Override
    public void setCancelled(@NotNull AccurateDuration expiration) {
        setCancelled(Instant.now().plus(expiration.getDuration()));
    }

    @Override
    public boolean isCancelled() {
        return Objects.nonNull(cancelled);
    }

    @Override
    public void setCancelled(boolean cancel) {
        if(cancel)
        {
            this.cancelled = Instant.now().plus(Period.ofDays(1));
        }
    }

    @Override
    public boolean isAsynchronous() {
        return true;
    }
}
