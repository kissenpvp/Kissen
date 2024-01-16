package net.kissenpvp.core.command.confirmation;

import net.kissenpvp.core.api.base.plugin.KissenPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;

public record ConfirmationNode(@Nullable KissenPlugin plugin, @NotNull Instant timeStamp, @NotNull Runnable task, @NotNull Runnable cancel, @NotNull Runnable expire) implements Confirmation
{
    @Override
    public boolean isValid()
    {
        return Instant.now().isBefore(timeStamp());
    }
}
