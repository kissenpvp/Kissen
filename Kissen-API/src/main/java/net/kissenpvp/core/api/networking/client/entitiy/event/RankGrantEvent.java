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

package net.kissenpvp.core.api.networking.client.entitiy.event;

import net.kissenpvp.core.api.event.Cancellable;
import net.kissenpvp.core.api.event.EventClass;
import net.kissenpvp.core.api.networking.client.entitiy.PlayerClient;
import net.kissenpvp.core.api.user.rank.PlayerRank;
import org.jetbrains.annotations.NotNull;

public class RankGrantEvent implements EventClass, Cancellable {
    private final PlayerClient<?> playerClient;
    private PlayerRank<?> playerRank;
    boolean cancelled;

    public RankGrantEvent(@NotNull PlayerClient<?> playerClient, @NotNull PlayerRank<?> playerRank) {
        this.playerClient = playerClient;
        this.playerRank = playerRank;
        this.cancelled = false;
    }

    public PlayerClient<?> getPlayerClient() {
        return playerClient;
    }

    public PlayerRank<?> getPlayerRank() {
        return playerRank;
    }

    public void setPlayerRank(PlayerRank<?> playerRank) {
        this.playerRank = playerRank;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
