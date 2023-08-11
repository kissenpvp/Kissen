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

package net.kissenpvp.core.ban.event;

import lombok.Getter;
import lombok.Setter;
import net.kissenpvp.core.api.ban.PlayerBan;
import net.kissenpvp.core.api.ban.event.PlayerBanAddCommentEvent;
import net.kissenpvp.core.api.networking.client.entitiy.ServerEntity;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

@Getter @Setter
public class KissenPlayerBanAddCommentEvent implements PlayerBanAddCommentEvent {
    private final PlayerBan playerBan;
    private final ServerEntity sender;

    private Component comment;
    private boolean cancelled;

    public KissenPlayerBanAddCommentEvent(@NotNull PlayerBan playerBan, @NotNull ServerEntity sender, @NotNull Component comment) {
        this.playerBan = playerBan;
        this.sender = sender;
        this.comment = comment;
        this.cancelled = false;
    }


    @Override
    public void setComment(@NotNull Component comment) {
        this.comment = comment;
    }

    @Override
    public void setComment(@NotNull String message) {
        this.comment = Component.text(message);
    }
}
