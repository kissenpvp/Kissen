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

package net.kissenpvp.core.api.message;

import net.kissenpvp.core.api.base.Implementation;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public interface ChatImplementation extends Implementation {

    void broadcast(@NotNull String message);

    void broadcast(@Nullable String permission, @NotNull String message);

    void broadcast(@Nullable ChatImportance chatImportance, @NotNull String message);

    void broadcast(@Nullable ChatImportance chatImportance, @Nullable String permission, @NotNull String message);

    void broadcast(@NotNull Component chatComponent);

    void broadcast(@NotNull String permission, @NotNull Component chatComponent);

    void broadcast(@Nullable ChatImportance chatImportance, @NotNull Component chatComponent);

    void broadcast(@Nullable ChatImportance chatImportance, @Nullable String permission, @NotNull Component chatComponent);

    void notifyTeam(@NotNull String message);

    void notifyTeam(@NotNull Component chatComponent);

    void notifyTeam(@Nullable ChatImportance chatImportance, @NotNull String message);

    void notifyTeam(@Nullable ChatImportance chatImportance, @NotNull Component chatComponent);

    void notifySupport(@NotNull String message);

    void notifySupport(@NotNull Component chatComponent);

    void notifySupport(@Nullable ChatImportance chatImportance, @NotNull String message);

    void notifySupport(@Nullable ChatImportance chatImportance, @NotNull Component chatComponent);
}
