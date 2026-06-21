package net.kissenpvp.network.actor.rank;

import com.google.common.base.Preconditions;
import net.kissenpvp.api.database.PersistableEntity;
import net.kissenpvp.api.network.actor.rank.Rank;
import net.kissenpvp.api.temporal.DefinedTemporalObject;
import net.kissenpvp.api.temporal.timespan.PermanentTimeSpan;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

public class InternalRank implements PersistableEntity<String>, Rank
{
    private final String id;
    private int priority;
    private DefinedTemporalObject temporal;

    public InternalRank(@NonNull String id, int priority) throws NullPointerException
    {
        this(id, priority, DefinedTemporalObject.toTemporal(new PermanentTimeSpan()));
    }

    public InternalRank(@NonNull String id, int priority, @NonNull DefinedTemporalObject temporal) throws NullPointerException
    {
        Preconditions.checkNotNull(id, "Id cannot be null");

        this.id = id;
        this.priority = priority;
        this.temporal = temporal;
    }

    @Override public @NonNull String id()
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

    @Override
    public boolean hasPriority(@NonNull Rank that)
    {
        return priority > that.priority();
    }

    @Override public boolean equals(Object o)
    {
        if (o == null || getClass() != o.getClass()) { return false; }
        InternalRank that = (InternalRank) o;
        return Objects.equals(id, that.id);
    }

    @Override public int hashCode()
    {
        return Objects.hashCode(id);
    }

    @Override public @NonNull DefinedTemporalObject temporal()
    {
        return temporal;
    }
}
