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

package net.kissenpvp.core.database.savable;

import lombok.AccessLevel;
import lombok.Getter;
import net.kissenpvp.core.api.base.plugin.KissenPlugin;
import net.kissenpvp.core.api.database.StorageImplementation;
import net.kissenpvp.core.api.database.meta.BackendException;
import net.kissenpvp.core.api.database.meta.Meta;
import net.kissenpvp.core.api.database.savable.Savable;
import net.kissenpvp.core.api.database.savable.SavableInitializeException;
import net.kissenpvp.core.api.database.savable.SavableMap;
import net.kissenpvp.core.api.networking.socket.DataPackage;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.database.savable.event.SavableDeletedEvent;
import net.kissenpvp.core.event.EventImplementation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;


public abstract class KissenSavable<T> extends HashMap<KissenPlugin, SavableMap> implements Savable<T> {

    private T id;
    @Getter(AccessLevel.PROTECTED) private SavableMap repository;

    @Override
    public @NotNull Map<String, Object> getStorage() {
        return KissenCore.getInstance().getImplementation(StorageImplementation.class).getStorage(getDatabaseID());
    }

    @Override
    public String[] getKeys() {
        return new String[0];
    }

    @Override
    public @NotNull String getSaveID() {
        return getClass().getSimpleName();
    }

    @Override
    public @NotNull SavableMap getRepository(@NotNull KissenPlugin plugin) {
        Meta meta = getTable().registerMeta(plugin);
        return super.computeIfAbsent(plugin, (key) -> {
            Map<String, Object> fetched = meta.getData(getDatabaseID()).join();
            return new KissenSavableMap(getDatabaseID(), meta, fetched);
        });
    }

    @Override
    public @NotNull Savable<T> setup(@NotNull T id) throws SavableInitializeException {
        return setup(id, null);
    }

    @Override
    public @NotNull Savable<T> setup(@NotNull T id, @Nullable Map<String, Object> initialData) {
        this.clear(); // clear previous object data
        this.id = id;

        repository = createRepository(initialData);
        SavableMap repo = getRepository();
        if (this.getKeys().length > 0) {
            if (!Stream.of(this.getKeys()).allMatch(repo::containsKey)) {
                throw new SavableInitializeException();
            }
        }

        if (!repo.containsKey("id")) {
            repo.put("id", getRawID());
            if (repo instanceof KissenSavableMap internal) {
                internal.getMeta().addMap(getDatabaseID(), repo);
            }
        }

        return this;
    }

    @Override
    public @NotNull String getDatabaseID() {
        return this.getSaveID() + this.getRawID();
    }

    protected abstract @NotNull SavableMap createRepository(@Nullable Map<String, Object> data);

    @Override
    public @NotNull T getRawID() {
        return id;
    }

    @Override
    public int delete() {
        /*sendData(KissenCore.getInstance() //TODO
                .getImplementation(NetworkImplementation.class)
                .createPackage("savable_delete", getSerializableSavableHandler()));*/
        return softDelete();
    }

    @Override
    public boolean equals(Object o) {

        if (this==o) {
            return true;
        }
        if (!(o instanceof KissenSavable<?> that)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        return getDatabaseID().equals(that.getDatabaseID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDatabaseID());
    }

    /**
     * Deletes the object without sending a {@link DataPackage} to the other servers.
     */
    public int softDelete() throws BackendException {
        int count = size();
        clear();
        //this.getMeta().purge(this.getDatabaseID());
        KissenCore.getInstance().getImplementation(EventImplementation.class).call(new SavableDeletedEvent(this));
        KissenCore.getInstance().getImplementation(StorageImplementation.class).dropStorage(getDatabaseID());
        return count;
    }

    /**
     * Sends a {@link DataPackage} to the servers which needs to be informed of changes in the data.
     *
     * @param dataPackage the changes.
     */
    public abstract void sendData(@NotNull DataPackage dataPackage);

    public abstract SerializableSavableHandler getSerializableSavableHandler();
}
