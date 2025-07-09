package net.kissenpvp.api.punishment;

import net.kissenpvp.api.database.PersistableEntity;
import net.kissenpvp.api.temporal.timespan.TimeSpan;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface Punishment extends PersistableEntity<Integer>
{
    @NotNull TimeSpan timeSpan();

    void timeSpan(@NotNull TimeSpan timeSpan);

    @NotNull PunishmentType punishmentType();

    void punishmentType(PunishmentType punishmentType);

    @NotNull Optional<Component> defaultMessage();

    void defaultMessage(@Nullable Component defaultMessage);

    void unsetMessage();
}
