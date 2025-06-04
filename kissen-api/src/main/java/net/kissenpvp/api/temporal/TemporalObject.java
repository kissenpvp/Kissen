package net.kissenpvp.api.temporal;

import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.Optional;

public interface TemporalObject
{
    @NotNull Optional<Instant> expiry();

    @NotNull Instant start();
}
