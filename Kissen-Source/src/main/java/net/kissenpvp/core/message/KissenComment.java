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

import net.kissenpvp.core.api.message.Comment;
import net.kissenpvp.core.api.networking.client.entitiy.ServerEntity;
import net.kissenpvp.core.database.DataWriter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public class KissenComment implements Comment {
    private final CommentNode commentNode;
    private final DataWriter dataWriter;

    public KissenComment(@NotNull CommentNode commentNode, @Nullable DataWriter dataWriter) {
        this.commentNode = commentNode;
        this.dataWriter = dataWriter;
        Component component = null;
    }

    @Override
    public @NotNull String getID() {
        return commentNode.id();
    }

    @Override
    public @NotNull ServerEntity getSender() {
        return commentNode.getSender();
    }

    @Override
    public @Unmodifiable @NotNull List<Component> getEdits() {
        return commentNode.messages()
                .stream()
                .map(message -> JSONComponentSerializer.json().deserialize(message.message()))
                .toList();
    }

    @Override
    public @NotNull Component getText() {
        return JSONComponentSerializer.json().deserialize(commentNode.messages().get(commentNode.messages().size() - 1).message());
    }

    @Override
    public void setText(@NotNull String text) {
        setText(Component.text(text));
    }

    @Override
    public void setText(@NotNull Component component) {
        if (dataWriter == null) {
            throw new UnsupportedOperationException("This object is immutable.");
        }

        commentNode.messages().add(new CommentMessageNode(JSONComponentSerializer.json().serialize(component), System.currentTimeMillis()));
        dataWriter.update(commentNode);
    }

    @Override
    public long getTimeStamp() {
        return commentNode.timeStamp();
    }

    @Override
    public boolean hasBeenEdited() {
        return commentNode.messages().size() > 1;
    }

    @Override
    public boolean hasBeenDeleted() {
        return commentNode.hasBeenDeleted().getValue();
    }

    @Override
    public void setDeleted(boolean deleted) {
        if (dataWriter == null) {
            throw new UnsupportedOperationException("This object is immutable.");
        }
        commentNode.hasBeenDeleted().setValue(deleted);
        dataWriter.update(commentNode);
    }
}
