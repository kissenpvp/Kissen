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

package net.kissenpvp.core.message;

import net.kissenpvp.core.api.networking.client.entitiy.ServerEntity;
import net.kissenpvp.core.api.user.UserImplementation;
import net.kissenpvp.core.api.util.Container;
import net.kissenpvp.core.ban.KissenPunishmentNode;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.time.KissenAccurateDuration;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record CommentNode(@NotNull String id, @NotNull List<CommentMessageNode> messages, @Nullable UUID sender,
                          @NotNull Instant timeStamp,
                          @NotNull Container<Boolean> hasBeenDeleted) {

    public CommentNode(@NotNull String id, @NotNull List<CommentMessageNode> messages, @Nullable UUID sender, @NotNull Instant timeStamp, @NotNull Container<Boolean> hasBeenDeleted) {
        this.id = id;
        this.messages = new ArrayList<>(messages);
        this.sender = sender;
        this.timeStamp = timeStamp;
        this.hasBeenDeleted = hasBeenDeleted;
    }

    public CommentNode(@NotNull String id, @NotNull Component comment, @Nullable UUID sender, @NotNull Instant timeStamp) {
        this(id, List.of(new CommentMessageNode(comment, timeStamp)), sender, timeStamp, new Container<>(false));
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
