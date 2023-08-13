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

package net.kissenpvp.core.message;

import net.kissenpvp.core.api.message.Comment;
import net.kissenpvp.core.api.networking.client.entitiy.ServerEntity;
import net.kissenpvp.core.api.user.UserImplementation;
import net.kissenpvp.core.api.util.Container;
import net.kissenpvp.core.base.KissenCore;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record CommentNode(@NotNull String id, @NotNull List<CommentMessageNode> messages, @Nullable UUID sender,
                          long timeStamp,
                          Container<Boolean> hasBeenDeleted) {

    public CommentNode(@NotNull String id, @NotNull List<CommentMessageNode> messages, @Nullable UUID sender, long timeStamp, Container<Boolean> hasBeenDeleted) {
        this.id = id;
        this.messages = new ArrayList<>(messages);
        this.sender = sender;
        this.timeStamp = timeStamp;
        this.hasBeenDeleted = hasBeenDeleted;
    }

    public CommentNode(@NotNull String id, @NotNull Component comment, @Nullable UUID sender, long timeStamp) {
        this(id, List.of(new CommentMessageNode(KissenComponentSerializer.getInstance().getJsonSerializer().serialize(comment), timeStamp)), sender, timeStamp, new Container<>(false));
    }

    public ServerEntity getSender() {
        return sender == null ? KissenCore.getInstance().getConsole() : KissenCore.getInstance().getImplementation(UserImplementation.class).getUser(sender).getPlayerClient();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        CommentNode that = (CommentNode) object;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
