package net.kissenpvp.core.database.mongodb.query;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import lombok.Getter;
import net.kissenpvp.core.api.database.queryapi.Column;
import net.kissenpvp.core.api.database.queryapi.update.QueryUpdate;
import net.kissenpvp.core.api.database.queryapi.update.Update;
import net.kissenpvp.core.database.mongodb.KissenMongoMeta;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Executes update queries in MongoDB.
 *
 * <p>
 * This class extends {@link MongoQueryExecutor}, providing functionality to execute update queries in MongoDB.
 * </p>
 *
 * @see MongoQueryExecutor
 */
@Getter
public class MongoUpdateQueryExecutor extends MongoQueryExecutor {

    private final QueryUpdate update;

    /**
     * Constructs a new MongoUpdateQueryExecutor with the specified update query and MongoDB metadata.
     *
     * @param update the update query to be executed
     * @param meta   the MongoDB metadata
     * @throws NullPointerException if either the update query or the MongoDB metadata is {@code null}
     */
    public MongoUpdateQueryExecutor(@NotNull QueryUpdate update, @NotNull KissenMongoMeta meta) {
        super(meta);
        this.update = update;
    }

    /**
     * Executes the update operation and returns the number of documents updated.
     * <p>
     * This method executes the update operation by applying the specified updates to the documents matching the filter queries.
     * It returns the total number of documents that were updated as a result of the operation.
     *
     * @return the number of documents updated
     */
    public long execute() {
        AtomicLong count = new AtomicLong();

        Bson update = Updates.combine(update());
        MongoCollection<Document> collection = getMeta().getCollection();

        UpdateResult updateResult = collection.updateMany(super.where(getUpdate().getFilterQueries()), update);
        count.addAndGet(updateResult.getMatchedCount());
        return count.get();
    }

    /**
     * Constructs an array of BSON update operations based on the specified update columns.
     * <p>
     * This method creates BSON update operations for each column specified in the update request.
     * It maps each update column to its corresponding BSON update operation and returns an array of BSON operations.
     *
     * @return an array of BSON update operations
     */
    private @NotNull Bson @NotNull [] update() {
        return Arrays.stream(getUpdate().getColumns()).map(this::updateRow).toArray(Bson[]::new);
    }

    /**
     * Constructs a BSON update operation for a single update row.
     * <p>
     * This method generates a BSON update operation based on the specified update object.
     * It determines the type of update operation based on the column type and returns the corresponding BSON operation.
     *
     * @param update the update object specifying the column and value to update
     * @return the BSON update operation for the specified update row
     */
    @Contract("_ -> new")
    private @NotNull Bson updateRow(@NotNull Update update) {
        String columnName = getMeta().getColumn(update.column());
        if (Objects.equals(update.column(), Column.VALUE)) {
            return updateValue(update.value(), columnName);
        }
        return Updates.set(getMeta().getColumn(update.column()), update.value());
    }

    /**
     * Constructs a BSON update operation for updating a value column.
     * <p>
     * This method creates a BSON update operation for updating a value column.
     * It serializes the value and combines it with an update for the type column.
     *
     * @param value      the value to be updated
     * @param columnName the name of the column to update
     * @return the BSON update operation for updating the value column
     */
    @Contract("_, _ -> new")
    private @NotNull Bson updateValue(@NotNull Object value, @NotNull String columnName) {
        String[] data = getMeta().serialize(value);
        return Updates.combine(Updates.set(getMeta().getTypeColumn(), data[0]), Updates.set(columnName, data[1]));
    }
}
