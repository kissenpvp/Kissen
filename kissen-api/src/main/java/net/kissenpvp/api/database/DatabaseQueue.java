package net.kissenpvp.api.database;

import org.jspecify.annotations.NonNull;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

/**
 * Executes database-related tasks asynchronously using a
 * {@link java.util.concurrent.ExecutorService}.
 *
 * <p>
 * Each operation returns a {@link java.util.concurrent.CompletableFuture}
 * that is completed when the task finishes.
 *
 * @author Ivo Quiring
 */
public interface DatabaseQueue
{
    /**
     * Submits a task for asynchronous execution.
     *
     * <p>
     * The given {@link java.util.concurrent.Callable} is executed on a background
     * thread managed by this service. The result of the computation is made available
     * through a {@link java.util.concurrent.CompletableFuture} once the task completes.
     *
     * <p>
     * If the task completes successfully, the returned future is completed with its result.
     * Otherwise, the task throws an exception, the future is completed exceptionally.
     *
     * @param task the computation to execute asynchronously; must not be {@code null}
     * @param <T> the type of the computation result
     * @return a non-null {@link java.util.concurrent.CompletableFuture} representing the pending result
     */
    <T> @NonNull CompletableFuture<T> submit(@NonNull Callable<T> task);
}
