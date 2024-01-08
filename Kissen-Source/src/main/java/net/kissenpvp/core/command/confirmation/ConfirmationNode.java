package net.kissenpvp.core.command.confirmation;

import org.jetbrains.annotations.NotNull;

import java.time.Instant;

public record ConfirmationNode(@NotNull Instant timeStamp, @NotNull Runnable run, @NotNull Runnable cancelled, @NotNull Runnable time) implements Confirmation
{
    public void execute()
    {
        run().run();
    }

    public void cancel()
    {
        cancelled().run();
    }

    public void clean()
    {
        time().run();
    }

    @Override
    public boolean isValid()
    {
        return Instant.now().isBefore(timeStamp());
    }
}
