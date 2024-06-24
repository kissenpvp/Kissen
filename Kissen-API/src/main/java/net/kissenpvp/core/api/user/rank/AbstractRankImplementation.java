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

package net.kissenpvp.core.api.user.rank;

import net.kissenpvp.core.api.base.Implementation;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.Set;


public interface AbstractRankImplementation<T extends AbstractRank> extends Implementation
{
    @NotNull Set<T> getRankTemplates();

    @NotNull Optional<T> getRankTemplate(@NotNull String name);

    @NotNull T createRankTemplate(@NotNull String name, @NotNull Map<String, Object> data);

    @NotNull T createRankTemplate(@NotNull String name, int priority);

    @NotNull T getDefaultRankTemplate();
}
