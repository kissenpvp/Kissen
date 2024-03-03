package net.kissenpvp.core.ban.warn;

import net.kissenpvp.core.api.ban.Ban;
import net.kissenpvp.core.api.ban.BanImplementation;
import net.kissenpvp.core.api.event.EventCancelledException;
import net.kissenpvp.core.api.user.Warn;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.time.KissenTemporalObject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.Optional;

public class KissenWarn<T extends Ban> extends KissenTemporalObject implements Warn<T>
{
    private final WarnNode warnNode;

    public KissenWarn(@NotNull WarnNode warnNode)
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
        return ((BanImplementation<T, ?>) KissenCore.getInstance().getImplementation(BanImplementation.class)).getBan(warnNode.id());
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
