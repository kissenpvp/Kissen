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

import net.kissenpvp.core.api.database.meta.ObjectMeta;
import net.kissenpvp.core.api.database.savable.Savable;
import net.kissenpvp.core.api.database.savable.SavableMap;
import net.kissenpvp.core.api.user.rank.Rank;
import net.kissenpvp.core.api.user.rank.RankImplementation;
import net.kissenpvp.core.base.KissenCore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * An abstract implementation of the RankImplementation interface for managing ranks in the Kissen framework.
 * This class provides a base structure for handling and caching ranks, with specific implementations required
 * for the concrete types of ranks used in the system.
 *
 * @param <T> The generic type parameter representing the specific type of ranks managed by this implementation.
 */
public abstract class KissenRankImplementation<T extends Rank> implements RankImplementation<T> {
    protected final Set<T> cachedRankIdSet;

    /**
     * Constructs a KissenRankImplementation object, initializing the internal set for caching ranks.
     */
    public KissenRankImplementation() {
        this.cachedRankIdSet = new HashSet<>();
    }

    @Override
    public boolean postStart() {
        fetchRanks();
        return RankImplementation.super.postStart();
    }

    @Override
    public @NotNull @Unmodifiable Set<T> getRankSet() {
        return cachedRankIdSet.stream().collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public @NotNull Optional<T> getRank(@NotNull String name) {
        return cachedRankIdSet.stream().filter(rank -> rank.getName().equals(name)).findFirst();
    }

    @Override
    public @NotNull T createRank(@NotNull String name, @NotNull Map<String, String> data) {
        T rank = setup(name, data);
        cachedRankIdSet.removeIf(currentRank -> currentRank.equals(rank));
        cachedRankIdSet.add(rank);
        return rank;
    }

    @Override
    public @NotNull T createRank(@NotNull String name, int priority, @NotNull TextColor chatColor, @NotNull Component prefix, @Nullable Component suffix) {
        final Map<String, String> data = new HashMap<>();
        data.put("priority", String.valueOf(priority));
        data.put("chat_color", chatColor.asHexString());
        data.put("prefix", JSONComponentSerializer.json().serialize(prefix));
        if (suffix != null) {
            data.put("suffix", JSONComponentSerializer.json().serialize(suffix));
        }
        return createRank(name, data);
    }

    @Override
    public @NotNull T getDefaultRank() {
        return getRankSet().stream().filter(currentRank -> ((KissenRank) currentRank).containsKey("default")).findFirst().orElseGet(this::getFallbackRank);
    }

    /**
     * Returns the {@link ObjectMeta} which contains the {@link Rank} data.
     *
     * @return the object meta containing the rank data.
     */
    protected ObjectMeta getMeta() {
        return KissenCore.getInstance().getPublicMeta();
    }

    /**
     * Removes a {@link Rank} from the {@link #cachedRankIdSet} when {@link Rank#delete()} is called.
     *
     * @param kissenRank the rank to remove from the cached ranks.
     */
    public void removeRank(@NotNull KissenRank kissenRank) {
        cachedRankIdSet.removeIf(rank -> rank.getName().equals(kissenRank.getName()));
    }

    /**
     * Asynchronously fetches ranks from the database using the associated metadata.
     * Populates the internal cache with the retrieved ranks.
     */
    protected void fetchRanks()
    {
        getMeta().getData(getSavableType()).thenAccept((data) ->
        {
            for(SavableMap savableMap : data.values())
            {
                cachedRankIdSet.add(setup(savableMap.getNotNull("id"), savableMap));
            }
            KissenCore.getInstance().getLogger().info("Successfully loaded {} rank(s) from the database.", cachedRankIdSet.size());
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
    protected @NotNull T setup(@NotNull String name, @NotNull Map<String, String> data)
    {
        Savable savable = getSavableType();
        savable.setup(name, data);
        return (T) savable;
    }


    /**
     * Retrieves the concrete implementation of the Savable interface associated with the ranks.
     * This method must be implemented by subclasses to provide the specific Savable type.
     *
     * @return An instance of Savable representing the type associated with the ranks.
     */
    protected abstract @NotNull Savable getSavableType();

    /**
     * Retrieves a fallback rank in case of errors or when a specific rank is not found.
     * This method must be implemented by subclasses to provide a default or fallback rank.
     *
     * @return A default or fallback rank object of type T.
     */
    protected abstract @NotNull T getFallbackRank();
}
