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

import lombok.Getter;
import lombok.Setter;
import net.kissenpvp.core.api.database.meta.BackendException;
import net.kissenpvp.core.api.database.meta.ObjectMeta;
import net.kissenpvp.core.api.database.savable.SavableMap;
import net.kissenpvp.core.api.database.savable.list.ListAction;
import net.kissenpvp.core.api.database.savable.list.SavableList;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.database.savable.list.KissenSavableList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;


public class KissenSavableMap extends HashMap<String, String> implements SavableMap {

    @Getter()
    private Map<String, ArrayList<String>> stringArrayListMap;
    @Getter
    @Setter
    private String id;

    public KissenSavableMap() {
        this(null, null);
    }

    /**
     * This constructor defines the meta in which the data is saved.
     * And the sender behind which the individual key is located.
     *
     * @param savableMap to create a copy.
     */
    public KissenSavableMap(KissenSavableMap savableMap) {
        setData(savableMap, savableMap instanceof KissenSavable kissenSavable ? kissenSavable.getRawID() : savableMap.getId());
    }

    /**
     * This constructor defines the meta in which the data is saved.
     * And the sender behind which the individual key is located.
     *
     * @param id   The sender of the savable object.
     * @param meta The meta in which everything should be saved.
     */
    public KissenSavableMap(String id, ObjectMeta meta) {
        this(new HashMap<>(), id, meta);
    }

    /**
     * This constructor defines the meta in which the data is saved.
     * And the ID behind which the individual key is located.
     *
     * @param data An already existing value which is included in this list.
     * @param id   The ID of the savable object.
     * @param meta The meta in which everything should be saved.
     */
    public KissenSavableMap(Map<String, String> data, String id, ObjectMeta meta) {
        setData(data, id);
    }

    /**
     * Resets the data of the options.
     *
     * @param data the new data.
     * @param id   the sender of the object.
     */
    protected void setData(Map<String, String> data, String id) {
        this.clear();
        super.putAll(data);

        this.id = id;
        stringArrayListMap = new HashMap<>();
        if (data instanceof KissenSavableMap kissenSavableMap) {
            stringArrayListMap.putAll(kissenSavableMap.stringArrayListMap);
        }
    }

    @Override
    public void putAll(@NotNull SavableMap savableMap) {
        savableMap.keySet().forEach(super::remove);
        super.putAll(savableMap);
        if (savableMap instanceof KissenSavableMap kissenSavableMap) {
            kissenSavableMap.stringArrayListMap.keySet().forEach(stringArrayListMap::remove);
            stringArrayListMap.putAll(kissenSavableMap.stringArrayListMap);
        }
    }

    @Override
    public void set(@NotNull String key, @Nullable String value) {
        if (get(key).isEmpty() || !Objects.equals(get(key).get(), value)) {
            try {
                getMeta().setString(getId(), key, value);
                if (value == null) {
                    remove(key);
                    return;
                }
                put(key, value);
            } catch (BackendException backendException) {
                KissenCore.getInstance()
                        .getLogger()
                        .error("An error occurred when updating a value in the savable entry {}.", getId(), backendException);
            }
        }
    }

    @Override
    public void setIfAbsent(@NotNull String key, @NotNull String value) {
        if (!containsKey(key)) {
            set(key, value);
        }
    }

    @Override
    public void delete(@NotNull String key) {
        set(key, null);
    }

    @Override
    public @NotNull Optional<@Nullable String> get(@NotNull String key) {
        return Optional.ofNullable(super.get(key));
    }

    @Override
    public @NotNull String getNotNull(@NotNull String key) {
        Optional<String> value = get(key);
        if (value.isEmpty()) {
            throw new NullPointerException();
        }
        return value.get();
    }

    @Override
    public boolean containsList(@NotNull String key) {
        return getList(key).isPresent();
    }

    @Override
    public @NotNull Optional<@Nullable SavableList> getList(@NotNull String key) {
        if (this.stringArrayListMap.containsKey("_" + key)) {
            SavableList kissenList = new KissenSavableList();
            kissenList.addAll(stringArrayListMap.get("_" + key));
            kissenList.setListAction((ListAction) (listExecution, values) -> setList(key, kissenList));
            return Optional.of(kissenList);
        }
        return Optional.empty();
    }

    @Override
    public @NotNull SavableList getListNotNull(@NotNull String key) {
        SavableList kissenList = new KissenSavableList();
        if (containsList(key)) {
            kissenList.addAll(stringArrayListMap.get("_" + key));
        }

        kissenList.setListAction((ListAction) (listExecution, values) -> setList(key, kissenList));
        return kissenList;
    }

    @Override
    public @NotNull SavableList putList(@NotNull String key, @Nullable List<String> value) {
        stringArrayListMap.remove("_" + key);
        if (value != null) {
            stringArrayListMap.put("_" + key, new ArrayList<>(value));
        }
        return getListNotNull(key);
    }

    @Override
    public @NotNull SavableList putListValue(@NotNull String key, @NotNull String value) {
        if (getList(key).isPresent()) {
            List<String> list = stringArrayListMap.get("_" + key);
            list.add(value);
            return putList(key, list);
        }
        return putList(key, Collections.singletonList(value));
    }

    @Override
    public @NotNull SavableList putListIfAbsent(@NotNull String key, @NotNull List<String> value) {
        if (getList(key).isEmpty()) {
            putList(key, value);
        }
        return getListNotNull(key);
    }

    @Override
    public @NotNull SavableList setList(@NotNull String key, @Nullable List<String> value) {
        try {
            if (value == null) {
                if (getList(key).isPresent()) {
                    removeList(key);
                    getMeta().delete(this.getId(), "_" + key);
                }
            } else {
                putList(key, value);
                getMeta().setStringList(getId(), "_" + key, value);
            }
            return getListNotNull(key);
        } catch (BackendException backendException) {
            KissenCore.getInstance()
                    .getLogger()
                    .error("An error occurred when updating a value in the savable entry {}.", getId(), backendException);
            throw new IllegalStateException(backendException);
        }
    }

    @Override
    public @NotNull SavableList setListIfAbsent(@NotNull String key, @NotNull List<String> value) {
        return containsList(key) ? setList(key, value) : getListNotNull(key);
    }

    @Override
    public @NotNull SavableList setListValue(@NotNull String key, @NotNull String value) {
        if (containsList(key)) {
            return putListValue(key, value);
        }
        return setListIfAbsent(key, Collections.singletonList(value));
    }

    @Override
    public void removeList(@NotNull String key) {
        putList(key, null);
    }

    @Override
    public void removeListValue(@NotNull String key, @NotNull String value) {
        if (containsList(key)) {
            this.stringArrayListMap.get("_" + key).remove(value);
        }
    }

    @Override
    public void deleteList(@NotNull String key) {
        setList(key, null);
    }

    @Override
    public void deleteListValue(@NotNull String key, @NotNull String value) {
        setListIfAbsent(key, new ArrayList<>()).remove(value);
    }

    @Override
    public @NotNull SavableMap serialize() {
        return new KissenSavableMap(this);
    }

    public @NotNull ObjectMeta getMeta() {
        return KissenCore.getInstance().getPublicMeta();
    }
}
