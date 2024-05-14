package net.kissenpvp.core.api.user;

import net.kissenpvp.core.api.ban.AbstractBan;
import net.kissenpvp.core.api.time.TemporalObject;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface AbstractWarn<T extends AbstractBan> extends TemporalObject
{
    @NotNull Optional<T> getBan();

    @NotNull Optional<Component> getReason();

    @NotNull String getBanOperator();
}