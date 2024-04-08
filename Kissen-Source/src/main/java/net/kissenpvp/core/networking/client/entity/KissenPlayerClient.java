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

package net.kissenpvp.core.networking.client.entity;

import net.kissenpvp.core.api.ban.AbstractBan;
import net.kissenpvp.core.api.ban.AbstractBanImplementation;
import net.kissenpvp.core.api.ban.AbstractPunishment;
import net.kissenpvp.core.api.database.DataImplementation;
import net.kissenpvp.core.api.database.DataWriter;
import net.kissenpvp.core.api.database.meta.BackendException;
import net.kissenpvp.core.api.database.meta.list.MetaList;
import net.kissenpvp.core.api.database.queryapi.Column;
import net.kissenpvp.core.api.database.queryapi.select.QuerySelect;
import net.kissenpvp.core.api.message.localization.LocalizationImplementation;
import net.kissenpvp.core.api.networking.client.entitiy.PlayerClient;
import net.kissenpvp.core.api.networking.client.entitiy.ServerEntity;
import net.kissenpvp.core.api.permission.AbstractPermission;
import net.kissenpvp.core.api.time.AccurateDuration;
import net.kissenpvp.core.api.time.TemporalData;
import net.kissenpvp.core.api.user.User;
import net.kissenpvp.core.api.user.UserImplementation;
import net.kissenpvp.core.api.user.playersettting.AbstractPlayerSetting;
import net.kissenpvp.core.api.user.playersettting.AbstractBoundPlayerSetting;
import net.kissenpvp.core.api.user.playersettting.RegisteredPlayerSetting;
import net.kissenpvp.core.api.user.rank.AbstractPlayerRank;
import net.kissenpvp.core.api.user.rank.AbstractRank;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.user.playersetting.KissenBoundPlayerSetting;
import net.kissenpvp.core.user.rank.KissenPlayerRank;
import net.kissenpvp.core.user.rank.PlayerRankNode;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class KissenPlayerClient<P extends AbstractPermission, R extends AbstractPlayerRank<?>, B extends AbstractPunishment<?>> implements PlayerClient<P, R, B> {

    /**
     * Retrieves a player setting of the specified type from the user system.
     *
     * <p>The {@code getPlayerSetting} method obtains a player setting of the specified type from the user system
     * by utilizing the {@link UserImplementation} and searching through the available player settings. If the
     * specified setting type is not found, an exception is thrown.</p>
     *
     * @param settingClass the {@link Class} object representing the type of the desired player setting
     * @param <X>          the generic type parameter for the player setting
     * @return a {@link AbstractPlayerSetting} instance of the specified type
     * @throws NoSuchElementException if no player setting of the specified type is found
     * @throws NullPointerException   if the settingClass parameter is {@code null}
     * @see UserImplementation
     * @see AbstractPlayerSetting
     */
    private static <X, S extends PlayerClient<?, ?, ?>> @NotNull RegisteredPlayerSetting<X, S> getPlayerSetting(@NotNull Class<? extends AbstractPlayerSetting<X, S>> settingClass) {
        UserImplementation userSystem = KissenCore.getInstance().getImplementation(UserImplementation.class);
        return (RegisteredPlayerSetting<X, S>) userSystem.getPlayerSetting(settingClass);
    }

    @Override
    public @NotNull R getRank() {
        return getRankIndex().map(index -> getRankHistory().get(index)).orElse(fallbackRank());
    }

    @Override
    public @NotNull R grantRank(@NotNull AbstractRank rank) {
        return grantRank(rank, null);
    }

    @Override
    public @NotNull @Unmodifiable List<R> getRankHistory() {
        MetaList<PlayerRankNode> ranks = getUser().getListNotNull("rank_list", PlayerRankNode.class);
        return ranks.stream().map(this::translateRank).sorted(Comparator.comparing(AbstractPlayerRank::getStart)).toList();
    }

    @Override
    public @NotNull @Unmodifiable Set<UUID> getAltAccounts() throws BackendException {
        QuerySelect query = getUser().getMeta().select(Column.TOTAL_ID, Column.VALUE).where(Column.KEY, "total_id");
        return Arrays.stream(query.execute().join()).filter(current -> current[1].equals(getTotalID())).map(current -> {
            String raw = current[0].toString();
            return UUID.fromString(raw);
        }).collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public @NotNull UUID getTotalID() {
        return getUser().get("total_id", UUID.class).orElse(getUniqueId());
    }

    @Override
    public @NotNull B punish(@NotNull AbstractBan ban, @NotNull ServerEntity banOperator) {
        return punish(ban, banOperator, null);
    }

    @Override
    public @NotNull B punish(@NotNull AbstractBan ban, @NotNull ServerEntity banOperator, @Nullable Component reason) throws BackendException {
        return (B) KissenCore.getInstance().getImplementation(AbstractBanImplementation.class).punish(getTotalID(), ban, banOperator, reason);
    }

    @Override
    public @NotNull Optional<B> getPunishment(@NotNull String id) throws BackendException {
        return getPunishmentHistory().stream().filter(punishment -> punishment.getID().equals(id)).findFirst();
    }

    @Override
    public @NotNull @Unmodifiable List<B> getPunishmentHistory() throws BackendException {
        return KissenCore.getInstance().getImplementation(AbstractBanImplementation.class).getPunishmentSet(getTotalID()).stream().sorted((Comparator<B>) (punishment1, punishment2) -> punishment2.getStart().compareTo(punishment1.getStart())).toList();
    }

    @Override
    public @NotNull AccurateDuration getOnlineTime() {
        return getOnlineTime(getUser());
    }

    @Override
    public @NotNull Locale getCurrentLocale() {
        LocalizationImplementation localizationImplementation = KissenCore.getInstance().getImplementation(LocalizationImplementation.class);
        Supplier<String> defaultLocale = () -> localizationImplementation.getDefaultLocale().toString();
        Supplier<String> autoLocale = () -> getUser().get("locale", String.class).orElseGet(defaultLocale);
        return localizationImplementation.getLocale(getUser().get("force_locale", String.class).orElseGet(autoLocale));
    }

    @Override
    public @NotNull Component displayName() {
        return Component.text(getName());
    }

    @Override
    public @NotNull String getName() {
        return getUser().getNotNull("name", String.class);
    }

    @Override
    public final boolean isClient() {
        return true;
    }

    /**
     * Sets the rank for the associated entity and translates the rank using the provided data writer.
     *
     * <p>The {@code setRank(@NotNull R playerRank)} method sets the specified player rank for the associated entity.
     * It translates the rank using the associated data writer for {@link PlayerRankNode} instances.</p>
     *
     * @param playerRank the {@link R} (generic) representing the player rank to be set
     * @return the translated player rank
     * @throws NullPointerException if the specified player rank is `null`
     * @see #setRankNode(PlayerRankNode)
     * @see #rankDataWriter()
     * @see #translateRank(PlayerRankNode, DataWriter)
     */
    public @NotNull R setRank(@NotNull R playerRank) {
        PlayerRankNode rankNode = ((KissenPlayerRank<?>) playerRank).getPlayerRankNode();
        return translateRank(setRankNode(rankNode), rankDataWriter());
    }

    /**
     * Sets the specified PlayerRankNode in the user's rank list and persists the change in the database.
     * If a {@link PlayerRankNode} with the same {@link PlayerRankNode#id()} already exists, it will be replaced;
     * otherwise, the new node will be inserted into the list.
     * <p>
     * This method ensures the rank list is initialized before performing the operation,
     * and it saves the updated rank list in the database.
     *
     * @param rankNode The PlayerRankNode to be set in the rank list. Must not be null.
     * @return The PlayerRankNode that has been set in the rank list.
     * @throws IllegalArgumentException if the provided rankNode is null.
     * @throws IllegalStateException    if the user is null, the rank list is not initialized, or database interaction fails.
     * @see PlayerRankNode
     */
    private @NotNull PlayerRankNode setRankNode(@NotNull PlayerRankNode rankNode) {
        getUser().getListNotNull("rank_list", PlayerRankNode.class).replaceOrInsert(rankNode);
        return rankNode;
    }

    /**
     * Retrieves the data writer for {@link PlayerRankNode}.
     *
     * <p>The {@code rankDataWriter()} method returns a {@link DataWriter} for {@link PlayerRankNode} instances.
     * It is used to set the rank node for the associated entity.</p>
     *
     * @return the data writer for {@link PlayerRankNode}
     * @see #setRankNode(PlayerRankNode)
     */
    protected @NotNull DataWriter<PlayerRankNode> rankDataWriter() {
        return this::setRankNode;
    }

    /**
     * Retrieves the index of the first valid rank in the rank history list.
     *
     * <p>The {@code getRankIndex} method is responsible for determining the index of the most recent valid rank
     * within the rank history list. It iterates through the list in reverse order, starting from the latest rank,
     * and identifies the index of the first valid rank encountered. If no valid ranks are found, an empty optional is returned.</p>
     *
     * @return an {@link Optional} containing the index of the most recent valid rank, or an empty optional if no valid ranks are found
     * @see #getRankHistory()
     */
    protected Optional<Integer> getRankIndex() {
        Integer index = null;
        List<R> rankList = getRankHistory();

        if (rankList.isEmpty()) {
            return Optional.empty();
        }
        for (int i = rankList.size() - 1; i > -1; i--) {
            if (rankList.get(i).isValid()) {
                index = i;
                break;
            }
        }
        return Optional.ofNullable(index);
    }

    @Override
    public @NotNull R grantRank(@NotNull AbstractRank rank, @Nullable AccurateDuration accurateDuration) {
        String id = KissenCore.getInstance().getImplementation(DataImplementation.class).generateID();
        PlayerRankNode playerRankNode = new PlayerRankNode(id, rank.getName(), new TemporalData(accurateDuration));
        return setRank(translateRank(playerRankNode, record -> {
        }));
    }

    /**
     * Retrieves the accurate online time for the specified user.
     *
     * <p>The {@code getOnlineTime} method obtains the online time for the given user. It retrieves the stored online time
     * from the user's data, or a default duration if no online time is found. If the user is currently connected, additional
     * online time since the last update may be calculated (TODO section).</p>
     *
     * @param user  the {@link User} for whom the online time is retrieved
     * @return      an {@link AccurateDuration} instance representing the user's online time
     * @throws NullPointerException if the user parameter is {@code null}
     * @see AccurateDuration
     */
    protected @NotNull AccurateDuration getOnlineTime(@NotNull User user) {

        AccurateDuration defaultDuration = new AccurateDuration(0);
        AccurateDuration onlineTime = user.get("online_time", AccurateDuration.class).orElse(defaultDuration);

        if (isConnected()) {
            //Duration onlineSince = Duration.between(Instant.now(), Instant.now()); //TODO
            //return new KissenAccurateDuration(onlineTime.milliseconds() + onlineSince.toMillis());
        }
        return onlineTime;
    }

    /**
     * Retrieves the last text color used in the specified text component.
     *
     * <p>The {@code getLastColor} method examines the provided text component and retrieves the last text color used.
     * If the component has children, it checks the last child's color. If no color is found, an empty optional is returned.</p>
     *
     * @param component  the {@link Component} from which to retrieve the last text color
     * @return           an {@link Optional} containing the last {@link TextColor}, or an empty optional if no color is found
     * @throws NullPointerException if the component parameter is {@code null}
     * @see TextColor
     */
    private @NotNull Optional<TextColor> getLastColor(@NotNull Component component) {
        TextColor color = component.color();
        if (!component.children().isEmpty()) {
            color = component.children().get(component.children().size() - 1).color();
        }
        return Optional.ofNullable(color);
    }

    /**
     * Translates the provided {@link PlayerRankNode} into a result of type {@code R} using the default {@link DataWriter}.
     *
     * <p>The {@code translateRank} method is responsible for translating the given {@link PlayerRankNode} into a result of type {@code R}
     * using the default {@link DataWriter}. This method acts as a convenient wrapper for the more customizable {@link #translateRank(PlayerRankNode, DataWriter)}
     * method, allowing for translation with the default writer.</p>
     *
     * @param playerRankNode  the {@link PlayerRankNode} to be translated
     * @return                a result of type {@code R} representing the translation
     * @throws NullPointerException if the playerRankNode parameter is {@code null}
     * @see #translateRank(PlayerRankNode, DataWriter)
     */
    private @NotNull R translateRank(@NotNull PlayerRankNode playerRankNode) {
        return translateRank(playerRankNode, rankDataWriter());
    }

    /**
     * Translates the provided {@link PlayerRankNode} into a result of type {@code R} using a custom {@link DataWriter}.
     *
     * <p>The {@code translateRank} method is an abstract method that should be implemented by subclasses to provide
     * a specific translation logic for the given {@link PlayerRankNode}. It allows for customization by accepting
     * a {@link DataWriter<PlayerRankNode>} parameter, which can be used to write data related to the translation.</p>
     *
     * @param playerRankNode  the {@link PlayerRankNode} to be translated
     * @param dataWriter      a custom {@link DataWriter<PlayerRankNode>} for writing data related to the translation, or {@code null} if not needed
     * @return                a result of type {@code R} representing the translation
     * @throws NullPointerException if the playerRankNode parameter is {@code null}
     */
    protected abstract @NotNull R translateRank(@NotNull PlayerRankNode playerRankNode, @Nullable DataWriter<PlayerRankNode> dataWriter);

    /**
     * Provides a fallback result of type {@code R} when translation fails or is not applicable.
     *
     * <p>The {@code fallbackRank} method is an abstract method that should be implemented by subclasses to provide
     * a fallback result of type {@code R} when translation cannot be performed or is not applicable for the given context.</p>
     *
     * @return a fallback result of type {@code R}
     */
    protected abstract @NotNull R fallbackRank();
}
