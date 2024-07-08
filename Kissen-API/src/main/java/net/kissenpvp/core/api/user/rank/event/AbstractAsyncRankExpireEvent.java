package net.kissenpvp.core.api.user.rank.event;

import net.kissenpvp.core.api.time.AccurateDuration;
import net.kissenpvp.core.api.user.rank.AbstractPlayerRank;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;
import java.time.Period;

public interface AbstractAsyncRankExpireEvent<T extends AbstractPlayerRank<?>> extends AbstractPlayerRankEvent<T>
{
    void setCancelled(@NotNull Instant expiration);

    void setCancelled(@NotNull Duration expiration);

    void setCancelled(@NotNull Period expiration);

    void setCancelled(@NotNull AccurateDuration expiration);
}
