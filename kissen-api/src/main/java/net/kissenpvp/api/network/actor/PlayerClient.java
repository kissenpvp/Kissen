package net.kissenpvp.api.network.actor;

import net.kissenpvp.api.database.PersistableEntity;
import net.kissenpvp.api.network.actor.rank.RankSubscription;
import net.kissenpvp.api.punishment.Punishment;
import net.kissenpvp.api.punishment.PunishmentSubscription;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * Represents a player client.
 * <p>
 * It is uniquely identified by its {@code id}
 * as defined by {@link PersistableEntity} and includes additional details.
 * <p>
 * This interface extends {@link Actor} to inherit common network actor properties
 * (e.g., username and locale) and {@link PersistableEntity} to support database persistence.
 *
 * @author Ivo Quiring
 */
public interface PlayerClient extends Actor, PersistableEntity<UUID>
{
    /**
     * Returns the stable identifier that associates this player client with an external identity
     * or a group of linked accounts. This value is guaranteed to be non-null.
     * <p>
     * It is used to correlate multiple accounts belonging to the same user, for example, to
     * apply cross-account punishments or manage account linking.
     *
     * @return a non-null {@link UUID} representing the link identifier for this player client.
     */
    @NonNull UUID linkId();

    @NonNull PunishmentSubscription punish(@NonNull Punishment punishment) throws NullPointerException;

    @NonNull PunishmentSubscription punish(@NonNull Punishment punishment, @Nullable Component message) throws NullPointerException;

    @NonNull List<RankSubscription> rankHistory();

    @NonNull RankSubscription rank();
}
