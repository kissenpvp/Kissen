package net.kissenpvp.core.ban.warn;

import net.kissenpvp.core.api.ban.AbstractBanTemplate;
import net.kissenpvp.core.api.ban.AbstractBanImplementation;
import net.kissenpvp.core.api.event.EventCancelledException;
import net.kissenpvp.core.api.time.KissenTemporalObject;
import net.kissenpvp.core.api.user.AbstractWarn;
import net.kissenpvp.core.base.KissenCore;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.Optional;

public class KissenAbstractWarn<T extends AbstractBanTemplate> extends KissenTemporalObject implements AbstractWarn<T>
{
    private final WarnNode warnNode;

    public KissenAbstractWarn(@NotNull WarnNode warnNode)
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
        return ((AbstractBanImplementation<T, ?>) KissenCore.getInstance().getImplementation(AbstractBanImplementation.class)).getBanTemplate(warnNode.id());
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
