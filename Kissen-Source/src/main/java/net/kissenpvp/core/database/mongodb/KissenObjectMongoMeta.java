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

package net.kissenpvp.core.database.mongodb;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.WriteModel;
import net.kissenpvp.core.api.base.plugin.KissenPlugin;
import net.kissenpvp.core.api.database.meta.BackendException;
import net.kissenpvp.core.api.database.meta.ObjectMeta;
import net.kissenpvp.core.api.database.meta.Table;
import net.kissenpvp.core.api.database.queryapi.Column;
import net.kissenpvp.core.api.database.queryapi.select.QuerySelect;
import net.kissenpvp.core.api.database.savable.Savable;
import net.kissenpvp.core.api.database.savable.SavableMap;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.database.savable.KissenSavableMap;
import org.bson.Document;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public abstract class KissenObjectMongoMeta extends KissenNativeMongoMeta implements ObjectMeta {

    public KissenObjectMongoMeta(@NotNull Table table, @Nullable KissenPlugin plugin) {
        super(table, plugin);
    }

    @Override
    public void addMap(@NotNull String id, final @NotNull Map<@NotNull String, @NotNull Object> data) throws BackendException {
        getCollection().bulkWrite(data.entrySet().stream().map(buildUpdateQuery(id)).toList());
        KissenCore.getInstance().getLogger().debug("Insert map {} with id {}.", data, id);
    }


    @Contract(pure = true)
    private @NotNull Function<Map.Entry<String, Object>, WriteModel<Document>> buildUpdateQuery(@NotNull String id) {
        return value -> {
            Document document = new Document();
            document.append(getTable().getColumn(Column.TOTAL_ID), id);
            document.append(getTable().getColumn(Column.KEY), value.getKey());

            String[] serialized = serialize(value.getValue());
            document.append(getTable().getPluginColumn(), getPluginName());
            document.append(getTable().getTypeColumn(), serialized[0]); // type
            document.append(getTable().getColumn(Column.VALUE), serialized[1]); // value

            return new UpdateOneModel<>(Filters.and(Filters.eq(getTable().getColumn(Column.TOTAL_ID), id), Filters.eq(getTable().getColumn(Column.KEY), value.getKey())), new Document("$set", document), new UpdateOptions().upsert(true));
        };
    }

    @Override
    public @NotNull CompletableFuture<SavableMap> getData(@NotNull String totalId) throws BackendException {
        return processQuery(select(Column.TOTAL_ID, Column.KEY, Column.VALUE).where(Column.TOTAL_ID, totalId)).thenApply(map -> map.get(totalId));
    }

    @Override
    public @Unmodifiable @NotNull <T extends Savable> CompletableFuture<@Unmodifiable Map<@NotNull String, @NotNull SavableMap>> getData(@NotNull T savable) throws BackendException {
        return processQuery(select(Column.TOTAL_ID, Column.KEY, Column.VALUE).where(Column.TOTAL_ID, "^" + savable.getSaveID()));
    }

    private @NotNull CompletableFuture<Map<String, SavableMap>> processQuery(@NotNull QuerySelect querySelect) throws BackendException {
        return querySelect.execute().thenApply(this::processQuery);
    }

    private @NotNull CompletableFuture<@Unmodifiable Map<String, SavableMap>> processQuery(@NotNull String totalID, @NotNull QuerySelect querySelect) throws BackendException {
        return querySelect.execute().thenApply(this::processQuery);
    }

    private @NotNull Map<String, SavableMap> processQuery(@NotNull Object @NotNull [] @NotNull [] data) throws BackendException {
        Map<String, SavableMap> dataContainer = new HashMap<>();
        for (Object[] current : data) {
            Function<String, SavableMap> generateMap = (id) -> new KissenSavableMap(id, KissenObjectMongoMeta.this, Collections.EMPTY_MAP);
            dataContainer.computeIfAbsent(current[0].toString(), generateMap).put(current[1].toString(), current[2]);
        }
        return dataContainer;
    }
}
