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

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import net.kissenpvp.core.api.database.meta.BackendException;
import net.kissenpvp.core.api.database.queryapi.FilterOperator;
import net.kissenpvp.core.api.database.queryapi.FilterQuery;
import net.kissenpvp.core.api.database.queryapi.select.QuerySelect;
import net.kissenpvp.core.api.database.queryapi.update.QueryUpdate;
import net.kissenpvp.core.api.database.queryapi.update.QueryUpdateDirective;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.database.KissenBaseMeta;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;


public abstract class KissenMongoMeta extends KissenBaseMeta {

    public KissenMongoMeta(String table, String totalIDColumn, String keyColumn, String valueColumn) {
        super(table, totalIDColumn, keyColumn, valueColumn);
    }

    @Override
    public void purge(@NotNull String totalID) {
        getCollection().deleteMany(new Document(getTotalIDColumn(), totalID));
    }

    @Override
    public void setString(@NotNull String totalID, @NotNull String key, @Nullable String value) {
        insert(totalID, key, value);
    }

    @Override
    protected @NotNull CompletableFuture<String[][]> execute(@NotNull QuerySelect querySelect) throws BackendException
    {
        return CompletableFuture.supplyAsync(() ->
        {
            FindIterable<Document> documents = getCollection().find(
                    Filters.and(decodeFilterQueries(querySelect.getFilterQueries())));
            List<String[]> data = new ArrayList<>();
            for (Document element : documents)
            {
                String[] result = new String[querySelect.getColumns().length];
                for (int i = 0; i < result.length; i++)
                {
                    String column = getColumn(querySelect.getColumns()[i]);
                    result[i] = element.getString(column);
                }
                data.add(result);
            }

            KissenCore.getInstance().getLogger().debug("Fetch columns {} filtered with {}.",
                    Arrays.toString(Arrays.stream(querySelect.getColumns()).map(Enum::name).toArray()), Arrays.toString(
                            Arrays.stream(querySelect.getFilterQueries()).map(
                                    filterQuery -> String.format("%s = %s [%s]", filterQuery.getColumn().name(),
                                            filterQuery.getValue(), filterQuery.getFilterType().name())).toArray()));

            return data.toArray(new String[0][]);
        });
    }

    @Override
    protected @NotNull CompletableFuture<Long> execute(@NotNull QueryUpdate queryUpdate) throws BackendException
    {
        return CompletableFuture.supplyAsync(
                () -> getCollection().updateMany(Filters.and(decodeFilterQueries(queryUpdate.getFilterQueries())),
                        Updates.combine(decodeUpdateDirectives(queryUpdate.getColumns()))).getModifiedCount());
    }

    @NotNull
    private Bson @NotNull [] decodeUpdateDirectives(@NotNull QueryUpdateDirective @NotNull ... queryUpdateDirectives) {
        Bson[] updateDirectives = new Bson[queryUpdateDirectives.length];
        for (int i = 0; i < updateDirectives.length; i++) {
            QueryUpdateDirective queryUpdateDirective = queryUpdateDirectives[i];
            updateDirectives[i] = Updates.set(getColumn(queryUpdateDirective.column()), queryUpdateDirective.value());
        }
        return updateDirectives;
    }

    @NotNull
    private Bson @NotNull [] decodeFilterQueries(@NotNull FilterQuery @NotNull ... querySelect) {
        List<Bson> filters = new ArrayList<>();
        FilterOperator currentOperator = FilterOperator.INIT;
        List<Bson> currentOperatorFilters = new ArrayList<>();

        for (FilterQuery filterQuery : querySelect) {
            Bson filter = switch (filterQuery.getFilterType()) {
                case EXACT_MATCH -> Filters.eq(getColumn(filterQuery.getColumn()), filterQuery.getValue());
                case ENDS_WITH -> Filters.regex(getColumn(filterQuery.getColumn()), filterQuery.getValue() + "$");
                case STARTS_WITH -> Filters.regex(getColumn(filterQuery.getColumn()), "^" + filterQuery.getValue());
            };

            if (currentOperator != filterQuery.getFilterOperator() && !currentOperatorFilters.isEmpty()) {
                switch (currentOperator) {
                    case AND -> filters.add(Filters.and(currentOperatorFilters));
                    case OR -> filters.add(Filters.or(currentOperatorFilters));
                    case INIT -> filters.addAll(currentOperatorFilters);
                }
                currentOperatorFilters.clear();
            }

            currentOperator = filterQuery.getFilterOperator();
            currentOperatorFilters.add(filter);
        }

        switch (currentOperator) {
            case AND -> filters.add(Filters.and(currentOperatorFilters));
            case OR -> filters.add(Filters.or(currentOperatorFilters));
            case INIT -> filters.addAll(currentOperatorFilters);
        }

        return filters.toArray(new Bson[0]);
    }

    protected abstract @NotNull MongoCollection<Document> getCollection();

    private void insert(@NotNull String totalID, @NotNull String key, @Nullable Object value) {

        if (value == null) {
            wipe(totalID, key);
            return;
        }

        Document document = new Document();
        document.append(getTotalIDColumn(), totalID);
        document.append(getKeyColumn(), key);
        document.append(getValueColumn(), value);

        getCollection().updateOne(Filters.and(Filters.eq(getTotalIDColumn(), totalID), Filters.eq(getKeyColumn(), key)),
                new Document("$set", document), new UpdateOptions().upsert(true));


        KissenCore.getInstance().getLogger().debug("Set {} to {} from id {}.", key, value, totalID);
    }

    private void wipe(@NotNull String totalID, @NotNull String key) {
        getCollection().deleteMany(new Document(getTotalIDColumn(), totalID).append(getKeyColumn(), key));
    }
}
