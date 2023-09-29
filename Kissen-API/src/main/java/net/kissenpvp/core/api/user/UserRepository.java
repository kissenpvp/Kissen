package net.kissenpvp.core.api.user;

import net.kissenpvp.core.api.database.meta.BackendException;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface UserRepository {

    @NotNull CompletableFuture<Optional<User>> getUser(@NotNull String name) throws BackendException;

    @NotNull CompletableFuture<Optional<User>> getUser(@NotNull UUID uuid);

}
