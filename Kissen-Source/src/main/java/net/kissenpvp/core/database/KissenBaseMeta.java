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
import net.kissenpvp.core.database.queryapi.KissenQuerySelect;
import net.kissenpvp.core.database.queryapi.KissenQueryUpdate;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

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
    public @NotNull <T> CompletableFuture<Collection<T>> getCollection(@NotNull String totalID, @NotNull String key, @NotNull Class<T> type) throws BackendException {
        return getObject(totalID, key, Object[].class).thenApply(data -> Arrays.stream((T[]) data).toList());
    }

    @Override
    public @NotNull <T> CompletableFuture<Collection<T>> getCollection(@NotNull String key, @NotNull Class<T> type) throws BackendException {
        return getCollection(UNDEFINED, key, type);
    }

    protected @NotNull String getColumn(@NotNull Column column) {
        return switch (column) {
            case TOTAL_ID -> getTotalIDColumn();
            case KEY -> getKeyColumn();
            case VALUE -> getValueColumn();
        };
    }

    protected @NotNull QuerySelect getDefaultQuery(@NotNull String totalID, @NotNull String key) {
        return select(Column.VALUE).where(Column.TOTAL_ID, totalID).and(Column.KEY, key);
    }

    protected @NotNull CompletableFuture<?> getJson(@NotNull String totalID, @NotNull String key) {
        return getDefaultQuery(totalID, key).execute().thenApply(data -> {
            if (data.length == 0 || data[0].length == 0) {
                throw new NullPointerException();
            }

            return data[0][0];
        });
    }

    protected <T> @NotNull T deserialize(@NotNull String json) throws ClassNotFoundException {
        JsonObject object = JsonParser.parseString(json).getAsJsonObject();

        String value = object.get("object").toString();
        String clazzString = object.get("type").getAsString();

        return (T) getGson().fromJson(value, Class.forName(clazzString));
    }

    protected @Nullable String serialize(@Nullable Object object) {
        if (object == null) {
            return null;
        }

        return InternalWrapper.wrap(object).toJson();
    }

    protected abstract void setJson(@NotNull String totalID, @NotNull String key, @Nullable String value);

    protected abstract CompletableFuture<Object[][]> execute(@NotNull QuerySelect querySelect);

    protected abstract CompletableFuture<Long> execute(@NotNull QueryUpdate queryUpdate);

    private record InternalWrapper(@NotNull String type, @NotNull Object object) {

        private static <T> @NotNull InternalWrapper wrap(@NotNull T object) {
            if (object instanceof Collection<?> collection) {

                String typeName = collection.stream().findFirst().orElseThrow().getClass().getName();
                return new InternalWrapper(ARRAY_PATTERN.formatted(typeName), collection.toArray(Object[]::new));
            }
            return new InternalWrapper(object.getClass().getName(), object);
        }

        private @NotNull String toJson() {
            return KissenBaseMeta.GSON.toJson(this);
        }
    }
}
