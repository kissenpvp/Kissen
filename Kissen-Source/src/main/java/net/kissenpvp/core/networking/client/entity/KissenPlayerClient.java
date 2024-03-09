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

import net.kissenpvp.core.api.ban.Ban;
import net.kissenpvp.core.api.ban.BanImplementation;
import net.kissenpvp.core.api.ban.Punishment;
import net.kissenpvp.core.api.database.DataImplementation;
import net.kissenpvp.core.api.database.meta.BackendException;
import net.kissenpvp.core.api.database.queryapi.Column;
import net.kissenpvp.core.api.database.queryapi.FilterType;
import net.kissenpvp.core.api.database.queryapi.select.QuerySelect;
import net.kissenpvp.core.api.database.meta.list.MetaList;
import net.kissenpvp.core.api.event.EventCancelledException;
import net.kissenpvp.core.api.message.Theme;
import net.kissenpvp.core.api.message.localization.LocalizationImplementation;
import net.kissenpvp.core.api.networking.client.entitiy.PlayerClient;
import net.kissenpvp.core.api.networking.client.entitiy.ServerEntity;
import net.kissenpvp.core.api.permission.Permission;
import net.kissenpvp.core.api.time.AccurateDuration;
import net.kissenpvp.core.api.time.TemporalObject;
import net.kissenpvp.core.api.user.User;
import net.kissenpvp.core.api.user.UserImplementation;
import net.kissenpvp.core.api.user.rank.PlayerRank;
import net.kissenpvp.core.api.user.rank.Rank;
import net.kissenpvp.core.api.user.suffix.Suffix;
import net.kissenpvp.core.api.user.usersetttings.PlayerSetting;
import net.kissenpvp.core.api.user.usersetttings.BoundPlayerSetting;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.database.DataWriter;
import net.kissenpvp.core.message.PlayerTheme;
import net.kissenpvp.core.time.KissenAccurateDuration;
import net.kissenpvp.core.user.rank.KissenPlayerRank;
import net.kissenpvp.core.user.rank.PlayerRankNode;
import net.kissenpvp.core.user.suffix.KissenSuffix;
import net.kissenpvp.core.user.suffix.SuffixInChatSetting;
import net.kissenpvp.core.user.suffix.SuffixNode;
import net.kissenpvp.core.user.suffix.SuffixSetting;
import net.kissenpvp.core.user.usersettings.KissenBoundPlayerSetting;
import net.kissenpvp.core.time.TemporalMeasureNode;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class KissenPlayerClient<P extends Permission, R extends PlayerRank<?>, B extends Punishment<?>> implements PlayerClient<P, R, B> {

    @Override
    public @NotNull @Unmodifiable List<R> getRankHistory() {
        Comparator<R> sort = Comparator.comparing(PlayerRank::getStart);
        Function<MetaList<PlayerRankNode>, List<R>> listFunction = rankNodes -> rankNodes.stream().map(this::translateRank).sorted(sort).toList();
        Optional<MetaList<PlayerRankNode>> playerRankNodes = getUser().getList("rank_list", PlayerRankNode.class);
        return playerRankNodes.map(listFunction).orElse(Collections.emptyList());
    }

    @Override
    public @NotNull R getRank() {
        return getRankIndex().map(index -> getRankHistory().get(index)).orElse(fallbackRank());
    }

    @Override
    public @NotNull R grantRank(@NotNull Rank rank) {
        return grantRank(rank, null);
    }

    @Override
    public @NotNull R grantRank(@NotNull Rank rank, @Nullable AccurateDuration accurateDuration) {
        String id = KissenCore.getInstance().getImplementation(DataImplementation.class).generateID();
        PlayerRankNode playerRankNode = new PlayerRankNode(id, rank.getName(), new TemporalMeasureNode(accurateDuration));
        R playerRank = translateRank(playerRankNode, record -> {
        });
        return setRank(playerRank);
    }

    @Override
    public @NotNull @Unmodifiable Set<UUID> getAltAccounts() throws BackendException {

        //key = total_id AND value = getTotalID
        QuerySelect query = getUser().getMeta().select(Column.TOTAL_ID).where(Column.KEY, "total_id", FilterType.EXACT_MATCH).and(Column.VALUE, "\"" + getTotalID() + "\"", FilterType.EXACT_MATCH);

        Function<String[], UUID> toUUID = data -> UUID.fromString(data[0].substring(getUser().getSaveID().length()));
        Predicate<UUID> byUser = uuid -> !getUniqueId().equals(uuid);

        /*CompletableFuture<String[][]> alts = query.execute();
        return alts.thenApply(result -> Arrays.stream(result).map(toUUID).filter(byUser).collect(Collectors.toSet())).join();*/
        return Collections.emptySet(); //TODO
    }

    @Override
    public @NotNull UUID getTotalID() {
        return getUser().get("total_id", UUID.class).orElse(getUniqueId());
    }

    @Override
    public @NotNull B punish(@NotNull Ban ban, @NotNull ServerEntity banOperator) throws BackendException {
        return punish(ban, banOperator, null);
    }

    @Override
    public @NotNull B punish(@NotNull Ban ban, @NotNull ServerEntity banOperator, @Nullable Component reason) throws BackendException {
        return (B) KissenCore.getInstance().getImplementation(BanImplementation.class).punish(getTotalID(), ban, banOperator, reason);
    }

    @Override
    public @NotNull Optional<B> getPunishment(@NotNull String id) throws BackendException {
        return getPunishmentHistory().stream().filter(punishment -> punishment.getID().equals(id)).findFirst();
    }

    @Override
    public @NotNull @Unmodifiable List<B> getPunishmentHistory() throws BackendException {
        return KissenCore.getInstance().getImplementation(BanImplementation.class).getPunishmentSet(getTotalID()).stream().sorted((Comparator<B>) (punishment1, punishment2) -> punishment2.getStart().compareTo(punishment1.getStart())).toList();
    }

    @Override
    public @NotNull AccurateDuration getOnlineTime() {
        return getOnlineTime(getUser());
    }

    @Override
    public @NotNull Set<Suffix> getSuffixSet() {

        Function<SuffixNode, Suffix> translator = suffixNode -> new KissenSuffix(suffixNode, suffixDataWriter());
        List<SuffixNode> currentSuffixNodes = getUser().getListNotNull("suffix_list", SuffixNode.class);
        Set<Suffix> suffixes = currentSuffixNodes.stream().map(translator).collect(Collectors.toSet());

        // add rank suffix if present
        getRank().getSource().getSuffix().ifPresent(rankSuffix -> {
            KissenSuffix kissenSuffix = new KissenSuffix(new SuffixNode("rank", rankSuffix), null);
            suffixes.add(kissenSuffix);
        });

        return Set.copyOf(suffixes);
    }

    @Override
    public @NotNull Optional<Suffix> getSuffix(@NotNull String name) {
        return getSuffixSet().stream().filter(suffix -> suffix.getName().equals(name)).findFirst();
    }

    @Override
    public @NotNull Suffix grantSuffix(@NotNull String name, @NotNull Component content) {
        return grantSuffix(name, content, null);
    }

    @Override
    public @NotNull Suffix grantSuffix(@NotNull String name, @NotNull Component content, @Nullable AccurateDuration accurateDuration) throws EventCancelledException {
        SuffixNode suffixNode = new SuffixNode(name, content, new TemporalMeasureNode(accurateDuration));
        getUser().getListNotNull("suffix_list", SuffixNode.class).replaceOrInsert(suffixNode);
        return new KissenSuffix(suffixNode, suffixDataWriter());
    }

    @Override
    public boolean revokeSuffix(@NotNull String name) {
        return getSuffix(name).filter(TemporalObject::isValid).map(suffix -> {
            suffix.setEnd(Instant.now());
            return true;
        }).orElse(false);
    }

    @Override
    public @NotNull Optional<Suffix> getSelectedSuffix() {
        String setting = getUserSetting(SuffixSetting.class).getValue();
        if (setting.equals("none")) {
            return Optional.empty();
        }
        return getSuffix(setting).filter(TemporalObject::isValid);
    }

    @Override
    public @NotNull Locale getCurrentLocale() {
        LocalizationImplementation localizationImplementation = KissenCore.getInstance().getImplementation(LocalizationImplementation.class);
        Supplier<String> defaultLocale = () -> localizationImplementation.getDefaultLocale().toString();
        Supplier<String> autoLocale = () -> getUser().get("locale", String.class).orElseGet(defaultLocale);
        return localizationImplementation.getLocale(getUser().get("force_locale", String.class).orElseGet(autoLocale));
    }

    @Override
    public @NotNull Component styledRankName() {
        TextComponent.Builder builder = Component.text();
        Rank rank = getRank().getSource();

        TextColor rankTheme = getLastColor(rank.getPrefix().orElse(Component.empty())).orElse(rank.getChatColor());
        rank.getPrefix().ifPresent(prefix -> builder.append(prefix).appendSpace());
        builder.append(displayName().color(rankTheme));
        if (getUserSetting(SuffixInChatSetting.class).getValue()) {
            getSelectedSuffix().ifPresent(suffix -> builder.appendSpace().append(suffix.getContent().color(rankTheme)));
        }
        return builder.asComponent();
    }

    @Override
    public @NotNull <X> BoundPlayerSetting<X> getUserSetting(@NotNull Class<? extends PlayerSetting<X>> settingClass) {
        return getUserSetting(getUser(), settingClass);
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
    public @NotNull Theme getTheme() {
        return new PlayerTheme(this);
    }

    @Override
    public final boolean isClient() {
        return true;
    }

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

    protected @NotNull DataWriter<PlayerRankNode> rankDataWriter() {
        return this::setRankNode;
    }

    private @NotNull DataWriter<SuffixNode> suffixDataWriter() {
        return suffixDataWriter(getUser());
    }

    protected @NotNull DataWriter<SuffixNode> suffixDataWriter(@NotNull User user) {
        return (suffix) -> user.getListNotNull("suffix_list", SuffixNode.class).replaceOrInsert(suffix);
    }

    /**
     * Returns index from the {@link #getRankHistory()} of the rank currently active.
     * This is determined using the {@link PlayerRank#isValid()} method and the {@link PlayerRank#getSource()} is not null.
     *
     * @return the index of the active {@link PlayerRank}
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

    protected <X> BoundPlayerSetting<X> getUserSetting(@NotNull User user, @NotNull Class<? extends PlayerSetting<X>> settingClass) {
        try {
            Class<UserImplementation> clazz = UserImplementation.class;
            UserImplementation userImplementation = KissenCore.getInstance().getImplementation(clazz);
            Stream<PlayerSetting<?>> settingStream = userImplementation.getPlayerSettings().stream();

            Predicate<PlayerSetting<?>> predicate = currentSetting -> settingClass.isAssignableFrom(currentSetting.getClass());
            return (BoundPlayerSetting<X>) new KissenBoundPlayerSetting<>(settingStream.filter(predicate).findFirst().orElseThrow(ClassCastException::new), user);
        } catch (ClassCastException classCastException) {
            throw new IllegalArgumentException(String.format("Attempted to request content from setting %s which isn't registered. Please ensure the setting %s is correctly registered before trying to fetch its content.", settingClass.getSimpleName(), settingClass.getSimpleName()));
        }
    }

    public @NotNull AccurateDuration getOnlineTime(@NotNull User user) {

        KissenAccurateDuration defaultDuration = new KissenAccurateDuration(0);
        KissenAccurateDuration onlineTime = user.get("online_time", KissenAccurateDuration.class).orElse(defaultDuration);

        if (isConnected()) {
            //Duration onlineSince = Duration.between(Instant.now(), Instant.now()); //TODO
            //return new KissenAccurateDuration(onlineTime.milliseconds() + onlineSince.toMillis());
        }
        return onlineTime;
    }

    public @NotNull Optional<Instant> getLastPlayed(@NotNull User user) {
        if(isConnected())
        {
            return Optional.of(Instant.now());
        }

        return user.get("last_played", Instant.class);
    }

    private @NotNull Optional<TextColor> getLastColor(@NotNull Component component) {
        TextColor color = component.color();
        if (!component.children().isEmpty()) {
            color = component.children().get(component.children().size() - 1).color();
        }
        return Optional.ofNullable(color);
    }

    private @NotNull R translateRank(@NotNull PlayerRankNode playerRankNode) {
        return translateRank(playerRankNode, rankDataWriter());
    }

    protected abstract @NotNull R translateRank(@NotNull PlayerRankNode playerRankNode, @Nullable DataWriter<PlayerRankNode> dataWriter);

    protected abstract @NotNull R fallbackRank();
}
