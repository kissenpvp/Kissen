package net.kissenpvp.api.network.actor;

import com.google.gson.JsonObject;
import org.jspecify.annotations.NonNull;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Represents a set of properties associated with a player.
 * <p>
 * This interface extends the {@link Map} interface to provide a key-value
 * representation of player-specific data, where each key is a {@code String}
 * and the corresponding value is a {@link JsonObject}.
 * <p>
 * In addition to the standard map operations, this interface offers methods
 * for retrieving the associated {@link PlayerClient}, checking if there are
 * unsaved changes, and persisting changes asynchronously.
 *
 * @author Ivo Quiring
 */
public interface PlayerProperties extends Map<String, JsonObject>
{
    /**
     * Retrieves the {@link PlayerClient} associated with the current player properties.
     * <p>
     * This method provides access to the underlying player client, which includes
     * details such as account linkage, login history, and other player-specific information.
     *
     * @return a non-null {@link PlayerClient} representing the player client linked to these properties.
     */
    @NonNull PlayerClient player();

    /**
     * Indicates whether the player properties have unsaved changes.
     * <p>
     * This method checks if there are any modifications made to the player properties
     * that have not been persisted to the underlying storage system.
     *
     * @return {@code true} if there are unsaved changes, {@code false} otherwise.
     */
    boolean unsaved();

    /**
     * Persists the current state of the player properties to the underlying storage system.
     * <p>
     * This operation is asynchronous and returns a {@link CompletableFuture} that completes
     * when the save operation has been successfully executed.
     *
     * @return a {@link CompletableFuture} that will complete with {@code null} upon successful persistence
     * or complete exceptionally if an error occurs during the save process.
     */
    @NonNull CompletableFuture<Void> save();
}
