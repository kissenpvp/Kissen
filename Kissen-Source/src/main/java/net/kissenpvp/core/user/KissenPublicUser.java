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

import net.kissenpvp.core.api.database.StorageImplementation;
import net.kissenpvp.core.api.database.meta.BackendException;
import net.kissenpvp.core.api.database.meta.ObjectMeta;
import net.kissenpvp.core.api.database.savable.SavableInitializeException;
import net.kissenpvp.core.api.database.savable.SavableMap;
import net.kissenpvp.core.api.message.localization.LocalizationImplementation;
import net.kissenpvp.core.api.permission.Permission;
import net.kissenpvp.core.api.user.User;
import net.kissenpvp.core.api.user.UserImplementation;
import net.kissenpvp.core.base.KissenCore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;


public abstract class KissenPublicUser<T extends Permission> extends KissenUser<T> implements User {

    /**
     * Creates a public user which is based on the parameter given just an abstract user or a specific player.
     *
     * @param name of the player this class should represent, null if it's just an abstraction.
     * @param uuid of the player this class should represent, null if it's just an abstraction.
     */
    public KissenPublicUser(@Nullable UUID uuid, @Nullable String name) throws BackendException {
        this(uuid, name, null);
    }

    public KissenPublicUser(@NotNull SavableMap savableMap) throws BackendException {
        this(UUID.fromString(savableMap.getNotNull("id")), savableMap.getNotNull("name"), savableMap);
    }

    /**
     * Creates a public user which is based on the parameter given just an abstract user or a specific player.
     *
     * @param name of the player this class should represent, null if it's just an abstraction.
     * @param uuid of the player this class should represent, null if it's just an abstraction.
     */
    public KissenPublicUser(@Nullable UUID uuid, @Nullable String name, @Nullable Map<String, String> data) throws BackendException {
        super(uuid, name, data);

        if (name != null && uuid != null) {
            if (!getNotNull("name").equals(name)) {
                if (containsKey("name")) {
                    ((KissenUserImplementation) KissenCore.getInstance().getImplementation(UserImplementation.class)).getCachedProfiles().removeIf(userInfoNode -> userInfoNode.name().equals(getNotNull("name")));
                    KissenCore.getInstance().getLogger().debug("The user '{}' has changed their name from '{}' to '{}'.", getRawID(), getNotNull("name"), name);
                }

                set("name", name);
            }
        }
    }

    @Override
    public String[] getKeys() {
        return new String[]{"name"};
    }

    @Override
    protected @NotNull Map<String, String> getDefaultData(@NotNull UUID uuid, String name) {
        Map<String, String> data = new HashMap<>();
        data.put("name", name);
        data.put("total_id", uuid.toString());
        return data;
    }

    @Override
    public void setup(@NotNull String id, @Nullable Map<String, String> meta) throws SavableInitializeException, BackendException {
        super.setup(id, meta);
        KissenCore.getInstance().getImplementation(KissenUserImplementation.class).getCachedProfiles().add(new UserInfoNode(UUID.fromString(id), getNotNull("name")));
    }

    @Override
    public final @NotNull ObjectMeta getMeta() {
        return ((KissenUserImplementation) KissenCore.getInstance()
                .getImplementation(UserImplementation.class)).getUserMeta();
    }

    @Override
    public final @NotNull String getSaveID() {
        return ((KissenUserImplementation) KissenCore.getInstance()
                .getImplementation(UserImplementation.class)).getUserSaveID();
    }

    @Override
    public void set(@NotNull String key, @Nullable String value) {
        String defaultLanguage = KissenCore.getInstance()
                .getImplementation(LocalizationImplementation.class)
                .getDefaultLocale()
                .toString().toLowerCase();
        if (key.equals("language") && Objects.equals(value, defaultLanguage)) {
            delete(key); // delete if default language is updated
            return;
        }
        super.set(key, value);
    }

    /**
     * Checks the host address from every player who joined and connects accounts which got the same address.
     */
    public void findAltAccounts() throws BackendException {
        String name = getNotNull("name");
        UUID totalID = UUID.fromString(get("total_id").orElse(getRawID()));

        Map<String, Object> hosts =
                KissenCore.getInstance().getImplementation(StorageImplementation.class).getStorage("host");

        String host = String.valueOf(hosts.get(getRawID()));
        Map<String, SavableMap> data = getMeta().getData(this);
        hosts.forEach((key, value) -> handleHost(totalID, host, data, key, value));

        printAltStatus(name, totalID, data);
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

            UUID targetID = UUID.fromString(savableMap.get("total_id").orElse(key));
            if (!targetID.equals(totalID)) {
                try {
                    ((KissenUserImplementation) KissenCore.getInstance()
                            .getImplementation(UserImplementation.class)).rewriteTotalID(targetID, totalID);
                } catch (Exception exception) {
                    KissenCore.getInstance()
                            .getLogger()
                            .error("Could not update the totalId from '{}' to '{}'", targetID, totalID, exception);
                }
            }
        }
    }
}
