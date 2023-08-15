package net.kissenpvp.core.database.mongodb;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import net.kissenpvp.core.api.database.meta.BackendException;
import net.kissenpvp.core.api.database.queryapi.FilterQuery;
import net.kissenpvp.core.api.database.queryapi.QuerySelect;
import net.kissenpvp.core.api.database.queryapi.QueryUpdate;
import net.kissenpvp.core.api.database.queryapi.QueryUpdateDirective;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.database.KissenBaseMeta;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public abstract class KissenMongoMeta extends KissenBaseMeta {

    public KissenMongoMeta(String table, String totalIDColumn, String keyColumn, String valueColumn) {
        super(table, totalIDColumn, keyColumn, valueColumn);
    }

    @Override
    public void purge(@NotNull String totalID) {
        getCollection().deleteOne(new Document(getTotalIDColumn(), totalID));
    }

    @Override
    public void setString(@NotNull String totalID, @NotNull String key, @Nullable String value) {
        insert(totalID, key, value);
    }

    @Override
    public void setStringList(@NotNull String totalID, @NotNull String key, @Nullable List<String> value) {
        insert(totalID, key, value);
    }

    @Override
    public @NotNull String[][] execute(@NotNull QuerySelect querySelect) throws BackendException {

        FindIterable<Document> documents = getCollection().find(
                Filters.and(decodeFilterQueries(querySelect.getFilterQueries())));
        List<String[]> data = new ArrayList<>();
        for (Document element : documents) {
            String[] result = new String[querySelect.getColumns().length];
            for (int i = 0; i < result.length; i++) {
                String column = getColumn(querySelect.getColumns()[i]);
                try {
                    result[i] = element.getString(column);
                } catch (ClassCastException classCastException) {
                    result[i] = element.getList(column, String.class).toString();
                }
            }
            data.add(result);
        }

        KissenCore.getInstance()
                .getLogger()
                .info("Fetch columns {} filtered with {}.", Arrays.toString(Arrays.stream(querySelect.getColumns())
                        .map(Enum::name)
                        .toArray()), Arrays.toString(Arrays.stream(querySelect.getFilterQueries())
                        .map(filterQuery -> String.format("%s = %s [%s]", filterQuery.getColumn()
                                .name(), filterQuery.getValue(), filterQuery.getFilterType().name()))
                        .toArray()));

        return data.toArray(new String[0][]);
    }

    @Override
    public long execute(@NotNull QueryUpdate queryUpdate) throws BackendException {

        return getCollection().updateMany(Filters.and(decodeFilterQueries(queryUpdate.getFilterQueries())),
                Updates.combine(
                        decodeUpdateDirectives(queryUpdate.getColumns()))).getModifiedCount();
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
        Bson[] filters = new Bson[querySelect.length];
        for (int i = 0; i < filters.length; i++) {
            FilterQuery filterQuery = querySelect[i];
            filters[i] = switch (filterQuery.getFilterType()) {
                case EQUALS -> Filters.eq(getColumn(filterQuery.getColumn()), filterQuery.getValue());
                case END -> Filters.regex(getColumn(filterQuery.getColumn()), filterQuery.getValue() + "$");
                case START -> Filters.regex(getColumn(filterQuery.getColumn()), "^" + filterQuery.getValue());
            };
        }
        return filters;
    }

    protected abstract @NotNull MongoCollection<Document> getCollection();

    @Override
    public @NotNull @Unmodifiable List<String> getStringList(@NotNull String totalID, @NotNull String key) {
        List<String> strings = new ArrayList<>();
        FindIterable<Document> documents = getCollection().find(Filters.and(Filters.eq(getTotalIDColumn(), totalID), Filters.eq(getKeyColumn(), key)));
        for (Document element : documents) {
            strings.addAll(element.getList(getValueColumn(), String.class));
            break;
        }
        KissenCore.getInstance().getLogger().info("Fetch list with id {} and key {}.", totalID, key);
        return strings;
    }

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


        KissenCore.getInstance().getLogger().info("Set {} to {} from id {}.", key, value, totalID);
    }

    private void wipe(@NotNull String totalID, @NotNull String key) {
        getCollection().deleteMany(new Document(getTotalIDColumn(), totalID).append(getKeyColumn(), key));
    }
}
