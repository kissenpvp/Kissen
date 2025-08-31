package net.kissenpvp.network.actor.rank;

import net.kissenpvp.api.network.actor.rank.Rank;
import net.kissenpvp.api.temporal.TemporalObject;
import net.kissenpvp.database.InternalPersistableEntity;
import net.kissenpvp.temporal.InternalTemporalObject;
import net.kissenpvp.temporal.timespan.PermanentTimeSpan;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class InternalRank extends InternalPersistableEntity<String> implements Rank
{
    private final String id;
    private int priority;
    private TemporalObject temporal;

    public InternalRank(@NotNull String id, int priority) throws NullPointerException
    {
        this(id, priority, InternalTemporalObject.toTemporal(new PermanentTimeSpan()));
    }

    public InternalRank(@NotNull String id, int priority, @NotNull TemporalObject temporal) throws NullPointerException
    {
        Objects.requireNonNull(id, "Id cannot be null");

        this.id = id;
        this.priority = priority;
        this.temporal = temporal;
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

    @Override public @NotNull TemporalObject temporal()
    {
        return temporal;
    }
}
