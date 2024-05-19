package net.kissenpvp.core.command.confirmation;

import net.kissenpvp.core.api.networking.client.entitiy.PlayerClient;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record PlayerConfirmationNode(@NotNull UUID uuid,
                                     @NotNull ConfirmationNode confirmationNode) implements Confirmation
{
    public boolean equals(@NotNull PlayerClient<?, ?> player)
    {
        return uuid().equals(player.getUniqueId());
    }

    @Override
    public boolean isValid()
    {
        return confirmationNode().isValid();
    }
}
