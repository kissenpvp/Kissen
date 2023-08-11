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
import net.kissenpvp.core.api.ban.event.PlayerBanReasonUpdateEvent;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;


public class KissenPlayerBanReasonUpdateUpdateEvent implements PlayerBanReasonUpdateEvent {

    @Getter private final PlayerBan playerBan;
    private Component reason;
    @Getter @Setter private boolean cancelled;

    public KissenPlayerBanReasonUpdateUpdateEvent(@NotNull PlayerBan playerBan, @Nullable Component reason) {
        this.playerBan = playerBan;
        this.reason = reason;
        this.cancelled = false;
    }

    @Override
    public @NotNull Optional<@Nullable Component> getReason() {
        return Optional.ofNullable(reason);
    }

    @Override
    public void setReason(@NotNull Component reason) {
        this.reason = reason;
    }

    @Override
    public void setReason(@NotNull String reason) {
        setReason(Component.text(reason));
    }
}