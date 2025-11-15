package net.kissenpvp.punishment;

import com.google.common.base.Preconditions;
import net.kissenpvp.api.network.actor.Actor;
import net.kissenpvp.api.network.actor.PlayerClient;
import net.kissenpvp.api.punishment.Punishment;
import net.kissenpvp.api.punishment.PunishmentSubscription;
import net.kissenpvp.api.temporal.WritableTemporalObject;
import net.kissenpvp.base.KissenCore;
import net.kissenpvp.database.InternalSubscriptionEntity;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;


import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class InternalPunishmentSubscription extends InternalSubscriptionEntity<String, Integer, Punishment> implements PunishmentSubscription
{
    private final String id;
    private final UUID linkId;
    private final UUID operator;
    private final @NonNull WritableTemporalObject temporalObject;
    private @Nullable Component message;

    public InternalPunishmentSubscription(
            int parent,
            @NonNull UUID linkId,
            @Nullable UUID operator,
            @NonNull WritableTemporalObject temporalObject,
            @Nullable Component message
    ) throws NullPointerException
    {
        this(String.valueOf(UUID.randomUUID()).split("-")[0], parent, linkId, operator, temporalObject, message);
    }

    public InternalPunishmentSubscription(
            @NonNull String id,
            int parent,
            @NonNull UUID linkId,
            @Nullable UUID operator,
            @NonNull WritableTemporalObject temporalObject,
            @Nullable Component message
    ) throws NullPointerException
    {
        super(id, parent);
        Preconditions.checkNotNull(id, "Id cannot be null.");
        Preconditions.checkNotNull(linkId, "LinkId cannot be null.");
        Preconditions.checkNotNull(temporalObject, "TimeSpan cannot be null.");

        if (id.length() > 8)
        {
            throw new IllegalArgumentException("Id cannot be longer than 4 characters!");
        }

        this.id = id;
        this.linkId = linkId;
        this.operator = operator;
        this.temporalObject = temporalObject;
        this.message = message;
    }

    @Override
    public @NonNull String id()
    {
        return id;
    }

    @Override
    public @NonNull UUID linkId()
    {
        return linkId;
    }

    @Override
    public @NonNull  Collection<PlayerClient> targets()
    {
        return KissenCore.getInstance().punishmentSubscriptionRepository().findTargets(linkId()).join();
    }

    @Override
    public @NonNull PlayerClient target()
    {
        Collection<PlayerClient> targets = targets();
        for (PlayerClient playerClient : targets())
        {
            if (Objects.equals(playerClient.id(), linkId()))
            {
                return playerClient;
            }
        }

        return targets.stream().findFirst().orElseThrow(() ->
        {
            String message = "A punishment without any targets has been found with the link id %s.";
            return new IllegalStateException(String.format(message, linkId()));
        });
    }

    @Override
    public @NonNull Actor operator()
    {
        if (Objects.isNull(operator))
        {
            return KissenCore.getInstance().console();
        }

        Optional<PlayerClient> actor = KissenCore.getInstance().playerRepository().find(operator).join();
        if (actor.isEmpty())
        {
            String message = "The player with the id %s was not found in the database but is bound to the punishment subscription %s.";
            throw new IllegalStateException(String.format(message, operator, id()));
        }

        return actor.get();
    }

    @Override
    public int signature()
    {
        return Objects.hash(linkId, temporalObject, message);
    }

    @Override
    public @NonNull Optional<Punishment> parent()
    {
        return KissenCore.getInstance().punishmentRepository().find(parentId()).join();
    }

    @Override
    public @NonNull Optional<Component> message()
    {
        return Optional.ofNullable(message);
    }

    @Override
    public void message(@Nullable Component component)
    {
        this.message = component;
    }

    @Override
    public void unsetMessage()
    {
        message(null);
    }

    @Override
    public @NonNull WritableTemporalObject temporal()
    {
        return temporalObject;
    }

    public @Nullable UUID rawOperator()
    {
        return operator;
    }
}
