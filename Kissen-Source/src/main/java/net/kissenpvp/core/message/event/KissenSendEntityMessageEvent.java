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

package net.kissenpvp.core.message.event;

import net.kissenpvp.core.api.event.Cancellable;
import net.kissenpvp.core.api.event.EventClass;
import net.kissenpvp.core.api.message.ChatImportance;
import net.kissenpvp.core.api.networking.client.entitiy.ConsoleClient;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class KissenSendEntityMessageEvent implements EventClass, Cancellable {
    private boolean cancelled;
    private final ConsoleClient from, to;
    private ChatImportance importance;
    private Component message;

    public KissenSendEntityMessageEvent(@NotNull ConsoleClient from, @NotNull ConsoleClient to, @NotNull ChatImportance importance, @NotNull Component message) {
        this.from = from;
        this.to = to;
        this.importance = importance;
        this.message = message;
        this.cancelled = false;
    }

    public ConsoleClient getFrom() {
        return from;
    }

    public ConsoleClient getTo() {
        return to;
    }

    public ChatImportance getImportance() {
        return importance;
    }

    public void setImportance(ChatImportance importance) {
        this.importance = importance;
    }

    public Component getMessage() {
        return message;
    }

    public void setMessage(Component message) {
        this.message = message;
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
