package net.kissenpvp.network.actor.rank;

import net.kissenpvp.api.network.actor.rank.Rank;
import net.kissenpvp.database.InternalPersistableEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class InternalRank extends InternalPersistableEntity<String> implements Rank
{
    private final String id;
    private int priority;

    public InternalRank(@NotNull String id, int priority) throws NullPointerException
    {
        Objects.requireNonNull(id, "Id cannot be null");

        this.id = id;
        this.priority = priority;
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

    @Override public int signature()
    {
        return Objects.hash(id, priority);
    }

    @Override public boolean equals(Object o)
    {
        if (o == null || getClass() != o.getClass()) {return false;}
        InternalRank that = (InternalRank) o;
        return Objects.equals(id, that.id);
    }

    @Override public int hashCode()
    {
        return Objects.hashCode(id);
    }
}
