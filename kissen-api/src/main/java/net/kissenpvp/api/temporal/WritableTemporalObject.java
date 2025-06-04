package net.kissenpvp.api.temporal;

import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.Optional;

public interface WritableTemporalObject extends TemporalObject
{
    void expire();

    @NotNull Optional<Instant> estimatedExpiry();

    boolean expiryAltered();
}
