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
import net.kissenpvp.core.api.event.EventImplementation;
import net.kissenpvp.core.api.message.Comment;
import net.kissenpvp.core.api.networking.client.entitiy.PlayerClient;
import net.kissenpvp.core.api.networking.client.entitiy.ServerEntity;
import net.kissenpvp.core.ban.events.punishment.PunishmentAlterCauseEvent;
import net.kissenpvp.core.ban.events.punishment.PunishmentAlterEndEvent;
import net.kissenpvp.core.ban.events.punishment.PunishmentCommentEvent;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.database.DataWriter;
import net.kissenpvp.core.message.CommentNode;
import net.kissenpvp.core.message.KissenComment;
import net.kissenpvp.core.message.KissenComponentSerializer;
import net.kissenpvp.core.time.KissenTemporalObject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.text.DateFormat;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

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
        return kissenPunishmentNode.cause()
                .toOptional()
                .map(text -> KissenComponentSerializer.getInstance().getJsonSerializer().deserialize(text));
    }

    @Override
    public void setCause(@Nullable Component cause) throws EventCancelledException {
        if (dataWriter == null) {
            throw new EventCancelledException();
        }

        PunishmentAlterCauseEvent<?> punishmentAlterCauseEvent = new PunishmentAlterCauseEvent<>(this, getCause().orElse(null), cause);
        if (!KissenCore.getInstance().getImplementation(EventImplementation.class).call(punishmentAlterCauseEvent)) {
            throw new EventCancelledException(punishmentAlterCauseEvent);
        }

        kissenPunishmentNode.cause().setValue(punishmentAlterCauseEvent.getUpdatedCause().map(component -> JSONComponentSerializer.json().serialize(component)).orElse(null));
        dataWriter.update(kissenPunishmentNode);
    }

    @Override
    public @NotNull @Unmodifiable List<Comment> getComments() {
        return kissenPunishmentNode.comments().stream().map(node -> new KissenComment(node, record -> dataWriter.update(kissenPunishmentNode))).map(kissenComment -> (Comment) kissenComment).toList();
    }

    @Override
    public @NotNull Comment addComment(@NotNull ServerEntity sender, @NotNull Component comment) throws EventCancelledException {
        if (dataWriter == null) {
            throw new EventCancelledException();
        }

        AtomicReference<CommentNode> tempCommentNode = new AtomicReference<>(constructComment(sender, comment));
        KissenComment currentComment = new KissenComment(tempCommentNode.get(), (record) -> tempCommentNode.set((CommentNode) record));

        PunishmentCommentEvent<?> punishmentCommentEvent = new PunishmentCommentEvent<>(this, currentComment);
        if (tempCommentNode.get() == null || !KissenCore.getInstance().getImplementation(EventImplementation.class).call(punishmentCommentEvent)) {
            throw new EventCancelledException(punishmentCommentEvent);
        }

        kissenPunishmentNode.comments().add(tempCommentNode.get());
        dataWriter.update(kissenPunishmentNode);
        return new KissenComment(tempCommentNode.get(), record -> dataWriter.update(kissenPunishmentNode));
    }


    @Override
    public void setEnd(@Nullable Instant end) throws EventCancelledException {
        if (dataWriter == null) {
            throw new EventCancelledException();
        }

        PunishmentAlterEndEvent<?> punishmentAlterEndEvent = new PunishmentAlterEndEvent<>(this, getEnd().orElse(null), end);
        if(!KissenCore.getInstance().getImplementation(EventImplementation.class).call(punishmentAlterEndEvent))
        {
            throw new EventCancelledException(punishmentAlterEndEvent);
        }

        rewriteEnd(punishmentAlterEndEvent.getUpdatedEnd().orElse(null));
        dataWriter.update(kissenPunishmentNode);
    }

    @Override
    public @Unmodifiable Set<UUID> getAffectedPlayers() {
        return null; //TODO
    }

    @Override
    public @NotNull Component getPunishmentText(@NotNull Locale locale) {

        Component banMessage = getCause().map(reason -> Component.translatable("multiplayer.disconnect.banned.cause", reason)).orElse(Component.translatable("multiplayer.disconnect.banned"));
        Optional<TranslatableComponent> optionalEnd = getEnd().map(end -> Component.translatable("multiplayer.disconnect.banned.expiration", DateFormat.getDateInstance(DateFormat.SHORT, locale).format(Date.from(end))));
        if (optionalEnd.isPresent()) {
            banMessage = banMessage.append(optionalEnd.get());
        }
        return banMessage;
    }

    /**
     * Constructs a {@link CommentNode} object with a generated comment ID, current time, sender UUID if the sender is a {@link PlayerClient}, and the provided comment.
     * The comment ID is generated using {@link DataImplementation} obtained from {@link KissenCore}.
     * Current time is obtained with `System.currentTimeMillis()`.
     * Sender UUID is obtained only if the sender is a {@link PlayerClient}.
     * <p>
     * This method should be used when creating a new comment in the system.
     *
     * @param sender the server entity who sends the comment, must not be null. If it's a {@link PlayerClient}, its UUID will be retrieved.
     * @param comment the content of the comment, must not be null.
     * @return a newly constructed {@link CommentNode} object.
     *
     * @see KissenCore
     * @see DataImplementation
     * @see PlayerClient
     * @see CommentNode
     * @see NotNull
     */
    private @NotNull CommentNode constructComment(@NotNull ServerEntity sender, @NotNull Component comment) {
        String id = KissenCore.getInstance().getImplementation(DataImplementation.class).generateID();
        long timeStamp = System.currentTimeMillis();
        UUID senderUUID = sender instanceof PlayerClient<?, ?, ?> playerClient ? playerClient.getUniqueId() : null;
        return new CommentNode(id, comment, senderUUID, timeStamp);
    }
}
