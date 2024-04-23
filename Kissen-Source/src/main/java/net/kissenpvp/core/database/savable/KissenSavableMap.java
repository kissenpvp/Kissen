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
import net.kissenpvp.core.api.database.meta.list.MetaList;
import net.kissenpvp.core.api.database.savable.SavableMap;
import net.kissenpvp.core.database.savable.list.KissenMetaList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;


/**
 * Represents a KissenSavableMap, which extends {@link HashMap} and implements {@link SavableMap}.
 *
 * <p>The KissenSavableMap class provides a convenient way to manage key-value pairs and supports serialization and persistence through the {@link SavableMap} interface.
 * It is designed to store its values in a database, making it suitable for scenarios where persistent storage is required.</p>
 *
 * <p>The class includes standard JavaBean annotations such as {@link Setter}, {@link Getter}, and {@link NoArgsConstructor} for enhanced functionality.</p>
 *
 * <p>This class supports the concept of a unique identifier ({@code id}) associated with each instance, allowing for easy identification and retrieval from the database.</p>
 *
 * <p>Example usage:</p>
 *
 * <pre>
 * {@code
 * KissenSavableMap savableMap = new KissenSavableMap();
 * savableMap.setId("exampleId");
 * savableMap.put("key1", "value1"); // not stored in database
 * savableMap.set("key2", 42); // stored in database
 * }
 * </pre>
 *
 * @see HashMap
 * @see SavableMap
 * @see Setter
 * @see Getter
 * @see NoArgsConstructor
 */
@Setter
@Getter
public class KissenSavableMap extends HashMap<String, Object> implements SavableMap {

    private final String id;
    private final ObjectMeta meta;
    private final boolean internal;


    /**
     * Constructs a new KissenSavableMap with the specified ID and ObjectMeta.
     *
     * <p>The constructor creates a new instance of KissenSavableMap with an empty data map, setting the provided ID and ObjectMeta.</p>
     *
     * @param id   the ID to associate with the new KissenSavableMap
     * @param meta the ObjectMeta to associate with the new KissenSavableMap
     */
    public KissenSavableMap(@NotNull String id, @NotNull ObjectMeta meta, @NotNull Map<String, Object> copy) {
        super.putAll(copy);

        this.id = id;
        this.meta = meta;
        this.internal = false;
    }

    @Override
    public <T> @Nullable Object set(@NotNull String key, @Nullable T value) {

        Object returnValue;
        if (Objects.isNull(value)) {
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
    public <T> @NotNull Optional<MetaList<T>> getList(@NotNull String key, @NotNull Class<T> type) {
        if (containsKey(key)) {
            return Optional.of(getList(key));
        }
        return Optional.empty();
    }

    @Override
    public <T> @NotNull MetaList<T> getListNotNull(@NotNull String key, @NotNull Class<T> type) {
        return getList(key, type).orElseGet(() -> getList(key));
    }

    @Override
    public <T> @Nullable Object putList(@NotNull String key, @Nullable Collection<T> value) {

        Object current = super.get(key);
        if (value==null) {
            remove(key);
            return current;
        }

        put(key, value);
        return current;
    }

    @Override
    public @Nullable @Unmodifiable <T> List<?> putListValue(@NotNull String key, @NotNull T value) {
        List<?> current = (List<?>) super.get(key);
        getList(key, value.getClass()).ifPresentOrElse(list -> {
            MetaList<T> casted = (MetaList<T>) list;
            casted.add(value);
        }, () -> putList(key, Collections.singletonList(value)));
        return current==null ? null:List.copyOf(current);
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
        getMeta().setCollection(getId(), key, value);
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
    public @Nullable @Unmodifiable <T> List<?> setListValue(@NotNull String key, @NotNull T value) {
        if (containsList(key)) {
            return putListValue(key, value);
        }
        return (List<?>) setListIfAbsent(key, Collections.singletonList(value));
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
    public boolean equals(Object o) {
        if (this==o) return true;
        if (o==null || getClass()!=o.getClass()) return false;
        if (!super.equals(o)) return false;
        KissenSavableMap that = (KissenSavableMap) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getId());
    }

    /**
     * Retrieves a {@link MetaList} associated with the specified key from this map.
     *
     * <p>The {@code getList} method retrieves a {@link MetaList} associated with the specified key from this map.
     * It creates a new instance of {@link KissenMetaList} and populates it with elements from the stored object,
     * if the object associated with the key is not null and is of a valid type (array or Collection).</p>
     *
     * <p>If the stored object is an array, it is converted to a {@link List} using the {@link Arrays#stream(Object[])} method.</p>
     * <p>If the stored object is not a {@link Collection}, an {@link IllegalArgumentException} is thrown.</p>
     * <p>The created {@link MetaList} has a list action set, which will update the stored value in the database when the list is modified.</p>
     *
     * @param key the key associated with the desired {@link MetaList}
     * @param <T> the type of elements in the {@link MetaList}
     * @return a {@link MetaList} associated with the specified key, or an empty list if the key is not present
     * @throws IllegalArgumentException if the stored object is not a {@link Collection}
     * @see MetaList
     * @see KissenMetaList
     * @see #setList(String, Collection)
     */
    private <T> @NotNull MetaList<T> getList(@NotNull String key) {
        MetaList<T> metaList = new KissenMetaList<>();
        Object obj = super.get(key);
        if (obj!=null) {
            if (obj.getClass().isArray()) {
                obj = Arrays.stream(((Object[]) obj)).toList();
            }

            if (!(obj instanceof Collection<?> collection)) {
                throw new IllegalArgumentException(); //Type not matching todo
            }
            metaList.addAll((Collection<? extends T>) collection);
        }

        metaList.setListAction((listExecution, before, after) -> setList(key, metaList));
        return metaList;
    }
}
