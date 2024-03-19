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
import com.mongodb.client.model.UpdateOptions;
import net.kissenpvp.core.api.database.meta.BackendException;
import net.kissenpvp.core.api.database.queryapi.select.QuerySelect;
import net.kissenpvp.core.api.database.queryapi.update.QueryUpdate;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.database.KissenBaseMeta;
import net.kissenpvp.core.database.mongodb.query.MongoSelectQueryExecutor;
import net.kissenpvp.core.database.mongodb.query.MongoUpdateQueryExecutor;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public abstract class KissenMongoMeta extends KissenBaseMeta {

    public KissenMongoMeta(String table, String totalIDColumn, String keyColumn, String valueColumn) {
        super(table, totalIDColumn, keyColumn, valueColumn, "type");
    }

    @Override
    public void purge(@NotNull String totalID) {
        getCollection().deleteMany(new Document(getTotalIDColumn(), totalID));
    }

    @Override
    protected void setJson(@NotNull String totalID, @NotNull String key, @Nullable Object object) {
        String[] data = serialize(object);
        if (data == null) {
            getCollection().deleteMany(new Document(getTotalIDColumn(), totalID).append(getKeyColumn(), key));
            return;
        }

        Document document = new Document();
        document.append(getTotalIDColumn(), totalID);
        document.append(getKeyColumn(), key);

        document.append(getTypeColumn(), data[0]);
        document.append(getValueColumn(), data[1]);

        getCollection().updateOne(Filters.and(Filters.eq(getTotalIDColumn(), totalID), Filters.eq(getKeyColumn(), key)), new Document("$set", document), new UpdateOptions().upsert(true));
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

    public abstract @NotNull MongoCollection<Document> getCollection();
}
