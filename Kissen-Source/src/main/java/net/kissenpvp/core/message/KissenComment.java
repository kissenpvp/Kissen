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
import net.kissenpvp.core.api.message.ComponentSerializer;
import net.kissenpvp.core.api.networking.client.entitiy.ServerEntity;
import net.kissenpvp.core.database.DataWriter;
import net.kyori.adventure.text.Component;
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
                .map(message -> ComponentSerializer.getInstance().getJsonSerializer().deserialize(message))
                .toList();
    }

    @Override
    public @NotNull Component getText() {
        return ComponentSerializer.getInstance()
                .getJsonSerializer()
                .deserialize(commentNode.messages().get(commentNode.messages().size() - 1));
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

        commentNode.messages().add(ComponentSerializer.getInstance().getJsonSerializer().serialize(component));
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
