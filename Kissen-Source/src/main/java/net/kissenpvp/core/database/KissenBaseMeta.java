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
import net.kissenpvp.core.api.database.meta.BackendException;
import net.kissenpvp.core.api.database.meta.Meta;
import net.kissenpvp.core.api.database.queryapi.*;
import net.kissenpvp.core.api.database.queryapi.select.QuerySelect;
import net.kissenpvp.core.api.database.queryapi.update.QueryUpdate;
import net.kissenpvp.core.api.database.queryapi.update.QueryUpdateDirective;
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

    private final String table, totalIDColumn, keyColumn, valueColumn;

    @Override
    public @NotNull Gson getGson() {
        return KissenBaseMeta.GSON;
    }

    @Override
    public boolean metaContains(@NotNull String totalID, @NotNull String key) throws BackendException {
        return !getString(totalID, key).isCompletedExceptionally();
    }

    @Override
    public void setString(@NotNull String key, @Nullable String value) throws BackendException {
        setString(UNDEFINED, key, value);
    }

    @Override
    public void setString(@NotNull String totalID, @NotNull String key, @Nullable String value) throws BackendException {
        setObject(totalID, key, value);
    }

    @Override
    public void setLong(@NotNull String key, long value) throws BackendException {
        setLong(UNDEFINED, key, value);
    }

    @Override
    public void setLong(@NotNull String totalID, @NotNull String key, long value) throws BackendException {
        setObject(totalID, key, value);
    }

    @Override
    public void setDouble(@NotNull String key, double value) throws BackendException {
        setDouble(UNDEFINED, key, value);
    }

    @Override
    public void setDouble(@NotNull String totalID, @NotNull String key, double value) throws BackendException {
        setObject(totalID, key, value);
    }

    @Override
    public void setFloat(@NotNull String key, float value) throws BackendException {
        setFloat(UNDEFINED, key, value);
    }

    @Override
    public void setFloat(@NotNull String totalID, @NotNull String key, float value) throws BackendException {
        setObject(totalID, key, value);
    }

    @Override
    public void setInt(@NotNull String key, int value) throws BackendException {
        setInt(UNDEFINED, key, value);
    }

    @Override
    public void setInt(@NotNull String totalID, @NotNull String key, int value) throws BackendException {
        setObject(totalID, key, value);
    }

    @Override
    public void setShort(@NotNull String key, short value) throws BackendException {
        setShort(UNDEFINED, key, value);
    }

    @Override
    public void setShort(@NotNull String totalID, @NotNull String key, short value) throws BackendException {
        setObject(totalID, key, value);
    }

    @Override
    public void setByte(@NotNull String key, byte value) throws BackendException {
        setByte(UNDEFINED, key, value);
    }

    @Override
    public void setByte(@NotNull String totalID, @NotNull String key, byte value) throws BackendException {
        setObject(totalID, key, value);
    }

    @Override
    public void setBoolean(@NotNull String key, boolean value) throws BackendException {
        setBoolean(UNDEFINED, key, value);
    }

    @Override
    public void setBoolean(@NotNull String totalID, @NotNull String key, boolean value) throws BackendException {
        setObject(totalID, key, value);
    }

    @Override
    public <T> void setObject(@NotNull String key, @Nullable T value) throws BackendException {
        setObject(UNDEFINED, key, value);
    }

    @Override
    public void setCollection(@NotNull String key, @Nullable Collection<?> value) throws BackendException {
        setCollection(UNDEFINED, key, value);
    }

    @Override
    public void setCollection(@NotNull String totalID, @NotNull String key, @Nullable Collection<?> value) throws BackendException {
        setObject(totalID, key, value);
    }

    @Override
    public <T> void setObject(@NotNull String totalID, @NotNull String key, @Nullable T value) throws BackendException {
        setJson(totalID, key, serialize(value));
    }

    @Override
    public void delete(@NotNull String totalID, @NotNull String key) throws BackendException {
        setObject(totalID, key, null);
    }

    @Override
    public void delete(@NotNull String key) throws BackendException {
        delete(UNDEFINED, key);
    }

    @Override
    public @NotNull CompletableFuture<String> getString(@NotNull String key) throws BackendException {
        return getString(UNDEFINED, key);
    }

    @Override
    public @NotNull CompletableFuture<String> getString(@NotNull String totalID, @NotNull String key) throws BackendException {
        return getObject(totalID, key, String.class);
    }

    @Override
    public @NotNull CompletableFuture<Long> getLong(@NotNull String key) throws BackendException {
        return getLong(UNDEFINED, key);
    }

    @Override
    public @NotNull CompletableFuture<Long> getLong(@NotNull String totalID, @NotNull String key) throws BackendException {
        return getObject(totalID, key, Long.class);
    }

    @Override
    public @NotNull CompletableFuture<Double> getDouble(@NotNull String key) throws BackendException {
        return getDouble(UNDEFINED, key);
    }

    @Override
    public @NotNull CompletableFuture<Double> getDouble(@NotNull String totalID, @NotNull String key) throws BackendException {
        return getObject(totalID, key, Double.class);
    }

    @Override
    public @NotNull CompletableFuture<Float> getFloat(@NotNull String key) throws BackendException {
        return getFloat(UNDEFINED, key);
    }

    @Override
    public @NotNull CompletableFuture<Float> getFloat(@NotNull String totalID, @NotNull String key) throws BackendException {
        return getObject(totalID, key, Float.class);
    }

    @Override
    public @NotNull CompletableFuture<Integer> getInt(@NotNull String key) throws BackendException {
        return getInt(UNDEFINED, key);
    }

    @Override
    public @NotNull CompletableFuture<Integer> getInt(@NotNull String totalID, @NotNull String key) throws BackendException {
        return getObject(totalID, key, Integer.class);
    }

    @Override
    public @NotNull CompletableFuture<Short> getShort(@NotNull String key) throws BackendException {
        return getShort(UNDEFINED, key);
    }

    @Override
    public @NotNull CompletableFuture<Short> getShort(@NotNull String totalID, @NotNull String key) throws BackendException {
        return getObject(totalID, key, Short.class);
    }

    @Override
    public @NotNull CompletableFuture<Byte> getByte(@NotNull String key) throws BackendException {
        return getByte(UNDEFINED, key);
    }

    @Override
    public @NotNull CompletableFuture<Byte> getByte(@NotNull String totalID, @NotNull String key) throws BackendException {
        return getObject(totalID, key, Byte.class);
    }

    @Override
    public @NotNull CompletableFuture<Boolean> getBoolean(@NotNull String key) throws BackendException {
        return getBoolean(UNDEFINED, key);
    }

    @Override
    public @NotNull CompletableFuture<Boolean> getBoolean(@NotNull String totalID, @NotNull String key) throws BackendException {
        return getObject(totalID, key, Boolean.class);
    }

    @Override
    public @NotNull <T> CompletableFuture<T> getObject(@NotNull String totalID, @NotNull String key, @NotNull Class<T> type) throws BackendException {
        return getJson(totalID, key).thenApply(data -> (T) data);
    }

    @Override
    public @NotNull <T> CompletableFuture<T> getObject(@NotNull String key, @NotNull Class<T> type) throws BackendException {
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
    public @NotNull QueryUpdate.RootQueryUpdate update(@NotNull QueryUpdateDirective... queryUpdateDirective) {
        return new KissenQueryUpdate.KissenRootQueryUpdate(queryUpdateDirective) {
            @Override
            public @NotNull CompletableFuture<Long> execute() {
                return KissenBaseMeta.this.execute(getQuery());
            }
        };
    }

    @Override
    public @NotNull <T> CompletableFuture<MetaList<T>> getCollection(@NotNull String totalID, @NotNull String key, @NotNull Class<T> type) throws BackendException {
        return getObject(totalID, key, Object[].class).handle((data, throwable) ->
        {
            MetaList<T> metaList = new KissenMetaList<>();
            if(Objects.nonNull(data))
            {
                metaList.addAll(Arrays.stream((T[]) data).toList());
            }
            metaList.setListAction((o1, o2, o3) -> setCollection(totalID, key, metaList));
            return metaList;
        });
    }

    @Override
    public @NotNull <T> CompletableFuture<MetaList<T>> getCollection(@NotNull String key, @NotNull Class<T> type) throws BackendException {
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
    protected @NotNull String getColumn(@NotNull Column column) {
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
    protected <T> @NotNull T deserialize(@NotNull String json) throws ClassNotFoundException {
        JsonObject object = JsonParser.parseString(json).getAsJsonObject();

        String value = object.get("object").toString();
        String clazzString = object.get("type").getAsString();

        return (T) getGson().fromJson(value, Class.forName(clazzString));
    }

    /**
     * Serializes the specified object into a JSON string representation.
     *
     * <p>The {@code serialize} method wraps the object using an {@link InternalWrapper} and converts it into a JSON string
     * using the GSON library. If the object is null or an empty collection, it returns null.</p>
     *
     * @param object the object to serialize
     * @return a JSON string representation of the object, or null if the object is null or an empty collection
     */
    protected @Nullable String serialize(@Nullable Object object) {
        if (object == null || (object instanceof Collection<?> collection && collection.isEmpty())) {
            return null;
        }

        return InternalWrapper.wrap(object).toJson();
    }

    /**
     * Sets the specified key-value pair in the JSON identified by the given total ID.
     *
     * <p>The {@code setJson} method sets the specified {@code key} and its corresponding {@code value} in the JSON structure
     * identified by the given {@code totalID}. This method is intended for manipulating JSON data in a storage or database system.
     * The implementation of this method should ensure that the changes are applied to the appropriate JSON structure.</p>
     *
     * @param totalID the total ID identifying the JSON structure
     * @param key the key to be set in the JSON structure
     * @param value the value to be associated with the specified key
     * @throws NullPointerException if {@code totalID} or {@code key} is {@code null}
     */
    protected abstract void setJson(@NotNull String totalID, @NotNull String key, @Nullable String value);

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
    private static @NotNull BiFunction<Object, Throwable, Object> logExceptions() {
        return (object, throwable) ->
        {
            if (throwable == null) {
                return object;
            }
            String message = "There was an exception caught when fetching data from the database.";
            KissenCore.getInstance().getLogger().debug(message, throwable);
            return object;
        };
    }

    /**
     * A record representing an internal wrapper for objects, associating a type and an object.
     *
     * <p>The {@code InternalWrapper} record is used to wrap objects along with their corresponding type.
     * It provides a convenient way to represent different types of objects uniformly and can be used
     * for serialization and other purposes.</p>
     *
     * @param type the type of the wrapped object
     * @param object the wrapped object
     */
    private record InternalWrapper(@NotNull String type, @NotNull Object object) {

        /**
         * Wraps the specified object with its corresponding type.
         *
         * <p>The {@code wrap} method is a factory method that creates an {@code InternalWrapper} instance
         * for the given object. If the object is a Collection, it determines the type based on the first
         * element in the collection. For other types, it uses the class name as the type.</p>
         *
         * @param object the object to be wrapped
         * @param <T> the type of the object
         * @return an {@code InternalWrapper} instance wrapping the specified object
         */
        private static <T> @NotNull InternalWrapper wrap(@NotNull T object) {
            if (object instanceof Collection<?> collection) {
                String typeName = collection.stream().findFirst().orElseThrow().getClass().getName();
                return new InternalWrapper(ARRAY_PATTERN.formatted(typeName), collection.toArray(Object[]::new));
            }
            return new InternalWrapper(object.getClass().getName(), object);
        }

        /**
         * Converts the internal wrapper to a JSON string.
         *
         * <p>The {@code toJson} method uses the GSON library from KissenBaseMeta to convert the internal wrapper
         * into a JSON string representation.</p>
         *
         * @return a JSON string representation of the internal wrapper
         * @see KissenBaseMeta#GSON
         */
        private @NotNull String toJson() {
            return KissenBaseMeta.GSON.toJson(this);
        }
    }
}
