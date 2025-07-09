package net.kissenpvp.network.actor.rank;

import net.kissenpvp.api.network.actor.rank.Rank;
import net.kissenpvp.database.InternalPersistableEntity;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public class InternalRank extends InternalPersistableEntity<String> implements Rank
{
    private final String id;
    private int priority;
    private @Nullable Component prefix;
    private @Nullable Component suffix;

    public InternalRank(String id, int priority, @Nullable Component prefix, @Nullable Component suffix) throws NullPointerException
    {
        Objects.requireNonNull(id, "Id cannot be null");

        this.id = id;
        this.priority = priority;
        this.prefix = prefix;
        this.suffix = suffix;
    }

    @Override public @NotNull String id()
    {
        return id;
    }

    @Override public int priority()
    {
        return priority;
    }

    @Override public void priority(int priority)
    {
        this.priority = priority;
    }

    @Override public @NotNull Optional<Component> prefix()
    {
        return Optional.ofNullable(prefix);
    }

    @Override public void prefix(@Nullable Component prefix)
    {
        this.prefix = prefix;
    }

    @Override public void unsetPrefix()
    {
        prefix(null);
    }

    @Override public @NotNull Optional<Component> suffix()
    {
        return Optional.ofNullable(suffix);
    }

    @Override public void suffix(@Nullable Component suffix)
    {
        this.suffix = suffix;
    }

    @Override public void unsetSuffix()
    {
        suffix(null);
    }

    @Override public int signature()
    {
        return Objects.hash(priority, prefix, suffix);
    }
}
