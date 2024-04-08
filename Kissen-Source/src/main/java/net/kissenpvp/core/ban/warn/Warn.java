package net.kissenpvp.core.ban.warn;

import net.kissenpvp.core.api.ban.AbstractBan;
import net.kissenpvp.core.api.ban.AbstractBanImplementation;
import net.kissenpvp.core.api.event.EventCancelledException;
import net.kissenpvp.core.api.time.KissenTemporalObject;
import net.kissenpvp.core.base.KissenCore;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.Optional;

public class Warn<T extends AbstractBan> extends KissenTemporalObject implements net.kissenpvp.core.api.user.Warn<T>
{
    private final WarnNode warnNode;

    public Warn(@NotNull WarnNode warnNode)
    {
        super(warnNode.temporalMeasure());
        this.warnNode = warnNode;
    }

    @Override
    public void setEnd(@Nullable Instant end) throws EventCancelledException
    {
        throw new EventCancelledException();
    }

    @Override
    public @NotNull Optional<T> getBan()
    {
        return ((AbstractBanImplementation<T, ?>) KissenCore.getInstance().getImplementation(AbstractBanImplementation.class)).getBan(warnNode.id());
    }

    @Override
    public @NotNull Optional<Component> getReason()
    {
        return Optional.ofNullable(warnNode.reason());
    }

    @Override
    public @NotNull String getBanOperator()
    {
        return warnNode.by();
    }
}
