package net.kissenpvp.database;

import net.kissenpvp.api.database.PersistableEntity;
import net.kissenpvp.api.database.SubscriptionEntity;
import org.jspecify.annotations.NonNull;

import java.util.Objects;


public abstract class InternalSubscriptionEntity<P, X, T extends PersistableEntity<X>> implements SubscriptionEntity<P, X, T>
{
    private final @NonNull P id;
    private final @NonNull X parentId;

    public InternalSubscriptionEntity(@NonNull P id, @NonNull X parentId)
    {
        this.id = id;
        this.parentId = parentId;
    }

    @Override public @NonNull P id()
    {
        return id;
    }

    @Override public @NonNull X parentId()
    {
        return parentId;
    }

    @Override public boolean equals(Object o)
    {
        if (o == null || getClass() != o.getClass()) { return false; }
        InternalSubscriptionEntity<?, ?, ?> that = (InternalSubscriptionEntity<?, ?, ?>) o;
        return Objects.equals(id, that.id);
    }

    @Override public int hashCode()
    {
        return Objects.hashCode(id);
    }
}
