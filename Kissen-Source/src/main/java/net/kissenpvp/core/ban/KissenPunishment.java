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

package net.kissenpvp.core.ban;

import net.kissenpvp.core.api.ban.BanOperator;
import net.kissenpvp.core.api.ban.BanType;
import net.kissenpvp.core.api.ban.Punishment;
import net.kissenpvp.core.api.database.DataImplementation;
import net.kissenpvp.core.api.event.EventCancelledException;
import net.kissenpvp.core.api.message.Comment;
import net.kissenpvp.core.api.networking.client.entitiy.PlayerClient;
import net.kissenpvp.core.api.networking.client.entitiy.ServerEntity;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.database.DataWriter;
import net.kissenpvp.core.message.CommentNode;
import net.kissenpvp.core.message.KissenComment;
import net.kissenpvp.core.message.KissenComponentSerializer;
import net.kissenpvp.core.time.KissenTemporalObject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.text.DateFormat;
import java.time.Instant;
import java.util.*;

public abstract class KissenPunishment<T> extends KissenTemporalObject implements Punishment<T> {

    private final UUID totalID;
    private final KissenPunishmentNode kissenPunishmentNode;
    private final DataWriter dataWriter;

    public KissenPunishment(@NotNull UUID totalID, @NotNull KissenPunishmentNode kissenPunishmentNode, @Nullable DataWriter dataWriter) {
        super(kissenPunishmentNode.temporalMeasureNode());
        this.totalID = totalID;
        this.kissenPunishmentNode = kissenPunishmentNode;
        this.dataWriter = dataWriter;
    }

    @Override
    public @NotNull UUID getTotalID() {
        return totalID;
    }

    @Override
    public @NotNull String getID() {
        return kissenPunishmentNode.id();
    }

    @Override
    public @NotNull String getName() {
        return kissenPunishmentNode.banName();
    }

    @Override
    public @NotNull BanOperator getBanOperator() {
        return new BanOperator() {
            @Override
            public @NotNull Component displayName() {
                return kissenPunishmentNode.banOperator().displayName(); //TODO sync player
            }
        };
    }

    @Override
    public @NotNull BanType getBanType() {
        return kissenPunishmentNode.banType();
    }

    @Override
    public @NotNull Optional<Component> getCause() {
        return kissenPunishmentNode.reason()
                .toOptional()
                .map(text -> KissenComponentSerializer.getInstance().getJsonSerializer().deserialize(text));
    }

    @Override
    public void setCause(@Nullable Component cause) throws EventCancelledException {
        if (dataWriter == null) {
            throw new EventCancelledException();
        }

        //TODO event
        kissenPunishmentNode.reason()
                .setValue(Optional.ofNullable(cause)
                        .map(node -> KissenComponentSerializer.getInstance().getJsonSerializer().serialize(node))
                        .orElse(null));
        dataWriter.update(kissenPunishmentNode);
    }

    @Override
    public @NotNull @Unmodifiable List<Comment> getComments() {
        return kissenPunishmentNode.translateComments();
    }

    @Override
    public @NotNull Comment addComment(@NotNull ServerEntity sender, @NotNull Component comment) throws EventCancelledException {
        if (dataWriter == null) {
            throw new EventCancelledException();
        }

        //TODO event
        CommentNode commentNode = constructComment(sender, comment);
        kissenPunishmentNode.comments()
                .add(KissenCore.getInstance().getImplementation(DataImplementation.class).toJson(commentNode));
        dataWriter.update(kissenPunishmentNode);
        return new KissenComment(commentNode, kissenPunishmentNode.commentDataWriter());
    }


    @Override
    public void setEnd(@Nullable Instant end) throws EventCancelledException {
        if(dataWriter == null)
        {
            throw new EventCancelledException();
        }

        rewriteEnd(end);
        dataWriter.update(kissenPunishmentNode);
    }

    @Override
    public @Unmodifiable Set<UUID> getAffectedPlayers() {
        return null; //TODO
    }

    @Override
    public @NotNull Component getPunishmentText(@NotNull Locale locale) {

        Component banMessage = getCause().map(reason -> Component.translatable("multiplayer.disconnect.banned.reason", reason)).orElse(Component.translatable("multiplayer.disconnect.banned"));
        Optional<TranslatableComponent> optionalEnd = getEnd().map(end -> Component.translatable("multiplayer.disconnect.banned.expiration", DateFormat.getDateInstance(DateFormat.SHORT, locale).format(Date.from(end))));
        if(optionalEnd.isPresent())
        {
            banMessage = banMessage.append(optionalEnd.get());
        }
        return banMessage;
    }

    private @NotNull CommentNode constructComment(@NotNull ServerEntity sender, @NotNull Component comment) {
        String id = KissenCore.getInstance().getImplementation(DataImplementation.class).generateID();
        long timeStamp = System.currentTimeMillis();
        UUID senderUUID = sender instanceof PlayerClient<?, ?, ?> playerClient ? playerClient.getUniqueId() : null;
        return new CommentNode(id, comment, senderUUID, timeStamp);
    }
}
