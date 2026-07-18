package net.kissenpvp.database;

import net.kissenpvp.api.database.DatabaseQueue;
import net.kissenpvp.base.KissenCore;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.*;

public class AsyncDatabaseQueue implements DatabaseQueue
{
    private final ExecutorService executorService;

    public static <T> @NonNull CompletableFuture<T> submitTask(@NonNull Callable<T> task)
    {
        return KissenCore.getInstance().databaseQueue().submit(task);
    }

    public AsyncDatabaseQueue()
    {
        this.executorService = Executors.newSingleThreadExecutor();
    }

    @Override
    public <T> @NonNull CompletableFuture<T> submit(@NonNull Callable<T> task)
    {
        CompletableFuture<T> future = new CompletableFuture<>();

        try (ExecutorService service = service())
        {
            service.submit(() -> {
                try
                {
                    T result = task.call();
                    future.complete(result);
                }
                catch (Exception exception)
                {
                    future.completeExceptionally(exception);
                }
            });
        }

        return future;
    }

    public void shutdown() {
        try (ExecutorService service = service())
        {
            service.shutdown();
        }
    }

    protected @NonNull ExecutorService service()
    {
        return executorService;
    }
}
