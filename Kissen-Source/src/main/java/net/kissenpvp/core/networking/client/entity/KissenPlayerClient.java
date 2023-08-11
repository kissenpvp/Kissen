package net.kissenpvp.core.networking.client.entity;

import net.kissenpvp.core.api.ban.Ban;
import net.kissenpvp.core.api.ban.BanImplementation;
import net.kissenpvp.core.api.ban.PlayerBan;
import net.kissenpvp.core.api.database.DataImplementation;
import net.kissenpvp.core.api.database.meta.Meta;
import net.kissenpvp.core.api.database.queryapi.Column;
import net.kissenpvp.core.api.database.queryapi.FilterType;
import net.kissenpvp.core.api.database.queryapi.QuerySelect;
import net.kissenpvp.core.api.database.savable.list.SavableRecordList;
import net.kissenpvp.core.api.event.EventImplementation;
import net.kissenpvp.core.api.message.ComponentSerializer;
import net.kissenpvp.core.api.message.localization.LocalizationImplementation;
import net.kissenpvp.core.api.networking.client.entitiy.PlayerClient;
import net.kissenpvp.core.api.networking.client.entitiy.event.RankGrantEvent;
import net.kissenpvp.core.api.networking.client.entitiy.event.RankUpdateEvent;
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

public interface KissenPlayerClient<PLAYERRANK extends PlayerRank<?>> extends PlayerClient<PLAYERRANK>, KissenServerEntity {

    @Override
    @NotNull
    default Locale getCurrentLocale() {
        LocalizationImplementation localizationImplementation = KissenCore.getInstance()
                .getImplementation(LocalizationImplementation.class);
        return localizationImplementation.getLocale(getUser().get("forced_language")
                .orElse(getUser().get("language")
                        .orElse(localizationImplementation.getDefaultLocale().toString())));
    }

    @Override
    default @NotNull @Unmodifiable Set<UUID> getAltAccounts() {
        return getAltAccounts(getTotalID(), getUser().getSaveID(), getUser().getMeta());
    }

    @Override
    default @NotNull UUID getTotalID() {
        return UUID.fromString(getUser().get("total_id").orElse(getUser().getRawID()));
    }

    @Override
    @NotNull
    default PlayerBan ban(@NotNull Ban ban, @NotNull Component banOperator) {
        return null; //TODO
    }

    @Override
    @NotNull
    default PlayerBan ban(@NotNull Ban ban, @NotNull Component banOperator, @Nullable Component reason) {
        return null; //TODO
    }

    @Override
    default @NotNull Optional<@Nullable PlayerBan> getBan(@NotNull String id) {
        return getBanHistory().stream().filter(playerBan -> playerBan.getID().equals(id)).findFirst();
    }

    @Override
    default @NotNull @Unmodifiable List<PlayerBan> getBanHistory() {
        List<PlayerBan> playerBans = new ArrayList<>(KissenCore.getInstance()
                .getImplementation(BanImplementation.class)
                .getPlayerBanSet(getTotalID()));
        playerBans.sort(((o1, o2) -> Long.compare(o2.getStart(), o1.getStart())));
        return Collections.unmodifiableList(playerBans);
    }

    @Override
    default @NotNull Component styledRankName() {
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
    default @NotNull Set<Suffix> getSuffixSet() {
        return getUser().getListNotNull("suffix_list")
                .toRecordList(SuffixNode.class)
                .toRecordList()
                .stream()
                .map(KissenSuffix::new)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    default @NotNull Optional<@Nullable Suffix> getSuffix(@NotNull String name) {
        return getSuffixSet().stream().filter(suffix -> suffix.getName().equals(name)).findFirst();
    }

    @Override
    default @NotNull Optional<@Nullable Suffix> setSuffix(@NotNull String name, @NotNull Component content) {
        SuffixNode suffixNode = new SuffixNode(name, ComponentSerializer.getInstance()
                .getJsonSerializer()
                .serialize(content));
        SavableRecordList<SuffixNode> savableRecordList = getUser().getListNotNull("suffix_list")
                .toRecordList(SuffixNode.class);
        Optional<Suffix> suffix = Optional.empty();
        if (savableRecordList.contains(suffixNode)) {
            suffix = savableRecordList.toRecordList()
                    .stream()
                    .filter(currentSuffix -> currentSuffix.name().equals(name))
                    .findFirst()
                    .map(KissenSuffix::new);
            switch (savableRecordList.replaceRecord((current -> current.equals(suffixNode)), suffixNode)) {
                case 0 -> KissenCore.getInstance()
                        .getLogger()
                        .warn("A suffix by by the name '{}' from user '{}' was found in the list but was not replaced.", name, getName());
                case 1 -> { /* ignored */ }
                default -> KissenCore.getInstance()
                        .getLogger()
                        .warn("A duplicated suffix by the name '{}' from user '{}' has been detected, this can only happen, if the dataset was manually adjusted. It's advised to clear this duplicate.", name, getName());
            }
        } else {
            if (savableRecordList.add(suffixNode)) {
                KissenCore.getInstance()
                        .getLogger()
                        .error("A suffix by by the name '{}' from user '{}' could not be added and only god knows why (and probably some programmers who are better than me).", name, getName());
            }
        }
        return suffix;
    }

    @Override
    default boolean deleteSuffix(@NotNull String name) {
        return getUser().getListNotNull("suffix_list")
                .toRecordList(SuffixNode.class)
                .removeIfRecord(currentRecord -> currentRecord.name().equals(name));
    }

    @Override
    default @NotNull Optional<@Nullable Suffix> getSelectedSuffix() {
        return getUser().get("").flatMap(this::getSuffix);
    }

    @Override
    default void setSelectedSuffix(@NotNull String name) throws NullPointerException {
        getUser().set("selected_suffix", Objects.requireNonNull(getSuffix(name).orElseThrow(NullPointerException::new))
                .getName());
    }

    @Override
    default @NotNull @Unmodifiable List<PLAYERRANK> getRankHistory() {

        return getUser().getList("rank_list").map(list -> list.toRecordList(KissenPlayerRankNode.class)
                .toRecordList()
                .stream()
                .map(rank -> translateRank(rank, getRankSaveChanges()))
                .sorted(Comparator.comparingLong(PlayerRank::getStart))
                .toList()).orElse(new ArrayList<>());
    }

    @Override
    default @NotNull PLAYERRANK grantRank(@NotNull Rank rank) {
        return grantRank(rank, null);
    }

    @Override
    default @NotNull PLAYERRANK grantRank(@NotNull Rank rank, @Nullable Duration duration) {
        long start = System.currentTimeMillis();
        long end = duration != null ? start + duration.toMillis() : -1;
        KissenPlayerRankNode kissenPlayerRankNode = new KissenPlayerRankNode(KissenCore.getInstance()
                .getImplementation(DataImplementation.class)
                .generateID(), rank.getName(), start, new Container<>(duration != null ? duration.toMillis() : null), new Container<>(end), end);
        PLAYERRANK playerRank = translateRank(kissenPlayerRankNode, record -> {
        });
        setRank(playerRank);
        return playerRank;
    }

    /**
     * Updates or adds rank data from a players rank.
     * Whether it is replaced or added is checked using {@link PlayerRank#getID()}.
     *
     * @param playerRank that should be updated or removed.
     * @return the rank which it was before it got edited.
     */
    @SuppressWarnings("UnusedReturnValue")
    default @Nullable Optional<PLAYERRANK> setRank(@NotNull PLAYERRANK playerRank) {
        KissenPlayerRankNode kissenPlayerRankNode = ((KissenPlayerRank<?>) playerRank).getKissenPlayerRankNode();
        return getUser().getListNotNull("rank_list")
                .toRecordList(KissenPlayerRankNode.class)
                .toRecordList()
                .stream()
                .filter(rank -> rank.equals(kissenPlayerRankNode))
                .findFirst()
                .map(oldOne -> Optional.of(overrideRank(translateRank(oldOne, null), playerRank)))
                .orElseGet(() -> {
                    RankGrantEvent rankGrantEvent = new RankGrantEvent(this, playerRank);
                    if (KissenCore.getInstance().getImplementation(EventImplementation.class).call(rankGrantEvent)) {
                        addRank(kissenPlayerRankNode);
                    }
                    return Optional.empty();
                });
    }


    @Override
    default long getOnlineTime() {
        return getOnlineTime(getUser());
    }

    default long getOnlineTime(User user) {
        return isConnected() ? Long.parseLong(user.get("online_time")
                .orElse("0")) + (System.currentTimeMillis() - (long) user.getStorage()
                .get("time_joined")) : Long.parseLong(user.get("online_time").orElse("-1"));
    }

    @Override
    default long getLastPlayed() {
        return getLastPlayed(getUser());
    }

    default long getLastPlayed(User user) {
        return isConnected() ? System.currentTimeMillis() : Long.parseLong(getUser().get("last_played").orElse("-1"));
    }

    @Override
    default @NotNull <X> UserSetting<X> getUserSetting(Class<? extends PlayerSetting<X>> settingClass) {
        try {
            PlayerSetting<X> playerSetting = (PlayerSetting<X>) KissenCore.getInstance()
                    .getImplementation(UserImplementation.class)
                    .getUserSettings()
                    .stream()
                    .filter(currentSetting -> currentSetting.getClass().equals(settingClass))
                    .findFirst()
                    .orElse(null);
            if (playerSetting == null) {
                throw new ClassCastException();
            }
            return new KissenUserBoundSettings<>(playerSetting, this.getUniqueId());
        } catch (ClassCastException classCastException) {
            throw new IllegalArgumentException("Requesting content of unregistered setting.");
        }
    }

    /**
     * Returns all accounts sharing the same {@link #getTotalID()} as this user.
     * These are most likely alt accounts, and therefore they are connected.
     *
     * @param totalID of this user to determine all other players having this one.
     * @param saveID  the save sender the users have.
     * @param meta    the meta the users are saved in.
     * @return an unmodifiable {@link Set} containing a list of all accounts sharing this total sender.
     */
    default @Unmodifiable @NotNull Set<UUID> getAltAccounts(@NotNull UUID totalID, @NotNull String saveID, @NotNull Meta meta) {
        final Set<UUID> altAccounts = new HashSet<>();
        try {
            QuerySelect querySelect = meta.select(Column.TOTAL_ID)
                    .appendFilter(Column.TOTAL_ID, saveID, FilterType.START)
                    .appendFilter(Column.KEY, "total_id", FilterType.EQUALS)
                    .appendFilter(Column.VALUE, String.valueOf(totalID), FilterType.EQUALS);
            for (String[] dataSet : meta.execute(querySelect)) {
                for (String unconvertedUUID : dataSet) {
                    UUID currentUUID = UUID.fromString(unconvertedUUID.substring(saveID.length()));
                    if (!currentUUID.equals(getUniqueId())) {
                        altAccounts.add(currentUUID);
                    }
                }
            }
        } catch (Exception exception) {
            KissenCore.getInstance()
                    .getLogger()
                    .error("Could not fetch alt accounts from user '{}'.", getName(), exception);
        }

        return Collections.unmodifiableSet(altAccounts);
    }

    /**
     * Translates the given {@link KissenPlayerRankNode} object to a {@link PLAYERRANK} object.
     * Implementations of this method should handle the translation from the {@link KissenPlayerRankNode} to the appropriate {@link PLAYERRANK} object.
     *
     * @param kissenPlayerRankNode The {@link KissenPlayerRankNode} object to be translated to a {@link PLAYERRANK} object.
     * @param dataWriter           A {@link DataWriter} object that may be used by implementations to write additional data to the {@link PLAYERRANK}
     *                             object.
     * @return A {@link PLAYERRANK} object representing the given {@link KissenPlayerRankNode}.
     * @throws NullPointerException if the kissenPlayerRankNode argument is null.
     * @see KissenPlayerRankNode
     * @see PLAYERRANK
     * @see DataWriter
     */
    @NotNull PLAYERRANK translateRank(@NotNull KissenPlayerRankNode kissenPlayerRankNode, @Nullable DataWriter dataWriter);

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
    default @NotNull DataWriter getRankSaveChanges() {
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
     * Overrides the user's current rank with a new rank. This method takes two arguments, both of which are {@link PLAYERRANK} objects: the old rank
     * that the user had and the new rank that the user will have.
     * This method updates the user's rank by creating a new {@link KissenPlayerRankNode} object from the new rank and updating the corresponding
     * record in the user's "rank_list" with the new {@link KissenPlayerRankNode}. If the update is successful, it returns the old rank.
     *
     * @param oldRank    the old rank that the user had
     * @param playerRank the new rank that the user will have
     * @return the old rank that the user had
     * @throws NullPointerException if either of the {@link PLAYERRANK} arguments are null
     * @see PLAYERRANK
     * @see KissenPlayerRankNode
     * @see RankUpdateEvent
     * @see User#getList(String)
     * @see SavableRecordList#toRecordList(Class)
     * @see SavableRecordList#replaceRecord(Predicate, Record)
     */
    private @NotNull PLAYERRANK overrideRank(@NotNull PLAYERRANK oldRank, @NotNull PLAYERRANK playerRank) {
        KissenPlayerRankNode kissenPlayerRankNode = ((KissenPlayerRank<?>) playerRank).getKissenPlayerRankNode();
        RankUpdateEvent rankUpdateEvent = new RankUpdateEvent(this, oldRank, playerRank);

        if (KissenCore.getInstance().getImplementation(EventImplementation.class).call(rankUpdateEvent)) {
            getUser().getListNotNull("rank_list")
                    .toRecordList(KissenPlayerRankNode.class)
                    .replaceRecord((rank -> rank.equals(kissenPlayerRankNode)), kissenPlayerRankNode);
        }

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
     * Generates a string of all visual data.
     * This can be checked to see if any changes were made.
     *
     * @return a generated string of all visual data.
     */
    default @NotNull String visualHash() {
        int actualIndex = getRankIndex();
        String hash = String.valueOf(actualIndex);
        if (actualIndex > -1) {
            hash += (getRankHistory().get(actualIndex).getSource().toString());
        }

        Optional<Suffix> suffix = getSelectedSuffix();
        if (suffix.isPresent()) {
            hash += suffix.get().getName() + suffix.get().getContent();
        }

        return hash;
    }

    /**
     * Returns index from the {@link #getRankHistory()} of the rank currently active.
     * This is determined using the {@link PlayerRank#isValid()} method and the {@link PlayerRank#getSource()} is not null.
     *
     * @return the index of the active {@link PlayerRank}
     */
    default int getRankIndex() {
        int index = -1;
        List<PLAYERRANK> rankList = getRankHistory();

        if (rankList.isEmpty()) {
            return index;
        }
        for (int i = rankList.size() - 1; i > -1; i--) {
            if (rankList.get(i).getSource().isPresent() && rankList.get(i).isValid()) {
                index = i;
                break;
            }
        }
        return index;
    }
}
