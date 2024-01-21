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
import net.kissenpvp.core.api.database.savable.list.SavableList;
import net.kissenpvp.core.api.database.savable.list.SavableRecordList;
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
import net.kissenpvp.core.api.user.usersetttings.UserSetting;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.database.DataWriter;
import net.kissenpvp.core.message.PlayerTheme;
import net.kissenpvp.core.user.rank.KissenPlayerRank;
import net.kissenpvp.core.user.rank.KissenPlayerRankNode;
import net.kissenpvp.core.user.suffix.KissenSuffix;
import net.kissenpvp.core.user.suffix.SuffixInChatSetting;
import net.kissenpvp.core.user.suffix.SuffixNode;
import net.kissenpvp.core.user.suffix.SuffixSetting;
import net.kissenpvp.core.user.usersettings.KissenUserBoundSettings;
import net.kissenpvp.core.time.TemporalMeasureNode;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class KissenPlayerClient<P extends Permission, R extends PlayerRank<?>, B extends Punishment<?>> implements PlayerClient<P, R, B> {

    @Override
    public @NotNull @Unmodifiable List<R> getRankHistory() {
        return getUser().getList("rank_list").map(
                list -> list.toRecordList(KissenPlayerRankNode.class).toRecordList().stream().map(
                        rank -> translateRank(rank, rankDataWriter())).sorted(
                        Comparator.comparing(PlayerRank::getStart)).toList()).orElse(new ArrayList<>());
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
        KissenPlayerRankNode kissenPlayerRankNode = new KissenPlayerRankNode(id, rank.getName(), new TemporalMeasureNode(accurateDuration));
        R playerRank = translateRank(kissenPlayerRankNode, record ->
        {});
        return setRank(playerRank);
    }

    @Override
    public @NotNull @Unmodifiable Set<UUID> getAltAccounts() throws BackendException {

        //key = total_id AND value = getTotalID
        QuerySelect query = getUser().getMeta().select(Column.TOTAL_ID).where(Column.KEY, "total_id", FilterType.EXACT_MATCH).and(Column.VALUE, getTotalID().toString(), FilterType.EXACT_MATCH);

        Function<String[], UUID> toUUID = data -> UUID.fromString(data[0].substring(getUser().getSaveID().length()));
        Predicate<UUID> byUser = uuid -> !getUniqueId().equals(uuid);

        CompletableFuture<String[][]> alts = query.execute();
        return alts.thenApply(result -> Arrays.stream(result).map(toUUID).filter(byUser).collect(Collectors.toSet())).join();
    }

    @Override
    public @NotNull UUID getTotalID() {
        return getUser().get("total_id").map(UUID::fromString).orElse(getUniqueId());
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
    public long getOnlineTime() {
        return getOnlineTime(getUser());
    }

    @Override
    public long getLastPlayed() {
        return getLastPlayed(getUser());
    }

    @Override
    public @NotNull Set<Suffix> getSuffixSet() {
        SavableRecordList<SuffixNode> recordList = getSuffixList(getUser());
        Function<SuffixNode, KissenSuffix> translator = suffixNode -> new KissenSuffix(suffixNode, suffixDataWriter());
        Set<Suffix> suffixes = recordList.toRecordList().stream().map(translator).collect(Collectors.toSet());
        getRank().getSource().getSuffix().ifPresent(component ->
        {
            KissenSuffix kissenSuffix = new KissenSuffix(new SuffixNode("rank", component), null);
            suffixes.add(kissenSuffix);
        });
        return Collections.unmodifiableSet(suffixes);
    }

    @Override
    public @NotNull Optional<Suffix> getSuffix(@NotNull String name) {
        return getSuffixSet().stream().filter(suffix -> suffix.getName().equals(name)).findFirst();
    }

    @Override
    public @NotNull Suffix grantSuffix(@NotNull String name, @NotNull Component content)
    {
        return grantSuffix(name, content, null);
    }

    @Override
    public @NotNull Suffix grantSuffix(@NotNull String name, @NotNull Component content, @Nullable AccurateDuration accurateDuration) throws EventCancelledException
    {
        SuffixNode suffixNode = new SuffixNode(name, content, new TemporalMeasureNode(accurateDuration));
        SavableRecordList<SuffixNode> suffixes = getSuffixList(getUser());
        if (suffixes.replaceRecord(suffix -> suffix.name().equals(name), suffixNode) == 0)
        {
            suffixes.add(suffixNode);
        }

        return new KissenSuffix(suffixNode, suffixDataWriter());
    }

    @Override
    public boolean revokeSuffix(@NotNull String name)
    {
        return getSuffix(name).filter(TemporalObject::isValid).map(suffix -> {
            suffix.setEnd(Instant.now());
            return true;
        }).orElse(false);
    }

    @Override
    public @NotNull Optional<Suffix> getSelectedSuffix() {
        String setting = getUserSetting(SuffixSetting.class).getValue();
        if (setting.equals("none"))
        {
            return Optional.empty();
        }
        return getSuffix(setting).filter(TemporalObject::isValid);
    }

    @Override
    public @NotNull Locale getCurrentLocale() {
        LocalizationImplementation localizationImplementation = KissenCore.getInstance().getImplementation(LocalizationImplementation.class);
        return localizationImplementation.getLocale(getUser().get("forced_locale").orElse(getUser().get("locale").orElse(localizationImplementation.getDefaultLocale().toString())));
    }

    @Override
    public @NotNull Component styledRankName() {
        TextComponent.Builder builder = Component.text();
        Rank rank = getRank().getSource();

        TextColor rankTheme = getLastColor(rank.getPrefix().orElse(Component.empty())).orElse(rank.getChatColor());
        rank.getPrefix().ifPresent(prefix -> builder.append(prefix).appendSpace());
        builder.append(displayName().color(rankTheme));
        if(getUserSetting(SuffixInChatSetting.class).getValue())
        {
            getSelectedSuffix().ifPresent(suffix -> builder.appendSpace().append(suffix.getContent().color(rankTheme)));
        }
        return builder.asComponent();
    }

    @Override
    public @NotNull <X> UserSetting<X> getUserSetting(@NotNull Class<? extends PlayerSetting<X>> settingClass) {
        return getUserSetting(getUser(), settingClass);
    }

    @Override
    public @NotNull Component displayName() {
        return Component.text(getName());
    }

    @Override
    public @NotNull String getName() {
        return getUser().getNotNull("name");
    }

    @Override
    public @NotNull Theme getTheme() {
        return new PlayerTheme(this);
    }

    @Override
    public final boolean isClient() {
        return true;
    }

    public @NotNull R setRank(@NotNull R playerRank)
    {
        KissenPlayerRankNode rankNode = ((KissenPlayerRank<?>) playerRank).getKissenPlayerRankNode();

        SavableList savableList = getUser().getListNotNull("rank_list");
        SavableRecordList<KissenPlayerRankNode> ranks = savableList.toRecordList(KissenPlayerRankNode.class);
        if (ranks.replaceRecord(rank -> rank.equals(rankNode), rankNode) == 0)
        {
            ranks.add(rankNode);
        }
        return translateRank(rankNode, rankDataWriter());
    }

    /**
     * Returns a {@link DataWriter} instance that writes changes to the user's rank_list. The {@link DataWriter} instance
     * returned by this method takes a {@link Record} argument and updates or adds the given record to the user's rank_list.
     * The returned {@link DataWriter} instance is used to write data changes for a given record in the {@link KissenCore} plugin.
     * It first checks if the given record already exists in the user's rank_list using the {@link SavableRecordList#contains(Object)}
     * method. If the record exists, it replaces it with the new record using the {@link SavableRecordList#replaceRecord(Predicate, Record)}
     * method, where the first parameter is a {@link Predicate} that tests whether a given element should be replaced, and the second
     * parameter is the new record. Otherwise, it adds the new record to the user's rank_list using the {@link SavableRecordList#add(Object)}
     * method.
     *
     * @return a {@link DataWriter} instance that writes changes to the user's rank_list.
     * @throws NullPointerException if the user's rank_list does not exist.
     * @see DataWriter
     * @see Record
     * @see SavableRecordList
     */
    protected @NotNull DataWriter rankDataWriter()
    {
        return record -> {
            SavableRecordList<KissenPlayerRankNode> savableRecordList = getUser().getListNotNull("rank_list").toRecordList(KissenPlayerRankNode.class);
            if (savableRecordList.contains((KissenPlayerRankNode) record)) {
                savableRecordList.replaceRecord((currentRank) -> currentRank.equals(record), (KissenPlayerRankNode) record);
                return;
            }
            savableRecordList.add((KissenPlayerRankNode) record);
        };
    }

    private @NotNull DataWriter suffixDataWriter()
    {
        return suffixDataWriter(getUser());
    }

    protected @NotNull DataWriter suffixDataWriter(@NotNull User user)
    {
        return (suffix) ->
        {
            SuffixNode suffixNode = (SuffixNode) suffix;
            SavableRecordList<SuffixNode> suffixes = getSuffixList(user);
            if (suffixes.replaceRecord(current -> current.name().equals(suffixNode.name()), suffixNode) == 0)
            {
                suffixes.add(suffixNode);
            }
        };
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

    protected <X> UserSetting<X> getUserSetting(@NotNull User user, @NotNull Class<? extends PlayerSetting<X>> settingClass)
    {
        try
        {
            Class<UserImplementation> clazz = UserImplementation.class;
            UserImplementation userImplementation = KissenCore.getInstance().getImplementation(clazz);
            Stream<PlayerSetting<?>> settingStream = userImplementation.getUserSettings().stream();

            Predicate<PlayerSetting<?>> predicate = currentSetting -> currentSetting.getClass().equals(settingClass);
            return (UserSetting<X>) new KissenUserBoundSettings<>(
                    settingStream.filter(predicate).findFirst().orElseThrow(ClassCastException::new), user);
        }
        catch (ClassCastException classCastException)
        {
            throw new IllegalArgumentException(String.format(
                    "Attempted to request content from setting %s which isn't registered. Please ensure the setting %s is correctly registered before trying to fetch its content.",
                    settingClass.getSimpleName(), settingClass.getSimpleName()));
        }
    }

    public long getOnlineTime(@NotNull User user) {
        return isConnected() ? Long.parseLong(user.get("online_time").orElse("0")) + (System.currentTimeMillis() - (long) user.getStorage().get("time_joined")) : Long.parseLong(user.get("online_time").orElse("-1"));
    }

    public long getLastPlayed(@NotNull User user) {
        return isConnected() ? System.currentTimeMillis() : Long.parseLong(user.get("last_played").orElse("-1"));
    }

    private @NotNull Optional<TextColor> getLastColor(@NotNull Component component)
    {
        TextColor color = component.color();
        if (!component.children().isEmpty())
        {
            color = component.children().get(component.children().size() - 1).color();
        }
        return Optional.ofNullable(color);
    }

    protected abstract @NotNull R translateRank(@NotNull KissenPlayerRankNode kissenPlayerRankNode, @Nullable DataWriter dataWriter);

    protected abstract @NotNull R fallbackRank();

    protected @NotNull SavableRecordList<SuffixNode> getSuffixList(@NotNull User user)
    {
        return user.getListNotNull("suffix_list").toRecordList(SuffixNode.class);
    }
}
