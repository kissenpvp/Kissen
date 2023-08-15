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

package net.kissenpvp.core.api.user.rank;

import net.kissenpvp.core.api.base.Implementation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.Set;


public interface RankImplementation<T extends Rank> extends Implementation
{
    @NotNull Set<T> getRankSet();

    @NotNull Optional<T> getRank(@NotNull String name);

    @NotNull T createRank(@NotNull String name, @NotNull Map<String, String> data);

    @NotNull T createRank(@NotNull String name, int priority, @NotNull String chatColor, @NotNull String prefix, @Nullable String suffix);

    @NotNull T getDefaultRank();
}
