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

import lombok.SneakyThrows;
import net.kissenpvp.core.api.database.StorageImplementation;
import net.kissenpvp.core.database.file.KissenObjectFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class KissenStorageImplementation implements StorageImplementation {

    private final Set<StorageContainer> userStorageContainer;
    private final KissenObjectFile kissenObjectFile;

    @SneakyThrows
    public KissenStorageImplementation() {
        this.userStorageContainer = new HashSet<>();
        kissenObjectFile = new KissenObjectFile(new File(".objcache"));
    }

    @Override
    public @NotNull Map<String, Object> getStorage(@NotNull String id) {
        StorageContainer storageContainer =
                userStorageContainer.stream().filter(storage -> storage.id().equals(id)).findFirst().orElse(null);
        if (storageContainer == null) {
            storageContainer = new StorageContainer(id, new HashMap<>());
            userStorageContainer.add(storageContainer);
        }
        return storageContainer.storage();
    }

    @Override
    public void dropStorage(@NotNull String id) {
        userStorageContainer.removeIf(storageContainer -> storageContainer.id().equals(id));
    }

    @Override
    public boolean containsCacheObject(@NotNull String key) {
        return kissenObjectFile.getStringSerializableMap().containsKey(key);
    }

    @Override
    public synchronized boolean deleteCacheObject(@NotNull String key) throws IOException {
        boolean contains = kissenObjectFile.getStringSerializableMap().containsKey(key);
        writeCacheObject(key, null);
        return contains == kissenObjectFile.getStringSerializableMap().containsKey(key);
    }

    @Override
    public synchronized <T extends Serializable> void writeCacheObject(@NotNull String key, @Nullable T serializable) throws IllegalArgumentException, IOException {
        try {
            kissenObjectFile.getStringSerializableMap().remove(key);
            if (serializable != null) {
                kissenObjectFile.getStringSerializableMap().put(key, serializable);
            }
            kissenObjectFile.write();
        } catch (NotSerializableException notSerializableException) {
            if (serializable != null) {
                throw new IllegalArgumentException(serializable.getClass().getName() + " is not serializable.", notSerializableException);
            }
        }
    }

    @Override
    public synchronized <T extends Serializable> @NotNull T readCacheObject(@NotNull String key, @NotNull Class<T> clazz) throws ClassCastException, NullPointerException {
        if (!kissenObjectFile.getStringSerializableMap().containsKey(key)) {
            throw new NullPointerException("The requested cache object with the key " + key + " did not exits.");
        }
        return (T) kissenObjectFile.getStringSerializableMap().get(key);
    }

    /**
     * @param id      the sender of the object which holds the data.
     * @param storage the data behind the storage.
     * @author groldi
     * @since 1.0.0-SNAPSHOT
     */
    private record StorageContainer(String id, Map<String, Object> storage) {
    }
}
