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

import net.kissenpvp.core.api.ban.AbstractPunishment;
import net.kissenpvp.core.api.ban.BanType;
import net.kissenpvp.core.api.database.DataWriter;
import net.kissenpvp.core.api.database.queryapi.Column;
import net.kissenpvp.core.api.database.queryapi.select.QuerySelect;
import net.kissenpvp.core.api.event.EventCancelledException;
import net.kissenpvp.core.api.message.Comment;
import net.kissenpvp.core.api.networking.client.entitiy.PlayerClient;
import net.kissenpvp.core.api.networking.client.entitiy.ServerEntity;
import net.kissenpvp.core.api.time.KissenTemporalObject;
import net.kissenpvp.core.api.user.UserImplementation;
import net.kissenpvp.core.api.user.UserInfo;
import net.kissenpvp.core.ban.events.punishment.PunishmentAlterCauseEvent;
import net.kissenpvp.core.ban.events.punishment.PunishmentAlterEndEvent;
import net.kissenpvp.core.ban.events.punishment.PunishmentCommentEvent;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.event.EventImplementation;
import net.kissenpvp.core.message.CommentNode;
import net.kissenpvp.core.message.KissenComment;
import net.kissenpvp.core.user.KissenUserImplementation;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.text.DateFormat;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public abstract class KissenPunishment<T> extends KissenTemporalObject implements AbstractPunishment<T> {

    private final UUID totalID;
    private final KissenPunishmentNode punishmentNode;
    private final DataWriter<KissenPunishmentNode> dataWriter;

    public KissenPunishment(@NotNull UUID totalID, @NotNull KissenPunishmentNode punishmentNode, @Nullable DataWriter<KissenPunishmentNode> dataWriter) {
        super(punishmentNode.temporalMeasure());
        this.totalID = totalID;
        this.punishmentNode = punishmentNode;
        this.dataWriter = dataWriter;
    }

    @Override
    public @NotNull UUID getTotalID() {
        return totalID;
    }

    @Override
    public @NotNull String getID() {
        return punishmentNode.id();
    }

    @Override
    public @NotNull String getName() {
        return punishmentNode.banName();
    }

    @Override
    public @NotNull String getBanOperator() {

        String data = punishmentNode.operator();
        if (data.matches("[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}")) {
            return KissenCore.getInstance().getImplementation(UserImplementation.class).getCachedUserProfile(UUID.fromString(data)).map(UserInfo::getName).orElse(data);
        }
        return punishmentNode.operator();
    }

    @Override
    public @NotNull BanType getBanType() {
        return punishmentNode.banType();
    }

    @Override
    public @NotNull Optional<Component> getCause() {
        return punishmentNode.cause().toOptional();
    }

    @Override
    public void setCause(@Nullable Component cause) throws EventCancelledException {
        DataWriter.validate(dataWriter, new EventCancelledException());

        PunishmentAlterCauseEvent<?> punishmentAlterCauseEvent = new PunishmentAlterCauseEvent<>(this, getCause().orElse(null), cause);
        if (!KissenCore.getInstance().getImplementation(EventImplementation.class).call(punishmentAlterCauseEvent)) {
            throw new EventCancelledException(punishmentAlterCauseEvent);
        }

        punishmentNode.cause().setValue(punishmentAlterCauseEvent.getUpdatedCause().orElse(null));
        dataWriter.update(punishmentNode);
    }

    @Override
    public @NotNull @Unmodifiable List<Comment> getComments() {
        return punishmentNode.comments().stream().map(node -> new KissenComment(node, record -> dataWriter.update(punishmentNode))).map(kissenComment -> (Comment) kissenComment).toList();
    }

    @Override
    public @NotNull Comment addComment(@NotNull ServerEntity sender, @NotNull Component comment) throws EventCancelledException {
        DataWriter.validate(dataWriter, new EventCancelledException());

        AtomicReference<CommentNode> tempCommentNode = new AtomicReference<>(constructComment(sender, comment));
        KissenComment currentComment = new KissenComment(tempCommentNode.get(), tempCommentNode::set);

        PunishmentCommentEvent<?> punishmentCommentEvent = new PunishmentCommentEvent<>(this, currentComment);
        if (tempCommentNode.get()==null || !KissenCore.getInstance().getImplementation(EventImplementation.class).call(punishmentCommentEvent)) {
            throw new EventCancelledException(punishmentCommentEvent);
        }

        punishmentNode.comments().add(tempCommentNode.get());
        dataWriter.update(punishmentNode);
        return new KissenComment(tempCommentNode.get(), record -> dataWriter.update(punishmentNode));
    }


    @Override
    public void setEnd(@Nullable Instant end) throws EventCancelledException {
        DataWriter.validate(dataWriter, new EventCancelledException());

        PunishmentAlterEndEvent<?> punishmentAlterEndEvent = new PunishmentAlterEndEvent<>(this, getEnd().orElse(null), end);
        if (!KissenCore.getInstance().getImplementation(EventImplementation.class).call(punishmentAlterEndEvent)) {
            throw new EventCancelledException(punishmentAlterEndEvent);
        }

        rewriteEnd(punishmentAlterEndEvent.getUpdatedEnd().orElse(null));
        dataWriter.update(punishmentNode);
    }

    @Override
    public @Unmodifiable Set<UUID> getAffectedPlayers() {
        KissenBanImplementation<?, ?> banSystem = KissenCore.getInstance().getImplementation(KissenBanImplementation.class);
        QuerySelect select = banSystem.getMeta().select(Column.TOTAL_ID).whereExact(Column.KEY, "total_id").andExact(Column.VALUE, getTotalID().toString());
        KissenUserImplementation userImplementation = KissenCore.getInstance().getImplementation(KissenUserImplementation.class);
        return Arrays.stream(select.execute().join()).map(obj -> {
            String id = String.valueOf(obj[0]).substring(userImplementation.getUserSaveID().length());
            return UUID.fromString(id);
        }).collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public @NotNull Component getPunishmentText(@NotNull Locale locale) {

        final net.kyori.adventure.text.ComponentBuilder<?, ?> banMessage = Component.empty().toBuilder();
        switch (getBanType()) {
            case BAN -> {
                banMessage.append(getCause().map(reason -> Component.translatable("multiplayer.disconnect.banned.cause", reason)).orElse(Component.translatable("multiplayer.disconnect.banned")).toBuilder());
                Optional<TranslatableComponent> optionalEnd = getEnd().map(end ->
                {
                    String date = DateFormat.getDateInstance(DateFormat.LONG, locale).format(Date.from(end));
                    return Component.translatable("multiplayer.disconnect.banned.expiration", Component.text(date));
                });
                optionalEnd.ifPresent(banMessage::append);
            }
            case MUTE -> {
                banMessage.append(Component.translatable("chat.filtered"));
                getCause().ifPresent(cause -> banMessage.appendNewline().append(cause));
            }
            case KICK -> {
                //TODO
            }
        }
        return banMessage.asComponent();
    }

    private @NotNull CommentNode constructComment(@NotNull ServerEntity sender, @NotNull Component comment) {
        String id = UUID.randomUUID().toString().split("-")[1];
        UUID senderUUID = sender instanceof PlayerClient<?, ?, ?> playerClient ? playerClient.getUniqueId():null;

        return new CommentNode(id, comment, senderUUID, System.currentTimeMillis());
    }

    @Override
    public boolean isValid() {
        return !getBanType().equals(BanType.KICK) && super.isValid();
    }
}
