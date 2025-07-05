package net.kissenpvp.api.database;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface SubscriptionEntity<P, X, T extends PersistableEntity<X>> extends PersistableEntity<P>
{
    @NotNull Optional<T> parent();

    @NotNull X parentId();

    int parentSignature();

}
