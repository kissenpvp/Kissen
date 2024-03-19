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

package net.kissenpvp.core.database;

import com.google.gson.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kissenpvp.core.api.database.meta.Meta;
import net.kissenpvp.core.api.database.queryapi.*;
import net.kissenpvp.core.api.database.queryapi.select.QuerySelect;
import net.kissenpvp.core.api.database.queryapi.update.QueryUpdate;
import net.kissenpvp.core.api.database.queryapi.update.Update;
import net.kissenpvp.core.api.database.meta.list.MetaList;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.database.queryapi.KissenQuerySelect;
import net.kissenpvp.core.database.queryapi.KissenQueryUpdate;
import net.kissenpvp.core.database.savable.list.KissenMetaList;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

@AllArgsConstructor
@Getter
public abstract class KissenBaseMeta implements Meta {

    private static final Gson GSON;
    private static final String UNDEFINED = "_UNDEFINED_";
    private static final String ARRAY_PATTERN = "[L%s;";

    static {
        GSON = GsonComponentSerializer.gson().serializer();
    }

    private final String table, totalIDColumn, keyColumn, valueColumn, typeColumn;

    @Override
    public @NotNull Gson getGson() {
        return KissenBaseMeta.GSON;
    }

    @Override
    public boolean metaContains(@NotNull String totalID, @NotNull String key) {
        return !getString(totalID, key).isCompletedExceptionally();
    }

    @Override
    public void setString(@NotNull String key, @Nullable String value) {
        setString(UNDEFINED, key, value);
    }

    @Override
    public void setString(@NotNull String totalID, @NotNull String key, @Nullable String value) {
        setObject(totalID, key, value);
    }

    @Override
    public void setLong(@NotNull String key, long value) {
        setLong(UNDEFINED, key, value);
    }

    @Override
    public void setLong(@NotNull String totalID, @NotNull String key, long value) {
        setObject(totalID, key, value);
    }

    @Override
    public void setDouble(@NotNull String key, double value) {
        setDouble(UNDEFINED, key, value);
    }

    @Override
    public void setDouble(@NotNull String totalID, @NotNull String key, double value) {
        setObject(totalID, key, value);
    }

    @Override
    public void setFloat(@NotNull String key, float value) {
        setFloat(UNDEFINED, key, value);
    }

    @Override
    public void setFloat(@NotNull String totalID, @NotNull String key, float value) {
        setObject(totalID, key, value);
    }

    @Override
    public void setInt(@NotNull String key, int value) {
        setInt(UNDEFINED, key, value);
    }

    @Override
    public void setInt(@NotNull String totalID, @NotNull String key, int value) {
        setObject(totalID, key, value);
    }

    @Override
    public void setShort(@NotNull String key, short value) {
        setShort(UNDEFINED, key, value);
    }

    @Override
    public void setShort(@NotNull String totalID, @NotNull String key, short value) {
        setObject(totalID, key, value);
    }

    @Override
    public void setByte(@NotNull String key, byte value) {
        setByte(UNDEFINED, key, value);
    }

    @Override
    public void setByte(@NotNull String totalID, @NotNull String key, byte value) {
        setObject(totalID, key, value);
    }

    @Override
    public void setBoolean(@NotNull String key, boolean value) {
        setBoolean(UNDEFINED, key, value);
    }

    @Override
    public void setBoolean(@NotNull String totalID, @NotNull String key, boolean value) {
        setObject(totalID, key, value);
    }

    @Override
    public <T> void setObject(@NotNull String key, @Nullable T value) {
        setObject(UNDEFINED, key, value);
    }

    @Override
    public void setCollection(@NotNull String key, @Nullable Collection<?> value) {
        setCollection(UNDEFINED, key, value);
    }

    @Override
    public void setCollection(@NotNull String totalID, @NotNull String key, @Nullable Collection<?> value) {
        setObject(totalID, key, value);
    }

    @Override
    public <T> void setObject(@NotNull String totalID, @NotNull String key, @Nullable T value) {
        setJson(totalID, key, value);
    }

    @Override
    public void delete(@NotNull String totalID, @NotNull String key) {
        setObject(totalID, key, null);
    }

    @Override
    public void delete(@NotNull String key) {
        delete(UNDEFINED, key);
    }

    @Override
    public @NotNull CompletableFuture<String> getString(@NotNull String key) {
        return getString(UNDEFINED, key);
    }

    @Override
    public @NotNull CompletableFuture<String> getString(@NotNull String totalID, @NotNull String key) {
        return getObject(totalID, key, String.class);
    }

    @Override
    public @NotNull CompletableFuture<Long> getLong(@NotNull String key) {
        return getLong(UNDEFINED, key);
    }

    @Override
    public @NotNull CompletableFuture<Long> getLong(@NotNull String totalID, @NotNull String key) {
        return getObject(totalID, key, Long.class);
    }

    @Override
    public @NotNull CompletableFuture<Double> getDouble(@NotNull String key) {
        return getDouble(UNDEFINED, key);
    }

    @Override
    public @NotNull CompletableFuture<Double> getDouble(@NotNull String totalID, @NotNull String key) {
        return getObject(totalID, key, Double.class);
    }

    @Override
    public @NotNull CompletableFuture<Float> getFloat(@NotNull String key) {
        return getFloat(UNDEFINED, key);
    }

    @Override
    public @NotNull CompletableFuture<Float> getFloat(@NotNull String totalID, @NotNull String key) {
        return getObject(totalID, key, Float.class);
    }

    @Override
    public @NotNull CompletableFuture<Integer> getInt(@NotNull String key) {
        return getInt(UNDEFINED, key);
    }

    @Override
    public @NotNull CompletableFuture<Integer> getInt(@NotNull String totalID, @NotNull String key) {
        return getObject(totalID, key, Integer.class);
    }

    @Override
    public @NotNull CompletableFuture<Short> getShort(@NotNull String key) {
        return getShort(UNDEFINED, key);
    }

    @Override
    public @NotNull CompletableFuture<Short> getShort(@NotNull String totalID, @NotNull String key) {
        return getObject(totalID, key, Short.class);
    }

    @Override
    public @NotNull CompletableFuture<Byte> getByte(@NotNull String key) {
        return getByte(UNDEFINED, key);
    }

    @Override
    public @NotNull CompletableFuture<Byte> getByte(@NotNull String totalID, @NotNull String key) {
        return getObject(totalID, key, Byte.class);
    }

    @Override
    public @NotNull CompletableFuture<Boolean> getBoolean(@NotNull String key) {
        return getBoolean(UNDEFINED, key);
    }

    @Override
    public @NotNull CompletableFuture<Boolean> getBoolean(@NotNull String totalID, @NotNull String key) {
        return getObject(totalID, key, Boolean.class);
    }

    @Override
    public @NotNull <T> CompletableFuture<T> getObject(@NotNull String totalID, @NotNull String key, @NotNull Class<T> type) {
        return getJson(totalID, key).thenApply(data -> (T) data);
    }

    @Override
    public @NotNull <T> CompletableFuture<T> getObject(@NotNull String key, @NotNull Class<T> type) {
        return getObject(UNDEFINED, key, type);
    }

    @Override
    public @NotNull QuerySelect.RootQuerySelect select(@NotNull Column... columns) {
        return new KissenQuerySelect.KissenRootQuerySelect(columns) {
            @Override
            public @NotNull CompletableFuture<Object[][]> execute() {
                return KissenBaseMeta.this.execute(getQuery());
            }
        };
    }

    @Override
    public @NotNull QueryUpdate.RootQueryUpdate update(@NotNull Update... update) {
        return new KissenQueryUpdate.KissenRootQueryUpdate(update) {
            @Override
            public @NotNull CompletableFuture<Long> execute() {
                return KissenBaseMeta.this.execute(getQuery());
            }
        };
    }

    @Override
    public @NotNull <T> CompletableFuture<MetaList<T>> getCollection(@NotNull String totalID, @NotNull String key, @NotNull Class<T> type) {
        return getObject(totalID, key, Object[].class).handle((data, throwable) ->
        {
            MetaList<T> metaList = new KissenMetaList<>();
            if(Objects.nonNull(data))
            {
                metaList.addAll(Arrays.stream((T[]) data).toList());
            }
            metaList.setListAction((o1, o2, o3) ->
            {
                try {

                    setCollection(totalID, key, metaList);
                }catch (Exception backendException)
                {
                    backendException.printStackTrace(); //TODO
                }
            });
            return metaList;
        });
    }

    @Override
    public @NotNull <T> CompletableFuture<MetaList<T>> getCollection(@NotNull String key, @NotNull Class<T> type) {
        return getCollection(UNDEFINED, key, type);
    }

    /**
     * Retrieves the column name associated with the specified {@link Column}.
     *
     * <p>The {@code getColumn} method uses a switch statement to determine the column name based on the provided {@link Column}.
     * It returns the corresponding column name for the given enumeration constant, such as total ID, key, or value.</p>
     *
     * @param column the {@link Column} for which to retrieve the column name
     * @return the column name associated with the specified {@link Column}
     */
    public @NotNull String getColumn(@NotNull Column column) {
        return switch (column) {
            case TOTAL_ID -> getTotalIDColumn();
            case KEY -> getKeyColumn();
            case VALUE -> getValueColumn();
        };
    }

    /**
     * Generates a default SELECT query for retrieving JSON data based on the specified total ID and key.
     *
     * <p>The {@code getDefaultQuery} method creates a default {@link QuerySelect} instance with the specified total ID and key,
     * querying the "value" column. This method provides a convenient way to construct a basic SELECT query for fetching
     * JSON data from a database.</p>
     *
     * @param totalID the total ID for the query
     * @param key the key for the query
     * @return a default {@link QuerySelect} for retrieving JSON data
     */
    protected @NotNull QuerySelect getDefaultQuery(@NotNull String totalID, @NotNull String key) {
        return select(Column.VALUE).where(Column.TOTAL_ID, totalID).and(Column.KEY, key);
    }

    /**
     * Asynchronously retrieves JSON data based on the specified total ID and key using the default query.
     *
     * <p>The {@code getJson} method executes the default SELECT query generated by {@link #getDefaultQuery(String, String)},
     * then processes the result by extracting the value from the data array. If the result is empty, it throws a
     * {@link NullPointerException}. The method uses the {@link #logExceptions()} handler to log any exceptions that may occur during execution.</p>
     *
     * @param totalID the total ID for the query
     * @param key the key for the query
     * @return a CompletableFuture containing the retrieved JSON data
     * @throws NullPointerException if the query result is empty
     */
    protected @NotNull CompletableFuture<?> getJson(@NotNull String totalID, @NotNull String key) {
        return getDefaultQuery(totalID, key).execute().thenApply(data -> {
            if (data.length == 0 || data[0].length == 0) {
                throw new NullPointerException();
            }

            return data[0][0];
        }).handle(logExceptions());
    }

    /**
     * Deserializes the specified JSON string into an object of the corresponding class.
     *
     * <p>The {@code deserialize} method parses the input JSON string and extracts the class type
     * and object data. It then uses reflection to load the class and deserialize the object using GSON.</p>
     *
     * @param json the JSON string to deserialize
     * @param <T> the type of the deserialized object
     * @return an object deserialized from the JSON string
     * @throws ClassNotFoundException if the class specified in the JSON string cannot be found
     */
    public <T> @NotNull T deserialize(@NotNull Class<?> type, @NotNull String json) throws ClassNotFoundException {
        return (T) getGson().fromJson(json, type);
    }

    public @Nullable String[] serialize(@Nullable Object object) {
        if (object == null || (object instanceof Collection<?> collection && collection.isEmpty())) {
            return null;
        }

        String clazz = object.getClass().getName();

        if (object instanceof Collection<?> collection) {
            String typeName = collection.stream().findFirst().orElseThrow().getClass().getName();
            clazz = ARRAY_PATTERN.formatted(typeName);
            return new String[] {clazz, getGson().toJson(collection.toArray(Object[]::new))};
        }

        return new String[] {clazz, getGson().toJson(object)};
    }

    /**
     * Sets the specified key-value pair in the JSON identified by the given total ID.
     *
     * <p>The {@code setJson} method sets the specified {@code key} and its corresponding {@code value} in the JSON structure
     * identified by the given {@code totalID}. This method is intended for manipulating JSON data in a storage or database system.
     * The implementation of this method should ensure that the changes are applied to the appropriate JSON structure.</p>
     *
     * @param totalID the total ID identifying the JSON structure
     * @param key     the key to be set in the JSON structure
     * @param object
     * @throws NullPointerException if {@code totalID} or {@code key} is {@code null}
     */
    protected abstract void setJson(@NotNull String totalID, @NotNull String key, @Nullable Object object);

    /**
     * Executes the specified SELECT query and returns a CompletableFuture containing the query result.
     *
     * <p>The {@code execute} method for SELECT queries asynchronously performs the query specified by the given {@link QuerySelect}
     * and returns a CompletableFuture containing the result as an array of objects. The implementation of this method should
     * execute the query and resolve the CompletableFuture with the result.</p>
     *
     * @param querySelect the SELECT query to be executed
     * @return a CompletableFuture containing the result as an array of objects
     * @throws NullPointerException if {@code querySelect} is {@code null}
     */
    protected abstract @NotNull CompletableFuture<Object[][]> execute(@NotNull QuerySelect querySelect);

    /**
     * Executes the specified UPDATE query and returns a CompletableFuture containing the number of affected rows.
     *
     * <p>The {@code execute} method for UPDATE queries asynchronously performs the update specified by the given {@link QueryUpdate}
     * and returns a CompletableFuture containing the number of affected rows. The implementation of this method should execute
     * the update query and resolve the CompletableFuture with the count of affected rows.</p>
     *
     * @param queryUpdate the UPDATE query to be executed
     * @return a CompletableFuture containing the number of affected rows
     * @throws NullPointerException if {@code queryUpdate} is {@code null}
     */
    protected abstract @NotNull CompletableFuture<@NotNull Long> execute(@NotNull QueryUpdate queryUpdate);

    /**
     * Returns a {@link BiFunction} that logs exceptions caught during data fetching from the MySQL database.
     *
     * <p>The returned {@link BiFunction} is designed to be used in conjunction with asynchronous operations,
     * such as CompletableFuture's exceptionally method. It logs any caught exceptions with a debug level message
     * and returns the original object if no exception occurred.</p>
     *
     * @return a {@link BiFunction} logging exceptions and returning the original object
     */
    protected static <T> @NotNull BiFunction<T, Throwable, T> logExceptions() {
        return (object, throwable) ->
        {
            if (Objects.isNull(throwable)) {
                return object;
            }

            String message = "There was an exception caught when fetching data from the database.";
            KissenCore.getInstance().getLogger().debug(message, throwable);
            return object;
        };
    }
}
