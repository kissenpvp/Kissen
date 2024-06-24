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

import lombok.extern.slf4j.Slf4j;
import net.kissenpvp.core.api.ban.AbstractBanTemplate;
import net.kissenpvp.core.api.ban.AbstractBanImplementation;
import net.kissenpvp.core.api.ban.AbstractPunishment;
import net.kissenpvp.core.api.ban.BanType;
import net.kissenpvp.core.api.database.StorageImplementation;
import net.kissenpvp.core.api.database.connection.DatabaseImplementation;
import net.kissenpvp.core.api.database.meta.Meta;
import net.kissenpvp.core.api.database.meta.list.MetaList;
import net.kissenpvp.core.api.database.queryapi.Column;
import net.kissenpvp.core.api.database.queryapi.select.QuerySelect;
import net.kissenpvp.core.api.database.savable.Savable;
import net.kissenpvp.core.api.database.savable.SavableMap;
import net.kissenpvp.core.api.networking.client.entitiy.ServerEntity;
import net.kissenpvp.core.api.time.AccurateDuration;
import net.kissenpvp.core.api.time.TemporalObject;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.database.KissenTable;
import net.kissenpvp.core.message.localization.KissenLocalizationImplementation;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.text.MessageFormat;
import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class is an abstract implementation of the BanImplementation interface.
 * It provides common functionality for handling bans and punishments.
 * Subclasses are required to implement several abstract methods to provide specific implementation details.
 *
 * @param <B> the type of Ban object to be used
 * @param <P> the type of Punishment object to be used
 */
@Slf4j(topic = "Kissen")
public abstract class KissenBanImplementation<B extends AbstractBanTemplate, P extends AbstractPunishment<?>> implements AbstractBanImplementation<B, P> {

    private static final String STORAGE_KEY = "ban_storage";
    private static final String TOTAL_ID_KEY = "ban_%s";
    private final Set<B> cachedBans;
    private KissenTable banTable;

    /**
     * Initializes a new instance of the KissenBanImplementation class.
     * It creates an empty HashSet to store the cached bans.
     */
    public KissenBanImplementation() {
        this.cachedBans = new HashSet<>();
    }

    @Override
    public boolean preStart() {
        DatabaseImplementation database = KissenCore.getInstance().getImplementation(DatabaseImplementation.class);
        banTable = (KissenTable) database.getPrimaryConnection().createTable("kissen_ban_table");
        return AbstractBanImplementation.super.preStart();
    }

    @Override
    public boolean start() {
        KissenLocalizationImplementation kissenLocalizationImplementation = KissenCore.getInstance().getImplementation(KissenLocalizationImplementation.class);
        kissenLocalizationImplementation.register("server.ban.player.not.allowed", new MessageFormat("You are not allowed to punish the player {0}."));

        kissenLocalizationImplementation.register("server.ban.player.banned", new MessageFormat("Player {0} has been banned from this network."));
        kissenLocalizationImplementation.register("server.ban.player.banned.cause", new MessageFormat("Player {0} has been banned from this network with the cause: \"{1}\"."));

        kissenLocalizationImplementation.register("server.ban.player.muted", new MessageFormat("Player {0} has been muted on this network."));
        kissenLocalizationImplementation.register("server.ban.player.muted.cause", new MessageFormat("Player {0} has been muted on this network with the cause: \"{1}\"."));

        kissenLocalizationImplementation.register("server.ban.player.kicked", new MessageFormat("Player {0} has been kicked on this network."));
        kissenLocalizationImplementation.register("server.ban.player.kicked.cause", new MessageFormat("Player {0} has been kicked from this network with the cause: \"{1}\"."));

        return AbstractBanImplementation.super.start();
    }

    @Override
    public boolean postStart() {
        cachedBans.addAll(fetchBanSet());
        return AbstractBanImplementation.super.postStart();
    }

    @Override
    public @NotNull @Unmodifiable Set<B> getBanTemplates() {
        return Collections.unmodifiableSet(cachedBans);
    }

    @Override
    public @NotNull Optional<B> getBanTemplate(int id) {
        return getBanTemplates().stream().filter(ban -> ban.getID()==id).findFirst();
    }

    @Override
    public @NotNull B createBanTemplate(int id, @NotNull String name, @NotNull BanType banType) {
        return createBanTemplate(id, name, banType, null);
    }

    @Override
    public @NotNull B createBanTemplate(int id, @NotNull String name, @NotNull BanType banType, @Nullable AccurateDuration accurateDuration) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("ban_type", banType);
        if (accurateDuration!=null) {
            data.put("duration", accurateDuration);
        }

        return setupBan(id, data);
    }

    @Override
    public @NotNull P punish(@NotNull UUID totalID, @NotNull B ban, @NotNull ServerEntity banOperator) {
        return punish(totalID, ban, banOperator, true);
    }

    @Override
    public @NotNull P punish(@NotNull UUID totalID, @NotNull B ban, @NotNull ServerEntity banOperator, @Nullable Component reason) {
        return punish(totalID, ban, banOperator, true, reason);
    }

    @Override
    public @NotNull P punish(@NotNull UUID totalID, @NotNull B ban, @NotNull ServerEntity banOperator, boolean apply) {
        return punish(totalID, ban, banOperator, apply, null);
    }

    @Override
    public @NotNull P punish(@NotNull UUID totalID, @NotNull B ban, @NotNull ServerEntity banOperator, boolean apply, @Nullable Component reason) {
        return punish(totalID, ban, banOperator, apply, reason, getMeta());
    }

    @Override
    public @NotNull Optional<P> getLatestPunishment(@NotNull UUID totalID) {
        return getLatestPunishment(totalID, getMeta());
    }

    @Override
    public @NotNull Optional<P> getLatestPunishment(@NotNull UUID totalID, @NotNull BanType banType) {
        return getLatestPunishment(totalID, banType, getMeta());
    }

    @Override
    public @NotNull @Unmodifiable Set<P> getPunishments(@NotNull UUID totalID) {
        return getPunishments(totalID, getMeta());
    }

    @Override
    public @NotNull @Unmodifiable Set<P> getPunishments() {
        return getPunishments(getMeta());
    }

    /**
     * Applies a ban to a specified user with the given parameters.
     *
     * @param totalID     the UUID of the user to be banned
     * @param ban         the ban object specifying the ban details
     * @param banOperator the ban operator determining the ban type
     * @param reason      the reason for the ban (optional)
     * @param meta        the metadata associated with the ban
     * @return the punishment object representing the applied ban
     * @ if there is an error in the backend operation
     */
    protected @NotNull P punish(@NotNull UUID totalID, @NotNull B ban, @NotNull ServerEntity banOperator, boolean apply, @Nullable Component reason, @NotNull Meta meta) {
        KissenPunishmentNode kissenPunishmentNode = constructPunishmentNode(ban, banOperator, reason);
        set(totalID, kissenPunishmentNode, meta);

        P punishment = translatePunishment(totalID, kissenPunishmentNode, meta);
        if (apply) {
            applyBan(punishment);
        }

        return punishment;
    }

    /**
     * Retrieves the current ban for a given player identified by their totalID.
     *
     * @param totalID the UUID of the player
     * @param meta    the metadata used for retrieving the ban
     * @return an Optional containing the current ban if found, otherwise an empty Optional
     * @ if there is an error retrieving the ban from the backend
     */
    protected @NotNull Optional<P> getLatestPunishment(@NotNull UUID totalID, @NotNull Meta meta) {
        return getPunishments(totalID, meta).stream().filter(TemporalObject::isValid).min(Comparator.comparing(TemporalObject::getStart));
    }

    /**
     * Retrieves the current ban of a player.
     *
     * @param totalID the UUID of the player
     * @param banType the type of ban to retrieve
     * @param meta    the metadata associated with the player
     * @return an Optional containing the current ban if found, otherwise an empty Optional
     * @ if there is an error retrieving the ban from the backend
     */
    protected @NotNull Optional<P> getLatestPunishment(@NotNull UUID totalID, @NotNull BanType banType, @NotNull Meta meta) {
        return getPunishments(totalID, meta).stream().filter(TemporalObject::isValid).filter(punishment -> punishment.getBanType().equals(banType)).min(Comparator.comparing(TemporalObject::getStart));
    }

    /**
     * Retrieves the set of player bans for the given total ID and meta.
     *
     * @param totalID The total ID of the player.
     * @param meta    The meta instance for executing the filter and processing the data.
     * @return The set of player bans, wrapped in an {@link Optional}.
     * @ If an error occurs while accessing the backend.
     */
    protected @NotNull @Unmodifiable Set<P> getPunishments(@NotNull UUID totalID, @NotNull Meta meta) {
        StorageImplementation storage = KissenCore.getInstance().getImplementation(StorageImplementation.class);
        Map<String, Object> cache = storage.getStorage(STORAGE_KEY, Duration.ofHours(1));
        return (Set<P>) cache.computeIfAbsent(String.format(TOTAL_ID_KEY, totalID), (k) -> loadPunishments(totalID, meta));
    }

    /**
     * Caches and retrieves a set of {@code P} objects associated with the specified {@link UUID} and {@link Meta}.
     *
     * <p>The {@code cacheId} method utilizes the provided {@link Meta} to fetch a collection of punishment data
     * associated with the given {@link UUID}. It then transforms the data into a set of {@code P} objects and returns
     * an unmodifiable set of these objects.</p>
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * {@code
     * // Example: Caching and retrieving punishment data for a specific UUID and Meta
     * UUID playerId = // specify UUID
     * Meta meta = // specify Meta
     * Set<P> punishmentSet = cacheId(playerId, meta);
     * }
     * </pre>
     *
     * @param totalID the {@link UUID} associated with the punishment data
     * @param meta    the {@link Meta} containing information about the data retrieval
     * @return an unmodifiable set of {@code P} objects associated with the specified {@link UUID} and {@link Meta}
     * @throws NullPointerException if the specified {@link UUID} or {@link Meta} is `null`
     */
    private @NotNull @Unmodifiable Set<P> loadPunishments(@NotNull UUID totalID, @NotNull Meta meta) {
        return meta.getCollection("punishment", totalID.toString(), KissenPunishmentNode.class).thenApply(data -> {
            Stream<KissenPunishmentNode> nodeStream = data.stream();
            return nodeStream.map(node -> translatePunishment(totalID, node, meta)).collect(Collectors.toUnmodifiableSet());
        }).join();
    }

    /**
     * Returns a set of player bans based on the provided parameters.
     *
     * @param meta the meta information for executing the query
     * @return an unmodifiable set of player bans
     * @ if there is an issue retrieving the player bans from the backend
     */
    protected @NotNull @Unmodifiable Set<P> getPunishments(@NotNull Meta meta) {
        QuerySelect querySelect = meta.select(Column.KEY, Column.VALUE).whereExact(Column.TOTAL_ID, "punishment");
        Object[][] objects = querySelect.execute().exceptionally((t) -> new Object[0][]).join();

        return Arrays.stream(objects).flatMap(data -> {
            UUID uuid = UUID.fromString(data[0].toString());
            KissenPunishmentNode[] punishmentNodes = (KissenPunishmentNode[]) data[1];
            return Arrays.stream(punishmentNodes).map(node -> translatePunishment(uuid, node, meta));
        }).collect(Collectors.toUnmodifiableSet());
    }


    /**
     * Sets a KissenPunishmentNode in the metadata for a given totalID.
     * If a KissenPunishmentNode with the same ID already exists, it is replaced.
     *
     * @param totalID        the UUID representing the totalID for which the KissenPunishmentNode should be set
     * @param punishmentNode the KissenPunishmentNode to be set
     * @param meta           the Metadata object
     * @ if there is an error accessing the backend
     */
    protected void set(@NotNull UUID totalID, @NotNull KissenPunishmentNode punishmentNode, @NotNull Meta meta) {
        Function<MetaList<KissenPunishmentNode>, Boolean> insert = list -> list.replaceOrInsert(punishmentNode);
        if (meta.getCollection("punishment", totalID.toString(), KissenPunishmentNode.class).thenApply(insert).join()) {
            invalidateCache(totalID);
        }
    }

    /**
     * Invalidates the cache for the specified {@link UUID} by removing the corresponding entry from the storage.
     *
     * <p>The {@code invalidateCache} method is responsible for removing the cache entry associated with the provided {@link UUID}
     * from the storage. This is useful when the cached data becomes outdated or needs to be refreshed.</p>
     *
     * @param totalID the {@link UUID} for which the cache needs to be invalidated
     * @throws NullPointerException if the specified {@link UUID} is `null`
     */
    private void invalidateCache(@NotNull UUID totalID) {
        StorageImplementation storage = KissenCore.getInstance().getImplementation(StorageImplementation.class);
        storage.getStorage(STORAGE_KEY).remove(String.format(TOTAL_ID_KEY, totalID));
    }

    /**
     * This function is responsible for removing an item with a specific identification number from the list of cached bans.
     * The function operates on the data structure `cachedBans`, which is a list of `B` objects, where `B` is a class or interface type.
     *
     * @param ban An instance of class (or interface) type `B` that is marked with `@NotNull`, denoting that this function expects a non-null object.
     *            The parameter `ban` here carries the ID of the ban which should be removed from `cachedBans`.
     *            <p>
     *            The method calls the `removeIf` method of the `cachedBans` list. `removeIf` is a default method in Java List interface, which removes all
     *            the elements of this list that satisfy the given predicate. The predicate is defined as a lambda function where the identification number
     *            of the current ban (`current.getID()`) is checked to see if it matches the identification number of the `ban` parameter (`ban.getID()`).
     *            <p>
     *            If the predicate returns true (meaning the IDs match), the current item will be removed from the list.
     * @return A boolean value. This function will return `true` if at least one item was removed from `cachedBans` (meaning that at least one
     * `B` object had the same ID as `ban`). If no items were removed, meaning that no object `B` in `cachedBans` had the same ID as `ban`,
     * the function will return `false`.
     * <p>
     * Note that the function does not provide explicit handling for null values in `cachedBans` beyond the `removeIf` method's own exception
     * handling.
     * <p>
     * This function does not modify the `ban` parameter or call any of its methods besides `getID()`, and as far as information flow/possible
     * side effects are concerned, it treats `ban` as an essentially immutable object.
     */
    public boolean remove(@NotNull B ban) {
        return cachedBans.removeIf(current -> current.getID()==ban.getID());
    }

    /**
     * Constructs a punishment node based on the given parameters.
     *
     * @param ban         the ban object associated with the punishment node
     * @param banOperator the operator who performed the ban
     * @param reason      the reason for the ban (can be null)
     * @return the constructed punishment node
     */
    protected @NotNull KissenPunishmentNode constructPunishmentNode(@NotNull B ban, @NotNull ServerEntity banOperator, @Nullable Component reason) {
        return new KissenPunishmentNode(ban, banOperator, reason);
    }

    public abstract void applyBan(@NotNull P ban);

    /**
     * Fetches and returns the ban set from the database, which is of type Set<B>.
     * <p>
     * This method provides an abstract mechanism for retrieving the stored
     * ban data directly from the database, rather than local memory cache. This data
     * is a set of 'Ban' objects of type 'B'.
     * <p>
     * It's essential that this method must be implemented by every subclass
     * that extends this abstract class. The implementation of this method
     * in subclasses might vary based on the specific techniques or strategies
     * used for database access and data retrieval.
     * <p>
     * Please note that the returned Set<B> is unmodifiable to ensure
     * the integrity and safety of the data. Any attempts to modify
     * the returned Set will result in an UnsupportedOperationException.
     *
     * @return The ban set fetched from the database, represented as an unmodifiable
     * Set of 'Ban' objects of type 'B'.
     * @ if any issues occur while retrieving
     * the data from the database, such as connection issues,
     * query execution errors, timeout, or any other low-level
     * database related errors.
     */
    protected @NotNull @Unmodifiable Set<B> fetchBanSet() {
        return translate(getMeta().getData((Savable<?>) createObject()).join());
    }

    protected Set<B> translate(@NotNull Map<String, SavableMap> data) {
        return data.values().stream().map((entrySet) -> {
            int id = entrySet.getNotNull("id", Integer.class);
            return setupBan(id, entrySet);
        }).collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Retrieves the meta information for the current object.
     *
     * @return The meta information as an instance of the Meta class.
     */
    protected @NotNull KissenTable getTable() {
        return banTable;
    }

    /**
     * Translates a punishment from the given parameters.
     *
     * @param totalID              the ID of the punishment
     * @param kissenPunishmentNode the punishment node to be translated
     * @param meta                 additional metadata for the translation
     * @return the translated punishment
     */
    protected abstract @NotNull P translatePunishment(@NotNull UUID totalID, @NotNull KissenPunishmentNode kissenPunishmentNode, @NotNull Meta meta);

    protected @NotNull Meta getMeta() {
        return getTable().setupMeta(null);
    }

    protected @NotNull B setupBan(int id, @NotNull Map<String, Object> data) {
        B ban = createObject();
        ((Savable<Integer>) ban).setup(id, data);
        if(!cachedBans.add(ban))
        {
            log.debug("Replace ban {} with new ban having the same id.", ban.getID());
            cachedBans.remove(ban);
            cachedBans.add(ban);
        }
        return ban;
    }

    protected abstract @NotNull B createObject();
}
