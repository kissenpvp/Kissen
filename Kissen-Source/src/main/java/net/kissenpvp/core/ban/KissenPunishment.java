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
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.time.Duration;
import java.util.*;

public abstract class KissenPunishment<T> implements Punishment<T> {

    private final UUID totalID;
    private final KissenPunishmentNode kissenPunishmentNode;
    private final DataWriter dataWriter;

    public KissenPunishment(@NotNull UUID totalID, @NotNull KissenPunishmentNode kissenPunishmentNode, @Nullable DataWriter dataWriter) {
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
        return null; //TODO
    }

    @Override
    public @NotNull BanType getBanType() {
        return kissenPunishmentNode.banType();
    }

    @Override
    public @NotNull Optional<Component> getReason() {
        return kissenPunishmentNode.reason()
                .toOptional()
                .map(text -> KissenComponentSerializer.getInstance().getMiniSerializer().deserialize(text));
    }

    @Override
    public void setReason(@Nullable Component component) throws EventCancelledException {
        if (dataWriter == null) {
            throw new EventCancelledException();
        }

        //TODO event
        kissenPunishmentNode.reason()
                .setValue(Optional.ofNullable(component)
                        .map(node -> KissenComponentSerializer.getInstance().getMiniSerializer().serialize(node))
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
    public long getStart() {
        return kissenPunishmentNode.start();
    }

    @Override
    public @NotNull Optional<Duration> getDuration() {
        return kissenPunishmentNode.duration().toOptional().map(Duration::ofMillis);
    }

    @Override
    public @NotNull Optional<Long> getEnd() {
        return kissenPunishmentNode.end().toOptional();
    }

    @Override
    public void setEnd(@Nullable Long end) throws EventCancelledException {
        if (dataWriter == null) {
            throw new EventCancelledException();
        }

        //TODO event
        kissenPunishmentNode.end().setValue(end);
    }

    @Override
    public @NotNull Optional<Long> getPredictedEnd() {
        return Optional.ofNullable(kissenPunishmentNode.predictedEnd());
    }

    @Override
    public boolean isValid() {
        return getEnd().map(end -> System.currentTimeMillis() < end).orElse(true);
    }

    @Override
    public @Unmodifiable Set<UUID> getAffectedPlayers() {
        return null; //TODO
    }

    private @NotNull CommentNode constructComment(@NotNull ServerEntity sender, @NotNull Component comment) {
        String id = KissenCore.getInstance().getImplementation(DataImplementation.class).generateID();
        long timeStamp = System.currentTimeMillis();
        UUID senderUUID = sender instanceof PlayerClient<?, ?, ?> playerClient ? playerClient.getUniqueId() : null;
        return new CommentNode(id, comment, senderUUID, timeStamp);
    }
}
