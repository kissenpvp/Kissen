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

package net.kissenpvp.core.user;

import net.kissenpvp.core.api.database.meta.BackendException;
import net.kissenpvp.core.api.database.meta.list.MetaList;
import net.kissenpvp.core.api.database.savable.Savable;
import net.kissenpvp.core.api.database.savable.SavableInitializeException;
import net.kissenpvp.core.api.database.savable.SavableMap;
import net.kissenpvp.core.api.permission.AbstractPermission;
import net.kissenpvp.core.api.user.User;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.user.rank.PlayerRankNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;


public abstract class KissenPublicUser<T extends AbstractPermission> extends KissenUser<T> implements User {

    /**
     * Creates a public user which is based on the parameter given just an abstract user or a specific player.
     *
     * @param name of the player this class should represent, null if it's just an abstraction.
     * @param uuid of the player this class should represent, null if it's just an abstraction.
     */
    public KissenPublicUser(@Nullable UUID uuid, @Nullable String name) throws BackendException {
        super(uuid, name);

        if (name!=null && uuid!=null) {
            String currentName = getRepository().getNotNull("name", String.class);
            if (!Objects.equals(currentName, name)) {
                updateName(name, currentName);
            }
        }
    }

    public @NotNull MetaList<PlayerRankNode> getRankNodes()
    {
        return getRepository().getListNotNull("rank_list", PlayerRankNode.class);
    }

    public void updateLocale(@NotNull String locale)
    {
        // TODO intervene when default
        getRepository().set("locale", locale);
    }

    /**
     * Updates the name of the user and logs the change.
     *
     * <p>The {@code updateName} method is used to update the name of the user to the specified name.
     * It also logs the name change, and removes the cached profiles with the previous name.</p>
     *
     * @param name        the new name for the user
     * @param currentName the current name of the user
     * @throws NullPointerException if the specified new name is `null`
     */
    private void updateName(@NotNull String name, String currentName) {
        String message = "The user '{}' has changed their name from '{}' to '{}'.";
        KissenCore.getInstance().getLogger().debug(message, getRawID(), getRepository().getNotNull("name", String.class), name);
        getImplementation().getCachedProfiles().removeIf(cached -> Objects.equals(currentName, cached.name()));
        getRepository().set("name", name);
    }

    @Override
    public String[] getKeys() {
        return new String[]{"name"};
    }

    @Override
    protected @NotNull Map<String, Object> getDefaultData(@NotNull UUID uuid, String name) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("total_id", uuid);
        return data;
    }

    @Override
    public @NotNull Savable setup(@NotNull String id) throws SavableInitializeException, BackendException {
        Savable value = super.setup(id);
        String name = getRepository().getNotNull("name", String.class);
        getImplementation().cacheProfile(new UserInfoNode(UUID.fromString(id), name));
        return value;
    }

    @Override
    public final @NotNull String getSaveID() {
        return getImplementation().getUserSaveID();
    }

    /**
     * Checks the host address from every player who joined and connects accounts which got the same address.
     */
    public void findAltAccounts() throws BackendException {
        /*String name = getNotNull("name");
        UUID totalID = UUID.fromString(get("total_id").orElse(getRawID()));

        Map<String, Object> hosts =
                KissenCore.getInstance().getImplementation(StorageImplementation.class).getStorage("host");

        String host = String.valueOf(hosts.get(getRawID()));
        Map<String, SavableMap> data = getMeta().getData(this);
        hosts.forEach((key, value) -> handleHost(totalID, host, data, key, value));

        printAltStatus(name, totalID, data);*/
        //TODO finds hosts
    }

    private void printAltStatus(String name, UUID totalID, Map<String, SavableMap> data) {
        /*final List<UUID> altAccountList = new ArrayList<>(((KissenPlayerClient<?, ?, ?>) getPlayerClient()).getAltAccounts(totalID, getSaveID(), getMeta()));
        if (!altAccountList.isEmpty()) {
            if (altAccountList.size() == 1) {
                //KissenCore.getInstance().getImplementation(ChatImplementation.class).notifyTeam(new PlayerAccountIsRelated(name, data.get(getSaveID() + altAccountList.get(0).toString()).getNotNull("name")));
                //TODO well, I'm working on the language system
            } else {
                StringBuilder altAccounts = new StringBuilder();
                for (int i = 0; i < altAccountList.size(); i++) {
                    altAccounts.append(data.get(getSaveID() + altAccountList.get(i)).getNotNull("name"));
                    if (KissenCore.getInstance()
                            .getImplementation(ConfigurationImplementation.class)
                            .getSetting(HighlightVariables.class)) {
                        altAccounts.append("§t")
                                .append(" ")
                                .append("[")
                                .append("§p")
                                .append(KissenCore.getInstance().isOnline(altAccountList.get(i)) ? "§+✔" : "§-❌")
                                .append("§t")
                                .append("]")
                                .append(i == altAccountList.size() - 1 ? "" : ", ");
                    } else {
                        altAccounts.append(" ")
                                .append("[")
                                .append(KissenCore.getInstance().isOnline(altAccountList.get(i)) ? "✔" : "❌")
                                .append("]")
                                .append(i == altAccountList.size() - 1 ? "" : ", ");
                    }
                }
                //KissenCore.getInstance().getImplementation(ChatImplementation.class).notifyTeam(new PlayerAccountIsRelatedToAccounts(name, altAccounts.toString()));
                //TODO same here as above
            }
        }*/ //TODO
    }

    private void handleHost(UUID totalID, String host, Map<String, SavableMap> data, String key, @NotNull Object value) {
        if (value.equals(host) && !key.equals(getRawID())) {
            SavableMap savableMap = data.get(getSaveID() + key);

            UUID targetID = savableMap.getNotNull("total_id", UUID.class);
            if (!targetID.equals(totalID)) {
                try {
                    getImplementation().rewriteTotalID(targetID, totalID);
                } catch (Exception exception) {
                    KissenCore.getInstance().getLogger().error("Could not update the totalId from '{}' to '{}'", targetID, totalID, exception);
                }
            }
        }
    }
}
