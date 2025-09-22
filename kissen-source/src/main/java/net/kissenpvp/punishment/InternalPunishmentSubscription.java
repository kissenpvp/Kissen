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
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class InternalPunishmentSubscription extends InternalSubscriptionEntity<String, Integer, Punishment> implements PunishmentSubscription
{
    private final String id;
    private final UUID linkId;
    private final UUID operator;
    private final @NotNull WritableTemporalObject temporalObject;
    private @Nullable Component message;

    public InternalPunishmentSubscription(
            int parent,
            @NotNull UUID linkId,
            @Nullable UUID operator,
            @NotNull WritableTemporalObject temporalObject,
            @Nullable Component message
    ) throws NullPointerException
    {
        this(String.valueOf(UUID.randomUUID()).split("-")[0], parent, linkId, operator, temporalObject, message);
    }

    public InternalPunishmentSubscription(
            @NotNull String id,
            int parent,
            @NotNull UUID linkId,
            @Nullable UUID operator,
            @NotNull WritableTemporalObject temporalObject,
            @Nullable Component message
    ) throws NullPointerException
    {
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
        this.operator = operator;
        this.temporalObject = temporalObject;
        this.message = message;
    }

    @Override
    public @NotNull String id()
    {
        return id;
    }

    @Override
    public @NotNull UUID linkId()
    {
        return linkId;
    }

    @Override
    public @NotNull @UnmodifiableView Collection<PlayerClient> targets()
    {
        return KissenCore.getInstance().punishmentSubscriptionRepository().findTargets(linkId()).join();
    }

    @Override
    public @NotNull PlayerClient target()
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
    public @NotNull Actor operator()
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
    public @NotNull Optional<Punishment> parent()
    {
        return KissenCore.getInstance().punishmentRepository().find(parentId()).join();
    }

    @Override
    public @NotNull Optional<Component> message()
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
    public @NotNull WritableTemporalObject temporal()
    {
        return temporalObject;
    }

    public @Nullable UUID rawOperator()
    {
        return operator;
    }
}
