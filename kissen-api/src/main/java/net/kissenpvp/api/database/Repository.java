package net.kissenpvp.api.database;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public interface Repository<P, T extends PersistableEntity<P>>
{
    @NotNull CompletableFuture<T> find(@NotNull P id) throws NullPointerException;

    @NotNull CompletableFuture<@UnmodifiableView Collection<T>> findAll(@NotNull Iterable<P> id)  throws NullPointerException;

    @NotNull CompletableFuture<@UnmodifiableView Collection<T>> findAll();

    @NotNull CompletableFuture<Boolean> has(@NotNull P id)  throws NullPointerException;

    @NotNull CompletableFuture<Void> save(@NotNull T id)  throws NullPointerException;

    @NotNull CompletableFuture<Void> saveAll(@NotNull Iterable<T> id) throws NullPointerException;

    @NotNull String table();
}
