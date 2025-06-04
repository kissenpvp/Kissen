package net.kissenpvp.api.database;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public interface Repository<P, T extends PersistableEntity<P>>
{
    @NotNull CompletableFuture<T> find(@NotNull P id);

    @NotNull CompletableFuture<@UnmodifiableView Collection<T>> findAll(@NotNull Iterable<P> id);

    @NotNull CompletableFuture<@UnmodifiableView Collection<T>> findAll();

    @NotNull CompletableFuture<Boolean> has(@NotNull P id);

    @NotNull CompletableFuture<Void> save(@NotNull T id);

    @NotNull CompletableFuture<Void> saveAll(@NotNull Iterable<T> id);

    @NotNull String table();
}
