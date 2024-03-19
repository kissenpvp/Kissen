package net.kissenpvp.core.database.mongodb.query;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.kissenpvp.core.api.database.queryapi.Column;
import net.kissenpvp.core.api.database.queryapi.select.QuerySelect;
import net.kissenpvp.core.database.mongodb.KissenMongoMeta;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Executes select queries in MongoDB.
 *
 * <p>
 * This class extends {@link MongoQueryExecutor}, providing functionality to execute select queries in MongoDB.
 * </p>
 * @see MongoQueryExecutor
 */
@Slf4j
@Getter
public class MongoSelectQueryExecutor extends MongoQueryExecutor {

    private final QuerySelect select;

    /**
     * Constructs a new MongoSelectQueryExecutor with the specified select query and MongoDB metadata.
     *
     * @param select the select query to be executed
     * @param meta   the MongoDB metadata
     * @throws NullPointerException if either the select query or the MongoDB metadata is {@code null}
     */
    public MongoSelectQueryExecutor(@NotNull QuerySelect select, @NotNull KissenMongoMeta meta) {
        super(meta);
        this.select = select;
    }

    /**
     * Retrieves data from the database based on the specified filter queries.
     *
     * <p>This method executes a database query using the filter queries specified by {@link #getSelect()}. It retrieves the data that matches the filter criteria and returns it as a two-dimensional array of {@link Object}.
     * Each row in the array represents a record from the database, and each column represents a field or attribute of the record.</p>
     *
     * @return a two-dimensional array of {@link Object} containing the retrieved data
     * @throws NullPointerException if the filter queries are `null`
     */
    public @NotNull Object[][] select() {
        List<Object[]> data = new ArrayList<>();
        find(super.where(getSelect().getFilterQueries()), handleResponse(data));
        return data.toArray(Object[][]::new);
    }

    /**
     * Finds documents in the database collection that match the specified filter and performs the specified action on each document.
     *
     * <p>This method executes a database query to find documents in the collection that match the specified filter. It then performs the specified action on each document found. The action is represented by the provided {@link Consumer}, which processes each document.</p>
     *
     * @param filter the filter to apply when searching for documents
     * @param consume the action to perform on each document found
     * @throws NullPointerException if either the filter or the consumer is `null`
     */
    private void find(@NotNull Bson filter, @NotNull Consumer<Document> consume) {
        getMeta().getCollection().find(filter).forEach(consume);
    }

    /**
     * Creates a {@link Consumer} to handle the response from the database query.
     *
     * <p>This method constructs a {@link Consumer} that processes each {@link Document} returned by the database query. It extracts data from each document and adds it to the provided list of arrays, representing the retrieved data.</p>
     *
     * @param data the list to which the retrieved data arrays will be added
     * @return a {@link Consumer} to handle the response from the database query
     * @throws NullPointerException if the list of data is `null`
     */
    @Contract(pure = true)
    private @NotNull Consumer<Document> handleResponse(@NotNull List<Object[]> data) {
        return (document) -> {
            try {
                Object[] result = new Object[getSelect().getColumns().length];
                for (int i = 0; i < getSelect().getColumns().length; i++) {
                    result[i] = handleResponse(select.getColumns()[i], document);
                }
                data.add(result);
            } catch (ClassNotFoundException classNotFoundException) {
                String clazz = document.getString(getMeta().getTypeColumn());
                String exceptionMessage = "The class {} contains an invalid class and cannot be deserialized.";
                log.debug(exceptionMessage, clazz, classNotFoundException);
            }
        };
    }


    /**
     * Handles the response for a specific column in the document.
     *
     * <p>This method extracts the value of the specified column from the document and returns it. If the column represents a serialized object, it deserializes the object before returning it.</p>
     *
     * @param column the column for which to handle the response
     * @param document the document from which to extract the column value
     * @return the value of the specified column
     * @throws ClassNotFoundException if the class specified in the document cannot be found during deserialization
     */
    private @NotNull Object handleResponse(@NotNull Column column, @NotNull Document document) throws ClassNotFoundException {
        String columnName = getMeta().getColumn(column);
        if (Objects.equals(column, Column.VALUE)) {
            Class<?> clazz = Class.forName(document.getString(getMeta().getTypeColumn()));
            return getMeta().deserialize(clazz, document.getString(columnName));
        }
        return document.getString(columnName);
    }
}
