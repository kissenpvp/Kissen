/*
 * Copyright (C) 2023 KissenPvP
 *
 * This program is licensed under the Apache License, Version 2.0.
 *
 * This software may be redistributed and/or modified under the terms
 * of the Apache License as published by the Apache Software Foundation,
 * either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the Apache
 * License, Version 2.0 for the specific language governing permissions
 * and limitations under the License.
 *
 * You should have received a copy of the Apache License, Version 2.0
 * along with this program. If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

package net.kissenpvp.core.user.rank;

import lombok.Getter;
import net.kissenpvp.core.api.database.connection.DatabaseImplementation;
import net.kissenpvp.core.api.database.meta.Meta;
import net.kissenpvp.core.api.database.savable.Savable;
import net.kissenpvp.core.api.database.savable.SavableMap;
import net.kissenpvp.core.api.event.EventCancelledException;
import net.kissenpvp.core.api.user.rank.AbstractRank;
import net.kissenpvp.core.api.user.rank.AbstractRankImplementation;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.database.KissenTable;
import net.kissenpvp.core.user.rank.event.InternalAsyncRankCreateEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * An abstract implementation of the RankImplementation interface for managing ranks in the Kissen framework.
 * This class provides a base structure for handling and caching ranks, with specific implementations required
 * for the concrete types of ranks used in the system.
 *
 * @param <T> The generic type parameter representing the specific type of ranks managed by this implementation.
 */
public abstract class KissenRankImplementation<T extends AbstractRank> implements AbstractRankImplementation<T> {

    protected final Set<T> cached;
    @Getter private KissenTable table;

    /**
     * Constructs a KissenRankImplementation object, initializing the internal set for caching ranks.
     */
    public KissenRankImplementation() {
        this.cached = new HashSet<>();
    }

    @Override
    public boolean preStart() {
        table = (KissenTable) KissenCore.getInstance().getPublicDatabase().createTable("rank_data");
        return AbstractRankImplementation.super.preStart();
    }

    @Override
    public boolean postStart() {
        cached.addAll(fetchRanks());
        return AbstractRankImplementation.super.postStart();
    }

    @Override
    public @NotNull @Unmodifiable Set<T> getRankTemplates() {
        return cached.stream().collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public @NotNull Optional<T> getRankTemplate(@NotNull String name) {
        return cached.stream().filter(rank -> rank.getName().equals(name)).findFirst();
    }

    @Override
    public @NotNull T createRankTemplate(@NotNull String name, @NotNull Map<String, Object> data) {

        T rank = setup(name, data);
        InternalAsyncRankCreateEvent<? extends AbstractRank> createEvent = createEvent(rank);
        if(createEvent.isCancelled())
        {
            rank.delete();
            throw new EventCancelledException();
        }
        cached.add(rank);

        postCreateEvent(rank);
        return rank;
    }

    @Override
    public @NotNull T createRankTemplate(@NotNull String name, int priority) {
        return createRankTemplate(name, Collections.singletonMap("priority", priority));
    }

    @Override
    public @NotNull T getDefaultRankTemplate() {
        return getMeta().getString("default_rank").handle(((string, throwable) -> {
            if (Objects.nonNull(throwable)) {
                return getFallbackRank();
            }
            return getRankTemplate(string).orElseGet(this::getFallbackRank);
        })).join();
    }

    /**
     * Creates an event for asynchronously creating a rank.
     * <p>
     * This method is responsible for creating and executing an instance of {@link InternalAsyncRankCreateEvent}
     * associated with the given rank. The event can be used to manage and control the creation
     * process of the rank, allowing for handling asynchronous operations and cancellations.
     *
     * @param rank the rank object that is in the process of being created
     * @return the previously called instance of {@link InternalAsyncRankCreateEvent} associated with the given rank
     *
     * @see InternalAsyncRankCreateEvent
     * @see AbstractRank
     */
    protected abstract @NotNull InternalAsyncRankCreateEvent<? extends AbstractRank> createEvent(@NotNull T rank);

    /**
     * Executes a post-processing event after the rank creation event has been handled.
     * <p>
     * This method is intended to be called after the rank creation has been processed. It allows for
     * performing any necessary post-creation actions, such as updating caches, notifying listeners, or
     * performing additional setup for the rank.
     *
     * @param rank the rank object that has been successfully created and processed
     */
    protected abstract void postCreateEvent(@NotNull T rank);

    /**
     * Removes a {@link AbstractRank} from the {@link #cached} when {@link AbstractRank#delete()} is called.
     *
     * @param kissenRank the rank to remove from the cached ranks.
     */
    public void removeRank(@NotNull KissenRank kissenRank) {
        cached.removeIf(rank -> rank.getName().equals(kissenRank.getName()));
    }

    /**
     * Fetches and returns an unmodifiable set of ranks using the associated {@link net.kissenpvp.core.api.database.meta.Meta} interface.
     *
     * <p>The {@code fetchRanks} method utilizes the {@link net.kissenpvp.core.api.database.meta.Meta} interface to retrieve rank data,
     * then transforms the data into a set of objects of type {@code T} using the provided setup function.
     * The resulting set is unmodifiable and represents the fetched ranks.</p>
     *
     * @return an unmodifiable set of ranks
     * @throws NullPointerException if the associated {@link net.kissenpvp.core.api.database.meta.Meta} is `null`
     */
    protected @NotNull @Unmodifiable Set<T> fetchRanks() {
        return getMeta().getData(getSavableType()).thenApply(rankData -> {
            Function<SavableMap, T> setup = map -> setup(map.getNotNull("id", String.class), Map.copyOf(map));
            return rankData.values().stream().map(setup).collect(Collectors.toUnmodifiableSet());
        }).join();
    }

    /**
     * Sets up and initializes a rank object based on the provided name and data.
     * This method delegates to the concrete implementation of Savable, obtained via getSavableType().
     *
     * @param name The name associated with the rank.
     * @param data A Map containing key-value pairs of data associated with the rank.
     * @return An initialized rank object of type T.
     */
    protected @NotNull T setup(@NotNull String name, @NotNull Map<String, Object> data) {
        return (T) getSavableType().setup(name, data);
    }

    /**
     * Retrieves the concrete implementation of the Savable interface associated with the ranks.
     * This method must be implemented by subclasses to provide the specific Savable type.
     *
     * @return An instance of Savable representing the type associated with the ranks.
     */
    protected abstract @NotNull Savable<String> getSavableType();

    /**
     * Retrieves a fallback rank in case of errors or when a specific rank is not found.
     * This method must be implemented by subclasses to provide a default or fallback rank.
     *
     * @return A default or fallback rank object of type T.
     */
    protected abstract @NotNull T getFallbackRank();

    public @NotNull Meta getMeta() {
        return getTable().setupMeta(null);
    }
}
