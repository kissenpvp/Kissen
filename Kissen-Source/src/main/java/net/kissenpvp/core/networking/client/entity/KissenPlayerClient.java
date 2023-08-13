package net.kissenpvp.core.networking.client.entity;

import net.kissenpvp.core.api.ban.Ban;
import net.kissenpvp.core.api.ban.BanImplementation;
import net.kissenpvp.core.api.ban.BanOperator;
import net.kissenpvp.core.api.ban.Punishment;
import net.kissenpvp.core.api.database.DataImplementation;
import net.kissenpvp.core.api.database.meta.BackendException;
import net.kissenpvp.core.api.database.queryapi.Column;
import net.kissenpvp.core.api.database.queryapi.FilterType;
import net.kissenpvp.core.api.database.savable.list.SavableRecordList;
import net.kissenpvp.core.api.message.ComponentSerializer;
import net.kissenpvp.core.api.message.Theme;
import net.kissenpvp.core.api.message.localization.LocalizationImplementation;
import net.kissenpvp.core.api.networking.client.entitiy.PlayerClient;
import net.kissenpvp.core.api.permission.Permission;
import net.kissenpvp.core.api.user.User;
import net.kissenpvp.core.api.user.UserImplementation;
import net.kissenpvp.core.api.user.rank.PlayerRank;
import net.kissenpvp.core.api.user.rank.Rank;
import net.kissenpvp.core.api.user.suffix.Suffix;
import net.kissenpvp.core.api.user.usersetttings.PlayerSetting;
import net.kissenpvp.core.api.user.usersetttings.UserSetting;
import net.kissenpvp.core.api.util.Container;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.database.DataWriter;
import net.kissenpvp.core.message.PlayerTheme;
import net.kissenpvp.core.user.rank.KissenPlayerRank;
import net.kissenpvp.core.user.rank.KissenPlayerRankNode;
import net.kissenpvp.core.user.suffix.KissenSuffix;
import net.kissenpvp.core.user.suffix.SuffixNode;
import net.kissenpvp.core.user.usersettings.KissenUserBoundSettings;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.time.Duration;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class KissenPlayerClient<P extends Permission, R extends PlayerRank<?>, B extends Punishment<?>> implements PlayerClient<P, R, B> {

    @Override
    public @NotNull @Unmodifiable List<R> getRankHistory() {
        return getUser().getList("rank_list")
                .map(list -> list.toRecordList(KissenPlayerRankNode.class)
                        .toRecordList()
                        .stream()
                        .map(rank -> translateRank(rank, getRankSaveChanges()))
                        .sorted(Comparator.comparingLong(PlayerRank::getStart))
                        .toList())
                .orElse(new ArrayList<>());
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
    public @NotNull R grantRank(@NotNull Rank rank, @Nullable Duration duration) {
        String id = KissenCore.getInstance().getImplementation(DataImplementation.class).generateID();

        Optional<Duration> optionalDuration = Optional.ofNullable(duration);
        long start = System.currentTimeMillis();

        Long milliDuration = optionalDuration.map(Duration::toMillis).orElse(null);
        Long end = optionalDuration.map(Duration::toMillis).map(val -> System.currentTimeMillis() + val).orElse(null);

        KissenPlayerRankNode kissenPlayerRankNode = new KissenPlayerRankNode(id, rank.getName(), start, new Container<>(milliDuration), new Container<>(end), end);
        R playerRank = translateRank(kissenPlayerRankNode, record -> {});
        setRank(playerRank);
        return playerRank;
    }

    @Override
    public @NotNull @Unmodifiable Set<UUID> getAltAccounts() throws BackendException {
        return Arrays.stream(getUser().getMeta()
                        .execute(getUser().getMeta()
                                .select(Column.TOTAL_ID)
                                .appendFilter(Column.KEY, "total_id", FilterType.EQUALS)
                                .appendFilter(Column.VALUE, getTotalID().toString(), FilterType.EQUALS)))
                .map(data -> data[0].substring(getUser().getSaveID().length()))
                .map(UUID::fromString)
                .collect(Collectors.toSet());
    }

    @Override
    public @NotNull UUID getTotalID() {
        return getUser().get("total_id").map(UUID::fromString).orElse(getUniqueId());
    }

    @Override
    public @NotNull B ban(@NotNull Ban ban, @NotNull BanOperator banOperator) throws BackendException {
        return ban(ban, banOperator, null);
    }

    @Override
    public @NotNull B ban(@NotNull Ban ban, @NotNull BanOperator banOperator, @Nullable Component reason) throws BackendException {
        return (B) KissenCore.getInstance()
                .getImplementation(BanImplementation.class)
                .ban(getTotalID(), ban, banOperator, reason);
    }

    @Override
    public @NotNull Optional<@Nullable B> getBan(@NotNull String id) throws BackendException {
        return getBanHistory().stream().filter(punishment -> punishment.getID().equals(id)).findFirst();
    }

    @Override
    public @NotNull @Unmodifiable List<B> getBanHistory() throws BackendException {
        return (List<B>) KissenCore.getInstance()
                .getImplementation(BanImplementation.class)
                .getPlayerBanSet(getTotalID());
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
        return getUser().getListNotNull("suffix_list")
                .toRecordList(SuffixNode.class)
                .toRecordList()
                .stream()
                .map(KissenSuffix::new)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public @NotNull Optional<@Nullable Suffix> getSuffix(@NotNull String name) {
        return getSuffixSet().stream().filter(suffix -> suffix.getName().equals(name)).findFirst();
    }

    @Override
    public @NotNull Optional<@Nullable Suffix> setSuffix(@NotNull String name, @NotNull Component content) {
        SuffixNode suffixNode = new SuffixNode(name, ComponentSerializer.getInstance()
                .getJsonSerializer()
                .serialize(content));
        SavableRecordList<SuffixNode> savableRecordList = getUser().getListNotNull("suffix_list")
                .toRecordList(SuffixNode.class);

        if (savableRecordList.contains(suffixNode)) {
            return overrideSuffix(name, savableRecordList, suffixNode);
        }

        if (!savableRecordList.add(suffixNode)) {
            KissenCore.getInstance()
                    .getLogger()
                    .error("A suffix by the name '{}' from user '{}' could not be added and only god knows why (and probably some programmers who are better than me).", name, getName());
        }
        return Optional.empty();
    }

    @Override
    public boolean deleteSuffix(@NotNull String name) {
        return getUser().getListNotNull("suffix_list")
                .toRecordList(SuffixNode.class)
                .removeIfRecord(currentRecord -> currentRecord.name().equals(name));
    }

    @Override
    public @NotNull Optional<@Nullable Suffix> getSelectedSuffix() {
        return getUser().get("selected_suffix").flatMap(this::getSuffix);
    }

    @Override
    public void setSelectedSuffix(@NotNull String name) throws NullPointerException {
        getUser().set("selected_suffix", Objects.requireNonNull(getSuffix(name).orElseThrow(NullPointerException::new))
                .getName());
    }

    @Override
    public @NotNull Locale getCurrentLocale() {
        LocalizationImplementation localizationImplementation = KissenCore.getInstance()
                .getImplementation(LocalizationImplementation.class);
        return localizationImplementation.getLocale(getUser().get("forced_language")
                .orElse(getUser().get("language").orElse(localizationImplementation.getDefaultLocale().toString())));
    }

    @Override
    public @NotNull Component styledRankName() {
        return getRank().getSource()
                .map(rank -> Component.empty()
                        .append(rank.getPrefix()
                                .map(prefix -> prefix.append(Component.space()))
                                .orElse(Component.empty()))
                        .append(displayName())
                        .append(rank.getSuffix()
                                .map(suffix -> Component.space().append(suffix))
                                .orElse(Component.empty())))
                .orElse(Component.empty().append(displayName()));
    }

    @Override
    public @NotNull <X> UserSetting<X> getUserSetting(@NotNull Class<? extends PlayerSetting<X>> settingClass) {
        try {
            return (UserSetting<X>) new KissenUserBoundSettings<>(KissenCore.getInstance()
                    .getImplementation(UserImplementation.class)
                    .getUserSettings()
                    .stream()
                    .filter(currentSetting -> currentSetting.getClass().equals(settingClass))
                    .findFirst()
                    .orElseThrow(ClassCastException::new), this.getUniqueId());
        } catch (ClassCastException classCastException) {
            throw new IllegalArgumentException(String.format("Attempted to request content from setting %s which isn't registered. Please ensure the setting %s is correctly registered before trying to fetch its content.", settingClass.getSimpleName(), settingClass.getSimpleName()));
        }
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
    public @NotNull UUID getUniqueId() {
        return UUID.fromString(getUser().getRawID());
    }

    @Override
    public @NotNull Theme getTheme() {
        return new PlayerTheme(this);
    }

    @Override
    public final boolean isClient() {
        return true;
    }

    @SuppressWarnings("UnusedReturnValue")
    public @NotNull Optional<R> setRank(@NotNull R playerRank) {
        KissenPlayerRankNode kissenPlayerRankNode = ((KissenPlayerRank<?>) playerRank).getKissenPlayerRankNode();
        return getUser().getListNotNull("rank_list")
                .toRecordList(KissenPlayerRankNode.class)
                .toRecordList()
                .stream()
                .filter(rank -> rank.equals(kissenPlayerRankNode))
                .findFirst()
                .map(oldOne -> Optional.of(overrideRank(translateRank(oldOne, null), playerRank)))
                .orElseGet(() -> {
                    addRank(kissenPlayerRankNode);
                    return Optional.empty();
                });
    }

    /**
     * Overrides the user's current rank with a new rank. This method takes two arguments, both of which are {@link R} objects: the old rank
     * that the user had and the new rank that the user will have.
     * This method updates the user's rank by creating a new {@link KissenPlayerRankNode} object from the new rank and updating the corresponding
     * record in the user's "rank_list" with the new {@link KissenPlayerRankNode}. If the update is successful, it returns the old rank.
     *
     * @param oldRank    the old rank that the user had
     * @param playerRank the new rank that the user will have
     * @return the old rank that the user had
     * @throws NullPointerException if either of the {@link R} arguments are null
     * @see R
     * @see KissenPlayerRankNode
     * @see User#getList(String)
     * @see SavableRecordList#toRecordList(Class)
     * @see SavableRecordList#replaceRecord(Predicate, Record)
     */
    private @NotNull R overrideRank(@NotNull R oldRank, @NotNull R playerRank) {
        KissenPlayerRankNode kissenPlayerRankNode = ((KissenPlayerRank<?>) playerRank).getKissenPlayerRankNode();
        //TODO events
        getUser().getListNotNull("rank_list")
                .toRecordList(KissenPlayerRankNode.class)
                .replaceRecord((rank -> rank.equals(kissenPlayerRankNode)), kissenPlayerRankNode);
        return oldRank;
    }

    /**
     * Adds the specified {@link KissenPlayerRankNode} object to the user's rank_list.
     * If the rank_list does not exist, it is created and the {@link KissenPlayerRankNode} object is added to it.
     * <p>
     * The method first serializes the {@link KissenPlayerRankNode} object to a JSON string representation
     * using the {@link DataImplementation#toJson(Record)} method.
     *
     * @param kissenPlayerRankNode the {@link KissenPlayerRankNode} object to be added to the user's rank_list
     * @throws NullPointerException if the kissenPlayerRankNode argument is null
     * @see User#containsList(String)
     * @see User#getList(String)
     * @see User#setList(String, List)
     * @see SavableRecordList#toRecordList(Class)
     */
    private void addRank(@NotNull KissenPlayerRankNode kissenPlayerRankNode) throws NullPointerException {

        if (!getUser().containsList("rank_list")) {
            String jsonRankNode = KissenCore.getInstance()
                    .getImplementation(DataImplementation.class)
                    .toJson(kissenPlayerRankNode);
            getUser().setList("rank_list", Collections.singletonList(jsonRankNode));
            return;
        }

        getUser().getListNotNull("rank_list").toRecordList(KissenPlayerRankNode.class).add(kissenPlayerRankNode);
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
    protected @NotNull DataWriter getRankSaveChanges() {
        return record -> {
            SavableRecordList<KissenPlayerRankNode> savableRecordList = getUser().getListNotNull("rank_list")
                    .toRecordList(KissenPlayerRankNode.class);
            if (savableRecordList.contains((KissenPlayerRankNode) record)) {
                savableRecordList.replaceRecord((currentRank) -> currentRank.equals(record), (KissenPlayerRankNode) record);
                return;
            }
            savableRecordList.add((KissenPlayerRankNode) record);
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
            if (rankList.get(i).getSource().isPresent() && rankList.get(i).isValid()) {
                index = i;
                break;
            }
        }
        return Optional.ofNullable(index);
    }

    public long getOnlineTime(User user) {
        return isConnected() ? Long.parseLong(user.get("online_time")
                .orElse("0")) + (System.currentTimeMillis() - (long) user.getStorage()
                .get("time_joined")) : Long.parseLong(user.get("online_time").orElse("-1"));
    }

    public long getLastPlayed(User user) {
        return isConnected() ? System.currentTimeMillis() : Long.parseLong(getUser().get("last_played").orElse("-1"));
    }

    private @NotNull Optional<Suffix> overrideSuffix(@NotNull String name, SavableRecordList<SuffixNode> savableRecordList, SuffixNode suffixNode) {
        Optional<Suffix> suffix = savableRecordList.toRecordList()
                .stream()
                .filter(currentSuffix -> currentSuffix.name().equals(name))
                .findFirst()
                .map(KissenSuffix::new);

        switch (savableRecordList.replaceRecord((current -> current.equals(suffixNode)), suffixNode)) {
            case 0 -> KissenCore.getInstance()
                    .getLogger()
                    .warn("A suffix by the name '{}' from user '{}' was found in the list but was not replaced.", name, getName());
            case 1 -> { /* ignored */ }
            default -> KissenCore.getInstance()
                    .getLogger()
                    .warn("A duplicated suffix by the name '{}' from user '{}' has been detected, this can only happen, if the dataset was manually adjusted. It's advised to clear this duplicate.", name, getName());
        }
        return suffix;
    }

    protected abstract @NotNull R translateRank(@NotNull KissenPlayerRankNode kissenPlayerRankNode, @Nullable DataWriter dataWriter);

    protected abstract @NotNull R fallbackRank();
}
