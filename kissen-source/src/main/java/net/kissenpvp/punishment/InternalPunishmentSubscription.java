package net.kissenpvp.punishment;

import net.kissenpvp.api.network.actor.Actor;
import net.kissenpvp.api.network.actor.PlayerClient;
import net.kissenpvp.api.punishment.Punishment;
import net.kissenpvp.api.punishment.PunishmentSubscription;
import net.kissenpvp.api.temporal.WritableTemporalObject;
import net.kissenpvp.base.KissenCore;
import net.kissenpvp.database.InternalSubscriptionEntity;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class InternalPunishmentSubscription extends InternalSubscriptionEntity<String, Integer, Punishment> implements PunishmentSubscription
{
    private final String id;
    private final UUID linkId;
    private final UUID operator, player;
    private final @NotNull WritableTemporalObject temporalObject;
    private @Nullable Component message;

    public InternalPunishmentSubscription(
            int parent,
            @NotNull UUID linkId,
            @NotNull UUID player,
            @Nullable UUID operator,
            @NotNull WritableTemporalObject temporalObject,
            @Nullable Component message
    ) throws NullPointerException {
        this(String.valueOf(UUID.randomUUID()).split("-")[0], parent, linkId, player, operator, temporalObject, message);
    }

    public InternalPunishmentSubscription(
            @NotNull String id,
            int parent,
            @NotNull UUID linkId,
            @NotNull UUID player,
            @Nullable UUID operator,
            @NotNull WritableTemporalObject temporalObject,
            @Nullable Component message
    ) throws NullPointerException {
        super(id, parent);
        Objects.requireNonNull(id, "Id cannot be null.");
        Objects.requireNonNull(linkId, "LinkId cannot be null.");
        Objects.requireNonNull(temporalObject, "TimeSpan cannot be null.");

        if (id.length() > 8)
        {
            throw new IllegalArgumentException("Id cannot be longer than 4 characters!");
        }

        this.id = id;
        this.linkId = linkId;
        this.player = player;
        this.operator = operator;
        this.temporalObject = temporalObject;
        this.message = message;
    }

    @Override public @NotNull String id()
    {
        return id;
    }

    @Override public @NotNull UUID linkId()
    {
        return linkId;
    }

    @Override public @NotNull PlayerClient target()
    {
        PlayerClient playerClient = KissenCore.getInstance().playerRepository().find(player).join();
        if(Objects.isNull(playerClient))
        {
            String message = "The player %s who is associated with the punishment subscription %s has not been found in the database.";
            throw new IllegalStateException(String.format(message, player, id()));
        }
        return playerClient;
    }

    @Override public @NotNull Actor operator()
    {
        if(Objects.isNull(operator))
        {
            return KissenCore.getInstance().console();
        }

        Actor actor = KissenCore.getInstance().playerRepository().find(operator).join();
        if(Objects.isNull(actor))
        {
            String message = "The player with the id %s was not found in the database but is bound to the punishment subscription %s.";
            throw new IllegalStateException(String.format(message, operator, id()));
        }

        return actor;
    }

    public @Nullable UUID rawOperator()
    {
        return operator;
    }

    @Override public int signature()
    {
        return Objects.hash(linkId, temporalObject, message);
    }

    @Override public @NotNull Optional<Punishment> parent()
    {
        return Optional.ofNullable(KissenCore.getInstance().punishmentRepository().find(parentId()).join());
    }

    @Override public @NotNull Optional<Component> message()
    {
        return Optional.ofNullable(message);
    }

    @Override public void message(@Nullable Component component)
    {
        this.message = component;
    }

    @Override public void unsetMessage()
    {
        message(null);
    }

    @Override
    public @NotNull WritableTemporalObject temporal() {
        return temporalObject;
    }
}
