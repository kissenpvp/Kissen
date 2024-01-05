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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kissenpvp.core.api.database.DataImplementation;
import net.kissenpvp.core.api.database.meta.BackendException;
import net.kissenpvp.core.api.database.meta.Meta;
import net.kissenpvp.core.api.database.queryapi.*;
import net.kissenpvp.core.api.database.queryapi.select.QuerySelect;
import net.kissenpvp.core.api.database.queryapi.update.QueryUpdate;
import net.kissenpvp.core.api.database.queryapi.update.QueryUpdateDirective;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.database.queryapi.KissenQuerySelect;
import net.kissenpvp.core.database.queryapi.KissenQueryUpdate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@AllArgsConstructor
@Getter
public abstract class KissenBaseMeta implements Meta {

    private static final String UNDEFINED = "_UNDEFINED_";
    private final String table, totalIDColumn, keyColumn, valueColumn;

    @Override
    public boolean metaContains(@NotNull String totalID, @NotNull String key) throws BackendException {
        return !getString(totalID, key).isCompletedExceptionally();
    }

    @Override
    public void setString(@NotNull String key, @Nullable String value) throws BackendException {
        setString(UNDEFINED, key, value);
    }

    @Override
    public void setLong(@NotNull String totalID, @NotNull String key, long value) throws BackendException {
        setString(totalID, key, String.valueOf(value));
    }

    @Override
    public void setLong(@NotNull String key, long value) throws BackendException {
        setString(key, String.valueOf(value));
    }

    @Override
    public void setDouble(@NotNull String totalID, @NotNull String key, double value) throws BackendException {
        setString(totalID, key, String.valueOf(value));
    }

    @Override
    public void setDouble(@NotNull String key, double value) throws BackendException {
        setString(key, String.valueOf(value));
    }

    @Override
    public void setFloat(@NotNull String totalID, @NotNull String key, float value) throws BackendException {
        setString(totalID, key, String.valueOf(value));
    }

    @Override
    public void setFloat(@NotNull String key, float value) throws BackendException {
        setString(key, String.valueOf(value));
    }

    @Override
    public void setInt(@NotNull String totalID, @NotNull String key, int value) throws BackendException {
        setString(totalID, key, String.valueOf(value));
    }

    @Override
    public void setInt(@NotNull String key, int value) throws BackendException {
        setString(key, String.valueOf(value));
    }

    @Override
    public void setShort(@NotNull String totalID, @NotNull String key, short value) throws BackendException {
        setString(totalID, key, String.valueOf(value));
    }

    @Override
    public void setShort(@NotNull String key, short value) throws BackendException {
        setString(key, String.valueOf(value));
    }

    @Override
    public void setByte(@NotNull String totalID, @NotNull String key, byte value) throws BackendException {
        setString(totalID, key, String.valueOf(value));
    }

    @Override
    public void setByte(@NotNull String key, byte value) throws BackendException {
        setString(key, String.valueOf(value));
    }

    @Override
    public void setBoolean(@NotNull String totalID, @NotNull String key, boolean value) throws BackendException {
        setString(totalID, key, String.valueOf(value));
    }

    @Override
    public void setBoolean(@NotNull String key, boolean value) throws BackendException {
        setString(key, String.valueOf(value));
    }

    @Override
    public void setStringList(@NotNull String key, @Nullable List<String> value) throws BackendException {
        setStringList(UNDEFINED, key, value);
    }

    @Override
    public void setStringList(@NotNull String totalID, @NotNull String key, @Nullable List<String> value) throws BackendException
    {
        setString(totalID, key, new Gson().toJson(value));
    }

    @Override
    public void setRecordList(@NotNull String totalID, @NotNull String key, @NotNull List<? extends Record> value) throws BackendException {
        setStringList(totalID, key, value.stream().map(record -> KissenCore.getInstance().getImplementation(DataImplementation.class).toJson(record))
                .toList());
    }

    @Override
    public void setRecordList(@NotNull String key, @NotNull List<? extends Record> value) throws BackendException {
        setRecordList(UNDEFINED, key, value);
    }

    @Override
    public <T extends Record> void setRecord(@NotNull String totalID, @NotNull String key, @NotNull T value) throws BackendException {
        setString(totalID, key, KissenCore.getInstance().getImplementation(DataImplementation.class).toJson(value));
    }

    @Override
    public <T extends Record> void setRecord(@NotNull String key, @NotNull T value) throws BackendException {
        setRecord(UNDEFINED, key, value);
    }

    @Override
    public void delete(@NotNull String totalID, @NotNull String key) throws BackendException {
        setString(totalID, key, null);
    }

    @Override
    public void delete(@NotNull String key) throws BackendException {
        delete(UNDEFINED, key);
    }

    @Override
    public @NotNull CompletableFuture<String> getString(@NotNull String totalID, @NotNull String key) throws BackendException
    {
        return getDefaultQuery(totalID, key).execute().thenApply(data -> data[0][0]);
    }

    @Override
    public @NotNull CompletableFuture<String> getString(@NotNull String key) throws BackendException
    {
        return getString(UNDEFINED, key);
    }

    @Override
    public @NotNull CompletableFuture<Long> getLong(@NotNull String totalID, @NotNull String key) throws BackendException
    {
        return getString(totalID, key).thenApply(Long::parseLong);
    }

    @Override
    public @NotNull CompletableFuture<Long> getLong(@NotNull String key) throws BackendException
    {
        return getString(key).thenApply(Long::parseLong);
    }

    @Override
    public @NotNull CompletableFuture<Double> getDouble(@NotNull String totalID, @NotNull String key) throws BackendException
    {
        return getString(totalID, key).thenApply(Double::parseDouble);
    }

    @Override
    public @NotNull CompletableFuture<Double> getDouble(@NotNull String key) throws BackendException
    {
        return getString(key).thenApply(Double::parseDouble);
    }

    @Override
    public @NotNull CompletableFuture<Float> getFloat(@NotNull String totalID, @NotNull String key) throws BackendException
    {
        return getString(totalID, key).thenApply(Float::parseFloat);
    }

    @Override
    public @NotNull CompletableFuture<Float> getFloat(@NotNull String key) throws BackendException
    {
        return getString(key).thenApply(Float::parseFloat);
    }

    @Override
    public @NotNull CompletableFuture<Integer> getInt(@NotNull String totalID, @NotNull String key) throws BackendException
    {
        return getString(totalID, key).thenApply(Integer::parseInt);
    }

    @Override
    public @NotNull CompletableFuture<Integer> getInt(@NotNull String key) throws BackendException
    {
        return getString(key).thenApply(Integer::parseInt);
    }

    @Override
    public @NotNull CompletableFuture<Short> getShort(@NotNull String totalID, @NotNull String key) throws BackendException
    {
        return getString(totalID, key).thenApply(Short::parseShort);
    }

    @Override
    public @NotNull CompletableFuture<Short> getShort(@NotNull String key) throws BackendException
    {
        return getString(key).thenApply(Short::parseShort);
    }

    @Override
    public @NotNull CompletableFuture<Byte> getByte(@NotNull String totalID, @NotNull String key) throws BackendException
    {
        return getString(totalID, key).thenApply(Byte::parseByte);
    }

    @Override
    public @NotNull CompletableFuture<Byte> getByte(@NotNull String key) throws BackendException
    {
        return getString(key).thenApply(Byte::parseByte);
    }

    @Override
    public @NotNull CompletableFuture<Boolean> getBoolean(@NotNull String totalID, @NotNull String key) throws BackendException
    {
        return getString(totalID, key).thenApply(Boolean::parseBoolean);
    }

    @Override
    public @NotNull CompletableFuture<Boolean> getBoolean(@NotNull String key) throws BackendException
    {
        return getString(key).thenApply(Boolean::parseBoolean);
    }

    @Override
    public @NotNull <T extends Record> CompletableFuture<T> getRecord(@NotNull String totalID, @NotNull String key, @NotNull Class<T> record) throws BackendException
    {
        return getDefaultQuery(totalID, key).execute().thenApply(
                data -> KissenCore.getInstance().getImplementation(DataImplementation.class).fromJson(data[0][0],
                        record));
    }

    @Override
    public @NotNull <T extends Record> CompletableFuture<T> getRecord(@NotNull String key, @NotNull Class<T> record) throws BackendException
    {
        return getRecord(UNDEFINED, key, record);
    }

    @Override
    public @NotNull QuerySelect.RootQuerySelect select(@NotNull Column... columns) {
        return new KissenQuerySelect.KissenRootQuerySelect(columns) {
            @Override
            public @NotNull CompletableFuture<String[][]> execute()
            {
                return KissenBaseMeta.this.execute(getQuery());
            }
        };
    }

    @Override
    public @NotNull QueryUpdate.RootQueryUpdate update(@NotNull QueryUpdateDirective... queryUpdateDirective) {
        return new KissenQueryUpdate.KissenRootQueryUpdate(queryUpdateDirective)
        {
            @Override
            public @NotNull CompletableFuture<Long> execute()
            {
                return KissenBaseMeta.this.execute(getQuery());
            }
        };
    }

    @Override
    public @NotNull <T extends Record> CompletableFuture<List<T>> getRecordList(@NotNull String totalID, @NotNull String key, @NotNull Class<T> record) throws BackendException
    {
        return getStringList(totalID, key).thenApply(list -> list.stream().map(
                data -> KissenCore.getInstance().getImplementation(DataImplementation.class).fromJson(data,
                        record)).toList());
    }

    @Override
    public @NotNull <T extends Record> CompletableFuture<List<T>> getRecordList(@NotNull String key, @NotNull Class<T> record) throws BackendException
    {
        return getRecordList(UNDEFINED, key, record);
    }

    @Override
    public @NotNull CompletableFuture<List<String>> getStringList(@NotNull String key) throws BackendException
    {
        return getStringList(UNDEFINED, key);
    }

    @Override
    public @NotNull CompletableFuture<List<String>> getStringList(@NotNull String totalID, @NotNull String key) throws BackendException
    {
        return select(Column.VALUE).where(Column.TOTAL_ID, totalID).and(Column.KEY, key).execute().thenApply(result ->
        {
            if (result.length == 0)
            {
                return new ArrayList<>();
            }
            return new Gson().fromJson(result[0][0], new TypeToken<List<String>>() {}.getType());
        });
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

    protected abstract CompletableFuture<String[][]> execute(@NotNull QuerySelect querySelect);

    protected abstract CompletableFuture<Long> execute(@NotNull QueryUpdate queryUpdate);
}
