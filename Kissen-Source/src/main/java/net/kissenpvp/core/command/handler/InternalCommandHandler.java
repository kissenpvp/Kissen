package net.kissenpvp.core.command.handler;

import net.kissenpvp.core.api.networking.client.entitiy.ServerEntity;
import net.kissenpvp.core.command.CommandHolder;
import org.jetbrains.annotations.NotNull;

public abstract class InternalCommandHandler<S extends ServerEntity, C extends CommandHolder<S, ? extends C>> extends AbstractCommandHandler<S, C>
{
    @Override
    protected @NotNull String getHandlerName()
    {
        return "system";
    }
}
