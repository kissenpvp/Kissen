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

import net.kissenpvp.core.api.database.StorageImplementation;
import net.kissenpvp.core.api.database.meta.BackendException;
import net.kissenpvp.core.api.database.savable.Savable;
import net.kissenpvp.core.api.database.savable.SavableInitializeException;
import net.kissenpvp.core.api.database.savable.SavableMap;
import net.kissenpvp.core.api.database.savable.list.SavableList;
import net.kissenpvp.core.api.event.EventImplementation;
import net.kissenpvp.core.api.networking.socket.DataPackage;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.database.savable.event.SavableDeletedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;


public abstract class KissenSavable extends KissenSavableMap implements Savable {
    /**
     * This class is setting the data about an object.
     */
    public KissenSavable() {
        super();
    }

    @Override
    public @NotNull Map<String, Object> getStorage() {
        return KissenCore.getInstance().getImplementation(StorageImplementation.class).getStorage(getId());
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
    public void setup(@NotNull String id, @Nullable Map<@NotNull String, @NotNull String> meta) throws SavableInitializeException, BackendException {
        super.setId(id);

        if (meta == null) {
            meta = getMeta().getData(this.getId())
                    .map(SavableMap::serialize)
                    .orElse(new KissenSavableMap(this.getId(), getMeta()));

        }

        if (this.getKeys().length > 0) {
            if (!Stream.of(this.getKeys()).allMatch(meta::containsKey)) {
                throw new SavableInitializeException();
            }
        }

        boolean justCreated = false;
        if (!meta.containsKey("id")) {
            Map<String, String> newMeta = new HashMap<>(meta);
            newMeta.put("id", this.getRawID());
            getMeta().add(this.getId(), newMeta);
            meta = newMeta;
            justCreated = true;
        }
        super.setId(meta.get("id")); //correct capitalization.
        setData(meta, getRawID());

        if (justCreated) {
            //TODO
            //sendData(KissenCore.getInstance().getImplementation(NetworkImplementation.class).createPackage("create_savable", getSerializableSavableHandler(), getRawID(), new HashMap<>(meta)));
        }
    }

    @Override
    public @NotNull String getId() {
        return this.getSaveID() + super.getId();
    }

    @Override
    public void setId(String id) {
    }

    @Override
    public @NotNull String getRawID() {
        return super.getId();
    }

    @Override
    public void set(@NotNull String key, @Nullable String value) {
        super.set(key, value);
        //TODO
        //sendData(KissenCore.getInstance().getImplementation(NetworkImplementation.class).createPackage("meta_update_entry", getSerializableSavableHandler(), key, value));
    }

    @Override
    public @NotNull SavableList setList(@NotNull String key, @Nullable List<String> value) {
        SavableList savableList = super.setList(key, value);
        /*sendData(KissenCore.getInstance().getImplementation(NetworkImplementation.class).createPackage(
                "meta_update_list", getSerializableSavableHandler(), key, value == null ? null :
                        new ArrayList<>(value)));*/
        return savableList;
    }


    @Override
    public int delete() throws BackendException {
        int rows = softDelete();
        /*sendData(KissenCore.getInstance()
                .getImplementation(NetworkImplementation.class)
                .createPackage("savable_delete", getSerializableSavableHandler()));*/
        return rows;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KissenSavable that)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + getId().hashCode();
        return result;
    }

    /**
     * Deletes the object without sending a {@link DataPackage} to the other servers.
     */
    public int softDelete() throws BackendException {
        AtomicInteger count = new AtomicInteger(size());
        getStringArrayListMap().forEach((key, value) -> count.addAndGet(value.size()));
        this.getMeta().purge(this.getId());
        KissenCore.getInstance().getImplementation(EventImplementation.class).call(new SavableDeletedEvent(this));
        KissenCore.getInstance().getImplementation(StorageImplementation.class).dropStorage(getId());
        return count.get();
    }

    /**
     * Sends a {@link DataPackage} to the servers which needs to be informed of changes in the data.
     *
     * @param dataPackage the changes.
     */
    public abstract void sendData(@NotNull DataPackage dataPackage);

    public abstract SerializableSavableHandler getSerializableSavableHandler();
}
