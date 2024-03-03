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

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.kissenpvp.core.api.database.meta.ObjectMeta;
import net.kissenpvp.core.api.database.savable.SavableMap;
import net.kissenpvp.core.api.database.savable.list.KissenList;
import net.kissenpvp.core.api.database.savable.list.ListAction;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.database.savable.list.KissenKissenList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;


@Setter
@Getter
@NoArgsConstructor
public class KissenSavableMap extends HashMap<String, Object> implements SavableMap {

    private String id;

    /**
     * This constructor defines the meta in which the data is saved.
     * And the sender behind which the individual key is located.
     *
     * @param savableMap to create a copy.
     */
    public KissenSavableMap(@NotNull KissenSavableMap savableMap) {
        setData(savableMap, savableMap instanceof KissenSavable kissenSavable ? kissenSavable.getDatabaseID() : savableMap.getId());
    }

    /**
     * This constructor defines the meta in which the data is saved.
     * And the sender behind which the individual key is located.
     *
     * @param id   The sender of the savable object.
     * @param meta The meta in which everything should be saved.
     */
    public KissenSavableMap(@NotNull String id, @NotNull ObjectMeta meta) {
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
    public KissenSavableMap(@NotNull Map<String, Object> data, @NotNull String id, @NotNull ObjectMeta meta) {
        setData(data, id);
    }

    /**
     * Resets the data of the options.
     *
     * @param data the new data.
     * @param id   the sender of the object.
     */
    protected void setData(@NotNull Map<String, Object> data, @NotNull String id) {
        this.clear();
        super.putAll(data);

        this.id = id;
    }

    @Override
    public void putAll(@NotNull SavableMap savableMap) {
        super.putAll(savableMap);
    }

    @Override
    public <T> @Nullable Object set(@NotNull String key, @Nullable T value) {

        Object returnValue;
        if (value == null) {
            if (Objects.nonNull(returnValue = remove(key))) {
                getMeta().delete(key);
            }
            return returnValue;
        }

        returnValue = put(key, value);
        if (!Objects.equals(returnValue, value)) {
            getMeta().setObject(getId(), key, value);
        }
        return returnValue;
    }

    @Override
    public <T> void setIfAbsent(@NotNull String key, @NotNull T value) {
        if (!containsKey(key)) {
            set(key, value);
        }
    }

    @Override
    public @Nullable Object delete(@NotNull String key) {
        return set(key, null);
    }

    @Override
    public @NotNull <T> Optional<T> get(@NotNull String key, @NotNull Class<T> clazz) {
        return Optional.ofNullable(super.get(key)).map(data -> (T) data);
    }

    @Override
    public <T> @NotNull T getNotNull(@NotNull String key, @NotNull Class<T> type) throws NoSuchFieldError {
        return get(key, type).orElseThrow();
    }

    @Override
    public boolean containsList(@NotNull String key) {
        return getList(key, List.class).isPresent();
    }

    @Override
    public <T> @NotNull Optional<KissenList<T>> getList(@NotNull String key, @NotNull Class<T> type) {
        if (containsKey(key)) {
            try {
                Object obj = super.get(key);
                if (obj != null && obj.getClass().isArray()) {
                    obj = Arrays.stream(((Object[]) obj)).toList();
                }

                if (!(obj instanceof Collection<?> collection)) {
                    throw new IllegalArgumentException(); //Type not matching todo
                }
                KissenList<T> kissenList = new KissenKissenList<>();
                kissenList.addAll((Collection<? extends T>) collection);

                kissenList.setListAction((ListAction) (listExecution, values) -> setList(key, kissenList));
                return Optional.of(kissenList);
            } catch (ClassCastException ignored) {
            }
        }
        return Optional.empty();
    }

    @Override
    public <T> @NotNull KissenList<T> getListNotNull(@NotNull String key, @NotNull Class<T> type) {
        return getList(key, type).orElseGet(() -> {
            KissenList<T> kissenList = new KissenKissenList<>();
            kissenList.setListAction(((listExecution, values) -> setList(key, kissenList)));
            return kissenList;
        });
    }

    @Override
    public <T> @Nullable Object putList(@NotNull String key, @Nullable Collection<T> value) {

        Object current =  super.get(key);
        if (value == null) {
            remove(key);
            return current;
        }

        put(key, value);
        return current;
    }

    @Override
    public @Nullable @Unmodifiable <T> List<T> putListValue(@NotNull String key, @NotNull T value) {
        List<T> current = (List<T>) super.get(key);
        getList(key, value.getClass()).ifPresentOrElse(list -> {
            KissenList<T> casted = (KissenList<T>) list;
            casted.add(value);
        }, () -> putList(key, Collections.singletonList(value)));
        return List.copyOf(current);
    }

    @Override
    public <T> @Nullable @Unmodifiable Object putListIfAbsent(@NotNull String key, @NotNull Collection<T> value) {
        if (!containsList(key)) {
            return putList(key, value);
        }
        return List.copyOf(getListNotNull(key, value.getClass()));
    }

    @Override
    public <T> @Nullable Object setList(@NotNull String key, @Nullable Collection<T> value) {
        Object list = putList(key, value);
        if (!Objects.equals(list, value)) {
            getMeta().setCollection(getId(), key, value == null ? null : List.copyOf(value));
        }
        return list;
    }

    @Override
    public <T> @Nullable Object setListIfAbsent(@NotNull String key, @NotNull Collection<T> value) {
        if (!containsList(key)) {
            return setList(key, value);
        }
        return super.get(key);
    }

    @Override
    public <T> @Nullable @Unmodifiable List<T> setListValue(@NotNull String key, @NotNull T value) {
        if (containsList(key)) {
            return putListValue(key, value);
        }
        return (List<T>) setListIfAbsent(key, Collections.singletonList(value));
    }

    @Override
    public boolean removeList(@NotNull String key) {
        return Objects.nonNull(putList(key, null));
    }

    @Override
    public <T> boolean removeListValue(@NotNull String key, @NotNull T value) {
        return getList(key, value.getClass()).map(content -> content.remove(value)).orElse(false);
    }

    @Override
    public boolean deleteList(@NotNull String key) {
        return Objects.nonNull(setList(key, null));
    }

    @Override
    public <T> boolean deleteListValue(@NotNull String key, @NotNull T value) {
        return getList(key, value.getClass()).map(data -> data.remove(value)).orElse(false);
    }

    @Override
    public @NotNull SavableMap serializeSavable() {
        return new KissenSavableMap(this);
    }

    public @NotNull ObjectMeta getMeta() {
        return KissenCore.getInstance().getPublicMeta(); //todo choose
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        KissenSavableMap that = (KissenSavableMap) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getId());
    }
}
