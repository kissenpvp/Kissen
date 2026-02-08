package net.kissenpvp.api.database;

import org.jspecify.annotations.NonNull;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

public interface DatabaseQueue
{
    <T> @NonNull CompletableFuture<T> submit(@NonNull Callable<T> task);

}
