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
