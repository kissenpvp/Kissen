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

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.WriteModel;
import net.kissenpvp.core.api.base.plugin.KissenPlugin;
import net.kissenpvp.core.api.database.meta.BackendException;
import net.kissenpvp.core.api.database.meta.Table;
import net.kissenpvp.core.api.database.queryapi.Column;
import net.kissenpvp.core.api.database.queryapi.select.QuerySelect;
import net.kissenpvp.core.api.database.queryapi.update.QueryUpdate;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.database.KissenBaseMeta;
import net.kissenpvp.core.database.mongodb.query.MongoSelectQueryExecutor;
import net.kissenpvp.core.database.mongodb.query.MongoUpdateQueryExecutor;
import org.bson.Document;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public abstract class KissenMongoMeta extends KissenBaseMeta {

    public KissenMongoMeta(@NotNull Table table, @Nullable KissenPlugin kissenPlugin) {
        super(table, kissenPlugin);
    }

    @Override
    public void purge(@NotNull String totalID) {
        getCollection().deleteMany(new Document(getTable().getColumn(Column.TOTAL_ID), totalID));
    }

    @Override
    public void addMap(@NotNull String id, final @NotNull Map<@NotNull String, @NotNull Object> data) throws BackendException {
        getCollection().bulkWrite(data.entrySet().stream().map(buildUpdateQuery(id)).toList());
        KissenCore.getInstance().getLogger().debug("Insert map {} with id {}.", data, id);
    }

    @Override
    protected void setJson(@NotNull String totalID, @NotNull String key, @Nullable Object object) {
        String[] data = serialize(object);
        if (data==null) {
            getCollection().deleteMany(new Document(getTable().getColumn(Column.TOTAL_ID), totalID).append(getTable().getColumn(Column.KEY), key));
            return;
        }

        Document document = new Document();
        document.append(getTable().getColumn(Column.TOTAL_ID), totalID);
        document.append(getTable().getColumn(Column.KEY), key);

        document.append(getTable().getPluginColumn(), getPluginName());
        document.append(getTable().getTypeColumn(), data[0]); // type
        document.append(getTable().getColumn(Column.VALUE), data[1]); // value

        getCollection().updateOne(Filters.and(Filters.eq(getTable().getColumn(Column.TOTAL_ID), totalID), Filters.eq(getTable().getColumn(Column.KEY), key)), new Document("$set", document), new UpdateOptions().upsert(true));
        KissenCore.getInstance().getLogger().debug("Set {} to {} from id {}.", key, data[0], totalID);
    }

    @Override
    protected @NotNull CompletableFuture<Object[][]> execute(@NotNull QuerySelect querySelect) throws BackendException {
        return CompletableFuture.supplyAsync(new MongoSelectQueryExecutor(querySelect, this)::select);
    }

    @Override
    protected @NotNull CompletableFuture<Long> execute(@NotNull QueryUpdate queryUpdate) throws BackendException {
        return CompletableFuture.supplyAsync(new MongoUpdateQueryExecutor(queryUpdate, this)::execute);
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

    public abstract @NotNull MongoCollection<Document> getCollection();
}
