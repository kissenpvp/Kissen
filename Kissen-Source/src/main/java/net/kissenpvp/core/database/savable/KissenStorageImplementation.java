/*
 * Copyright 2023 KissenPvP
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
