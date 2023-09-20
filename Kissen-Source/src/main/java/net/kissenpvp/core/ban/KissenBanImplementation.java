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

import net.kissenpvp.core.api.ban.*;
import net.kissenpvp.core.api.database.DataImplementation;
import net.kissenpvp.core.api.database.meta.BackendException;
import net.kissenpvp.core.api.database.meta.Meta;
import net.kissenpvp.core.api.database.meta.ObjectMeta;
import net.kissenpvp.core.api.database.queryapi.Column;
import net.kissenpvp.core.api.database.queryapi.FilterType;
import net.kissenpvp.core.api.database.queryapi.QuerySelect;
import net.kissenpvp.core.api.networking.client.entitiy.PlayerClient;
import net.kissenpvp.core.api.util.Container;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.message.KissenComponentSerializer;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This class is an abstract implementation of the BanImplementation interface.
 * It provides common functionality for handling bans and punishments.
 * Subclasses are required to implement several abstract methods to provide specific implementation details.
 *
 * @param <B> the type of Ban object to be used
 * @param <P> the type of Punishment object to be used
 */
public abstract class KissenBanImplementation<B extends Ban, P extends Punishment<?>> implements BanImplementation<B, P> {

    private final Set<B> cachedBans;

    /**
     * Initializes a new instance of the KissenBanImplementation class.
     * It creates an empty HashSet to store the cached bans.
     */
    public KissenBanImplementation() {
        this.cachedBans = new HashSet<>();
    }

    @Override
    public boolean postStart() {
        try {
            cachedBans.addAll(obtainBanSet());
        } catch (BackendException backendException) {
            KissenCore.getInstance()
                    .getLogger()
                    .error("An error occurred when loading bans from the backend. The server will shutdown to prevent further damages to the data.");
            return false;
        }
        return BanImplementation.super.postStart();
    }

    @Override
    public @NotNull @Unmodifiable Set<B> getBanSet() {
        return Collections.unmodifiableSet(cachedBans);
    }

    @Override
    public @NotNull Optional<B> getBan(int id) {
        return cachedBans.stream().filter(ban -> {
            System.out.println(ban.getID() + " == " + id + " (" + (ban.getID() == id) + ")");
            return ban.getID() == id;
        }).findFirst();
    }

    @Override
    public @NotNull B createBan(int id, @NotNull Map<String, String> data) throws BackendException {
        B ban = buildBan(id, data);
        cachedBans.removeIf(currentBan -> currentBan.getID() == ban.getID());
        cachedBans.add(ban);
        return ban;
    }

    @Override
    public @NotNull B createBan(int id, @NotNull String name, @NotNull BanType banType) throws BackendException {
        return createBan(id, name, banType, null);
    }

    @Override
    public @NotNull B createBan(int id, @NotNull String name, @NotNull BanType banType, @Nullable Duration duration) throws BackendException {
        Map<String, String> data = new HashMap<>();
        data.put("name", name);
        data.put("ban_type", banType.name());
        if (duration != null) {
            data.put("duration", String.valueOf(duration.toMillis()));
        }
        return createBan(id, data);
    }

    @Override
    public @NotNull P punish(@NotNull UUID totalID, @NotNull B ban, @NotNull BanOperator banOperator) throws BackendException {
        return punish(totalID, ban, banOperator, null);
    }

    @Override
    public @NotNull P punish(@NotNull UUID totalID, @NotNull B ban, @NotNull BanOperator banOperator, @Nullable Component reason) throws BackendException {
        return punish(totalID, ban, banOperator, reason, getMeta());
    }

    @Override
    public @NotNull Optional<P> getCurrentBan(@NotNull UUID totalID) throws BackendException {
        return getCurrentBan(totalID, getMeta());
    }

    @Override
    public @NotNull Optional<P> getCurrentBan(@NotNull UUID totalID, @NotNull BanType banType) throws BackendException {
        return getCurrentBan(totalID, banType, getMeta());
    }

    @Override
    public @NotNull @Unmodifiable Set<P> getPlayerBanSet(@NotNull UUID totalID) throws BackendException {
        return getPlayerBanSet(totalID, getMeta());
    }

    @Override
    public @NotNull @Unmodifiable Set<P> getPlayerBanSet() throws BackendException {
        return getPlayerBanSet(getMeta());
    }

    /**
     * Filters the punishment based on the given meta data.
     *
     * @param meta The meta data used for filtering the punishment.
     * @return A QuerySelect object that represents the filtered punishment.
     */
    private @NotNull QuerySelect punishmentFilter(@NotNull Meta meta) {
        return meta.select(Column.KEY, Column.VALUE).appendFilter(Column.TOTAL_ID, "punishment", FilterType.EQUALS);
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
     * @throws BackendException if there is an error in the backend operation
     */
    protected @NotNull P punish(@NotNull UUID totalID, @NotNull B ban, @NotNull BanOperator banOperator, @Nullable Component reason, @NotNull Meta meta) throws BackendException {
        KissenPunishmentNode kissenPunishmentNode = constructPunishmentNode(ban, banOperator, Optional.ofNullable(reason)
                .map(component -> KissenComponentSerializer.getInstance().getJsonSerializer().serialize(component))
                .orElse(null));
        set(totalID, kissenPunishmentNode, meta);

        P punishment = translatePunishment(totalID, kissenPunishmentNode, meta);
        applyBan(punishment);

        return punishment;
    }

    /**
     * Retrieves the current ban for a given player identified by their totalID.
     *
     * @param totalID the UUID of the player
     * @param meta    the metadata used for retrieving the ban
     * @return an Optional containing the current ban if found, otherwise an empty Optional
     * @throws BackendException if there is an error retrieving the ban from the backend
     */
    protected @NotNull Optional<P> getCurrentBan(@NotNull UUID totalID, @NotNull Meta meta) throws BackendException {
        return getPlayerBanSet(totalID, meta).stream()
                .filter(Punishment::isValid)
                .min(Comparator.comparingLong(punishment -> punishment.getStart()));
    }

    /**
     * Retrieves the current ban of a player.
     *
     * @param totalID the UUID of the player
     * @param banType the type of ban to retrieve
     * @param meta    the metadata associated with the player
     * @return an Optional containing the current ban if found, otherwise an empty Optional
     * @throws BackendException if there is an error retrieving the ban from the backend
     */
    protected @NotNull Optional<P> getCurrentBan(@NotNull UUID totalID, @NotNull BanType banType, @NotNull Meta meta) throws BackendException {
        return getPlayerBanSet(totalID, meta).stream()
                .filter(Punishment::isValid)
                .filter(punishment -> punishment.getBanType().equals(banType))
                .min(Comparator.comparingLong(punishment -> punishment.getStart()));
    }

    /**
     * Retrieves the set of player bans for the given total ID and meta.
     *
     * @param totalID The total ID of the player.
     * @param meta    The meta instance for executing the filter and processing the data.
     * @return The set of player bans, wrapped in an {@link Optional}.
     * @throws BackendException If an error occurs while accessing the backend.
     */
    protected @NotNull @Unmodifiable Set<P> getPlayerBanSet(@NotNull UUID totalID, @NotNull Meta meta) throws BackendException {
        return meta.getRecordList("punishment", totalID.toString(), KissenPunishmentNode.class).stream().map(obj -> translatePunishment(totalID, obj, meta)).collect(Collectors.toSet());
    }

    /**
     * Returns a set of player bans based on the provided parameters.
     *
     * @param meta the meta information for executing the query
     * @return an unmodifiable set of player bans
     * @throws BackendException if there is an issue retrieving the player bans from the backend
     */
    protected @NotNull @Unmodifiable Set<P> getPlayerBanSet(@NotNull Meta meta) throws BackendException {
        //return Arrays.stream(meta.getRecordList("punishment")).map(dataProcessor()).collect(Collectors.toSet());
        return new HashSet<>(); //TODO
    }

    /**
     * Processes the given data to produce an instance of type P.
     *
     * @return a function that accepts a String array and returns an instance of type P.
     */
    @Contract(pure = true)
    private @NotNull Function<? super String[], ? extends P> dataProcessor() {
        return (Function<String[], P>) data -> {
            KissenPunishmentNode kissenPunishmentNode = KissenCore.getInstance()
                    .getImplementation(DataImplementation.class)
                    .fromJson(data[1], KissenPunishmentNode.class);
            return translatePunishment(UUID.fromString(data[0]), kissenPunishmentNode, getMeta());
        };
    }

    /**
     * Sets a KissenPunishmentNode in the metadata for a given totalID.
     * If a KissenPunishmentNode with the same ID already exists, it is replaced.
     *
     * @param totalID              the UUID representing the totalID for which the KissenPunishmentNode should be set
     * @param kissenPunishmentNode the KissenPunishmentNode to be set
     * @param meta                 the Meta data object
     * @throws BackendException if there is an error accessing the backend
     */
    protected void set(@NotNull UUID totalID, @NotNull KissenPunishmentNode kissenPunishmentNode, @NotNull Meta meta) throws BackendException {
        List<KissenPunishmentNode> punishmentRecordList = new ArrayList<>(meta.getRecordList("punishment", totalID.toString(), KissenPunishmentNode.class));
        punishmentRecordList.removeIf(node -> node.id().equals(kissenPunishmentNode.id()));
        punishmentRecordList.add(kissenPunishmentNode);
        meta.setRecordList("punishment", totalID.toString(), punishmentRecordList);
    }

    /**
     * Constructs a punishment node based on the given parameters.
     *
     * @param ban         the ban object associated with the punishment node
     * @param banOperator the operator who performed the ban
     * @param reason      the reason for the ban (can be null)
     * @return the constructed punishment node
     */
    protected @NotNull KissenPunishmentNode constructPunishmentNode(@NotNull B ban, @NotNull BanOperator banOperator, @Nullable String reason) {
        String id = KissenCore.getInstance().getImplementation(DataImplementation.class).generateID();
        BanOperatorNode banOperatorNode = new BanOperatorNode(banOperator instanceof PlayerClient<?, ?, ?> playerClient ? playerClient.getUniqueId() : null, banOperator.displayName());
        long timeStamp = System.currentTimeMillis();
        Long end = ban.getDuration().map(duration -> timeStamp + duration.toMillis()).orElse(null);
        return new KissenPunishmentNode(id, ban.getName(), banOperatorNode, ban.getBanType(), new Container<>(reason), new ArrayList<>(), timeStamp, new Container<>(ban.getDuration()
                .map(Duration::toMillis)
                .orElse(null)), new Container<>(end), end);
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
     * @throws BackendException if any issues occur while retrieving
     *                          the data from the database, such as connection issues,
     *                          query execution errors, timeout, or any other low-level
     *                          database related errors.
     */
    protected abstract @NotNull @Unmodifiable Set<B> obtainBanSet() throws BackendException;

    /**
     * Retrieves the meta information for the current object.
     *
     * @return The meta information as an instance of the Meta class.
     */
    protected abstract @NotNull @Unmodifiable ObjectMeta getMeta();

    /**
     * Constructs a ban object with the specified id and data.
     *
     * @param id   the id of the ban object
     * @param data the data to be associated with the ban object
     * @return the constructed ban object
     */
    protected abstract @NotNull B buildBan(int id, @NotNull Map<String, String> data) throws BackendException;

    /**
     * Translates a punishment from the given parameters.
     *
     * @param totalID              the ID of the punishment
     * @param kissenPunishmentNode the punishment node to be translated
     * @param meta                 additional metadata for the translation
     * @return the translated punishment
     */
    protected abstract @NotNull P translatePunishment(@NotNull UUID totalID, @NotNull KissenPunishmentNode kissenPunishmentNode, @NotNull Meta meta);
}
