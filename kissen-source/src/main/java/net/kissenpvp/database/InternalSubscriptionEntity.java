package net.kissenpvp.database;

import net.kissenpvp.api.database.PersistableEntity;
import net.kissenpvp.api.database.SubscriptionEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;


public abstract class InternalSubscriptionEntity<P, X, T extends PersistableEntity<X>> extends InternalPersistableEntity<P> implements SubscriptionEntity<P, X, T>
{
    private final @NotNull P id;
    private final @NotNull X parentId;

    public InternalSubscriptionEntity(@NotNull P id, @NotNull X parentId)
    {
        this.id = id;
        this.parentId = parentId;
    }

    @Override public @NotNull P id()
    {
        return id;
    }

    @Override public @NotNull X parentId()
    {
        return parentId;
    }

    @Override public int parentSignature()
    {
        return parent().map(PersistableEntity::signature).orElse(0);
    }

    @Override public boolean equals(Object o)
    {
        if (o == null || getClass() != o.getClass()) {return false;}
        InternalSubscriptionEntity<?, ?, ?> that = (InternalSubscriptionEntity<?, ?, ?>) o;
        return Objects.equals(id, that.id);
    }

    @Override public int hashCode()
    {
        return Objects.hashCode(id);
    }
}
