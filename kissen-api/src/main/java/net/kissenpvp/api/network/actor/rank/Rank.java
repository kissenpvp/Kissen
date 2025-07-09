package net.kissenpvp.api.network.actor.rank;

import net.kissenpvp.api.database.PersistableEntity;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface Rank extends PersistableEntity<String>
{
    int priority();

    void priority(int priority);

    @NotNull Optional<Component> prefix();

    void prefix(@Nullable Component prefix);

    void unsetPrefix();

    @NotNull Optional<Component> suffix();

    void suffix(@Nullable Component suffix);

    void unsetSuffix();
}
