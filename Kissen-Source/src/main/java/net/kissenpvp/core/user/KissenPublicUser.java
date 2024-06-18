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

import lombok.extern.slf4j.Slf4j;
import net.kissenpvp.core.api.database.meta.BackendException;
import net.kissenpvp.core.api.database.meta.Meta;
import net.kissenpvp.core.api.database.meta.list.MetaList;
import net.kissenpvp.core.api.database.queryapi.Column;
import net.kissenpvp.core.api.database.queryapi.select.QuerySelect;
import net.kissenpvp.core.api.database.savable.Savable;
import net.kissenpvp.core.api.database.savable.SavableMap;
import net.kissenpvp.core.api.message.localization.LocalizationImplementation;
import net.kissenpvp.core.api.permission.AbstractPermission;
import net.kissenpvp.core.api.user.User;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.database.savable.KissenSavableMap;
import net.kissenpvp.core.user.rank.PlayerRankNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j(topic = "Kissen")
public abstract class KissenPublicUser<T extends AbstractPermission> extends KissenUser<T> implements User {

    /**
     * Creates a public user which is based on the parameter given just an abstract user or a specific player.
     *
     * @param name of the player this class should represent, null if it's just an abstraction.
     * @param uuid of the player this class should represent, null if it's just an abstraction.
     */
    public KissenPublicUser(@Nullable UUID uuid, @Nullable String name) {
        super(uuid, name);

        if (name != null && uuid != null) {
            String currentName = getRepository().getNotNull("name", String.class);
            if (!Objects.equals(currentName, name)) {
                updateName(name, currentName);
            }
        }
    }

    public @NotNull @Unmodifiable Set<UUID> getAltAccounts() {
        Meta meta = ((KissenSavableMap) getRepository()).getMeta();
        QuerySelect query = meta.select(Column.TOTAL_ID).where(Column.VALUE, getTotalId()).andExact(Column.KEY, "total_id");
        return query.execute().thenApply(data -> Arrays.stream(data).map(columns -> {
            String uuid = String.valueOf(columns[0]).substring(getSaveID().length());
            return UUID.fromString(uuid);
        }).collect(Collectors.toUnmodifiableSet())).join();
    }

    public @NotNull MetaList<PlayerRankNode> getRankNodes() {
        return getRepository().getListNotNull("rank_list", PlayerRankNode.class);
    }

    public void updateLocale(@NotNull String locale) {
        LocalizationImplementation locales = KissenCore.getInstance().getImplementation(LocalizationImplementation.class);
        if (Objects.equals(locale, locales.getDefaultLocale().toString())) {
            getRepository().delete("locale");
            return;
        }
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
        log.debug(message, getRawID(), getRepository().getNotNull("name", String.class), name);
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
    public @NotNull Savable<UUID> setup(@NotNull UUID id, @Nullable Map<String, Object> initialData) {
        Savable<UUID> value = super.setup(id, initialData);
        String name = getRepository().getNotNull("name", String.class);
        getImplementation().cacheProfile(new UserInfoNode(id, name));
        return value;
    }

    @Override
    public final @NotNull String getSaveID() {
        return getImplementation().getUserSaveID();
    }
}
