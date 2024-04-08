package net.kissenpvp.core.database.mongodb.query;

import com.mongodb.client.model.Filters;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kissenpvp.core.api.database.queryapi.Column;
import net.kissenpvp.core.api.database.queryapi.FilterOperator;
import net.kissenpvp.core.api.database.queryapi.FilterQuery;
import net.kissenpvp.core.database.mongodb.KissenMongoMeta;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A utility class for executing MongoDB queries.
 * <p>
 * This class provides functionality for executing MongoDB queries. It is annotated with {@code @AllArgsConstructor} to generate
 * a constructor with all fields as parameters and {@code @Getter} to automatically generate getter methods for all fields.
 *
 * @see Getter
 * @see AllArgsConstructor
 */
@Getter
@AllArgsConstructor
public class MongoQueryExecutor {

    private final @NotNull KissenMongoMeta meta;

    /**
     * Constructs a BSON filter representing a logical OR operation based on the provided array of {@link FilterQuery} objects.
     *
     * <p>
     * This method constructs a BSON filter representing a logical OR operation based on the provided array of {@link FilterQuery} objects.
     * It creates a list of BSON filters using the {@link #getFilters(List, FilterQuery[])} method, and then combines them using the
     * {@link Filters#or(Bson...)} method to form a single filter representing the logical OR operation.
     * </p>
     *
     * @param filterQueries an array of {@link FilterQuery} objects representing the filter conditions
     * @return a BSON filter representing the logical OR operation of the provided filter conditions
     * @throws NullPointerException if {@code filterQueries} is {@code null}
     * @see FilterQuery
     * @see Filters#or(Bson...)
     * @see #getFilters(List, FilterQuery[])
     */
    protected @NotNull @Unmodifiable Bson where(@NotNull FilterQuery @NotNull [] filterQueries) {
        List<Bson> total = new ArrayList<>();
        getFilters(total, filterQueries);
        return Filters.or(total);
    }

    /**
     * Constructs a list of BSON filters based on the provided array of {@link FilterQuery} objects.
     * <p>
     * This method constructs a list of BSON filters based on the provided array of {@link FilterQuery} objects.
     * It iterates through each {@code filterQuery} in the array, checks the filter operator, and constructs
     * the appropriate BSON filter accordingly. If the filter operator is {@link FilterOperator#AND} or {@link FilterOperator#INIT},
     * it creates a filter using the {@link #createFilter(FilterQuery)} method and adds it to the temporary list {@code construct}.
     * When encountering other filter operators, it creates an 'AND' combination of the filters in the {@code construct} list
     * and adds it to the {@code total} list. Finally, it adds an 'AND' combination of the remaining filters in the {@code construct} list
     * to the {@code total} list.
     *
     * @param total the list of {@link Bson} filters to which the constructed filters will be added
     * @param filterQueries an array of {@link FilterQuery} objects representing the filter conditions
     * @throws NullPointerException if either {@code total} or {@code filterQueries} is {@code null}
     * @see FilterQuery
     * @see FilterOperator
     * @see #createFilter(FilterQuery)
     * @see Filters#and(Bson...)
     */
    private void getFilters(@NotNull List<Bson> total, @NotNull FilterQuery @NotNull [] filterQueries) {
        List<Bson> construct = new ArrayList<>();
        for (FilterQuery filterQuery : filterQueries) {
            if (filterQuery.getFilterOperator().equals(FilterOperator.AND) || filterQuery.getFilterOperator().equals(FilterOperator.INIT)) {
                construct.add(createFilter(filterQuery));
                continue;
            }
            total.add(Filters.and(List.copyOf(construct)));

            construct.clear();
            construct.add(createFilter(filterQuery));
        }
        total.add(Filters.and(construct));
    }

    /**
     * Creates a BSON filter based on the provided {@link FilterQuery}.
     * <p>
     * This method generates a BSON filter based on the provided {@link FilterQuery}. It converts the value of the filter
     * to a string representation and constructs the appropriate BSON filter using the {@link Filters#regex(String, String)} method.
     * If the column of the filter is {@link Column#VALUE}, it serializes the value using the metadata and retrieves the second element
     * of the serialized array (assuming the first element is not required).
     *
     * @param filterQuery the {@link FilterQuery} object containing the column and value information for the filter
     * @return a BSON filter representing the provided {@link FilterQuery}
     * @throws NullPointerException if the {@code filterQuery} parameter is {@code null}
     * @see FilterQuery
     * @see Column
     * @see Filters#regex(String, String)
     * @see net.kissenpvp.core.api.database.meta.Table#getColumn(Column)
     * @see net.kissenpvp.core.database.KissenBaseMeta#serialize(Object)
     */
    private @NotNull Bson createFilter(@NotNull FilterQuery filterQuery) {
        String value = filterQuery.getValue().toString();
        if (Objects.equals(filterQuery.getColumn(), Column.VALUE)) {
            value = getMeta().serialize(filterQuery.getValue())[1];
        }

        return Filters.regex(getMeta().getTable().getColumn(filterQuery.getColumn()), value);
    }
}
