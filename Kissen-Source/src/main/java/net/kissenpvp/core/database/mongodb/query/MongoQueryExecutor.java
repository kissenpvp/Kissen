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

@AllArgsConstructor
@Getter
public class MongoQueryExecutor {

    private final @NotNull KissenMongoMeta meta;

    protected @NotNull @Unmodifiable Bson where(@NotNull FilterQuery @NotNull [] filterQueries) {
        List<Bson> total = new ArrayList<>();
        getFilters(total, filterQueries);
        return Filters.or(total);
    }

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

    private @NotNull Bson createFilter(@NotNull FilterQuery filterQuery) {
        String value = filterQuery.getValue().toString();
        if (Objects.equals(filterQuery.getColumn(), Column.VALUE)) {
            value = getMeta().serialize(filterQuery.getValue())[1]; //[0] is not required
        }

        return Filters.regex(getMeta().getColumn(filterQuery.getColumn()), value);
    }
}
