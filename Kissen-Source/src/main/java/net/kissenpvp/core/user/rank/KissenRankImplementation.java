/*
 * Copyright 2023 KissenPvP
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.kissenpvp.core.user.rank;

import net.kissenpvp.core.api.database.meta.ObjectMeta;
import net.kissenpvp.core.api.user.rank.Rank;
import net.kissenpvp.core.api.user.rank.RankImplementation;
import net.kissenpvp.core.base.KissenCore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.stream.Collectors;


public abstract class KissenRankImplementation<T extends Rank> implements RankImplementation<T> {
    private final Set<T> cachedRankIdSet;

    public KissenRankImplementation() {
        this.cachedRankIdSet = new HashSet<>();
    }

    @Override
    public boolean postStart() {
        cachedRankIdSet.addAll(fetchRanks());
        return RankImplementation.super.postStart();
    }

    @Override
    public @NotNull @Unmodifiable Set<T> getRankSet() {
        return cachedRankIdSet.stream().collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public @NotNull Optional<@Nullable T> getRank(@NotNull String name) {
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
    public @NotNull T createRank(@NotNull String name, int priority, @NotNull String chatColor, @NotNull String prefix, @Nullable String suffix) {
        final Map<String, String> data = new HashMap<>();
        data.put("priority", String.valueOf(priority));
        data.put("chat_color", chatColor);
        data.put("prefix", prefix);
        if (suffix != null) {
            data.put("suffix", suffix);
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
    public void removeRank(KissenRank kissenRank) {
        cachedRankIdSet.removeIf(rank -> rank.getName().equals(kissenRank.getName()));
    }

    protected abstract @Unmodifiable @NotNull Set<T> fetchRanks();

    protected abstract @NotNull T getFallbackRank();

    protected abstract @NotNull T setup(String name, Map<String, String> data);
}
