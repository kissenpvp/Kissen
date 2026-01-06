package net.kissenpvp.api.database;

import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public interface DeletableRepository<P>
{
    /**
     * Deletes a single entry from the repository.
     * <p>
     * The returned {@link CompletableFuture} completes successfully
     * when the entry has been deleted. If the deletion fails, the future
     * is completed exceptionally.
     *
     * @param entry the entry to delete; must not be {@code null}
     * @return a {@link CompletableFuture} that completes when the deletion operation has finished
     * @throws NullPointerException if {@code entry} is {@code null}
     */
    @NonNull CompletableFuture<Void> delete(@NonNull P entry);

    /**
     * Deletes all entries provided by the given iterable.
     *<p>
     * The deletion may be performed sequentially or in bulk, depending
     * on the implementation. The returned {@link CompletableFuture}
     * completes when all entries have been processed.
     * <p>
     * If deletion of any entry fails, the future is completed exceptionally.
     *
     * @param iterable the entries to delete; must not be {@code null}
     * @return a {@link CompletableFuture} that completes when all deletions have finished
     * @throws NullPointerException if {@code iterable} is {@code null}
     */
    @NonNull CompletableFuture<Void> deleteAll(@NonNull Iterable<P> iterable);
}
