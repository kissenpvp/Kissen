package net.kissenpvp.core.api.command;

import net.kissenpvp.core.api.base.plugin.KissenPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.Instant;

public interface Confirmation {

    @Nullable
    KissenPlugin plugin();

    @NotNull
    Instant timeStamp();

    @NotNull
    Runnable task();

    @NotNull
    Runnable cancel();

    @NotNull
    Runnable expire();
}
