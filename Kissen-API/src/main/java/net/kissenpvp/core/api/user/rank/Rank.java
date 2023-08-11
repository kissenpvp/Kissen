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

import net.kissenpvp.core.api.database.meta.BackendException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;


public interface Rank {
    @NotNull String getName();

    int getPriority();

    void setPriority(int priority);

    @NotNull Optional<Component> getPrefix();

    void setPrefix(@Nullable Component prefix);

    @NotNull NamedTextColor getChatColor();

    void setChatColor(@NotNull NamedTextColor chatColor);

    @NotNull Optional<Component> getSuffix();

    void setSuffix(@Nullable Component suffix);

    int delete() throws BackendException;
}
