package net.kissenpvp.core.database.mongodb;

import net.kissenpvp.core.api.database.meta.BackendException;
import net.kissenpvp.core.api.database.meta.ObjectMeta;
import net.kissenpvp.core.api.database.queryapi.Column;
import net.kissenpvp.core.api.database.queryapi.FilterType;
import net.kissenpvp.core.api.database.queryapi.QuerySelect;
import net.kissenpvp.core.api.database.savable.Savable;
import net.kissenpvp.core.api.database.savable.SavableMap;
import net.kissenpvp.core.database.savable.KissenSavableMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class KissenObjectMongoMeta extends KissenNativeMongoMeta implements ObjectMeta {
    public KissenObjectMongoMeta(String table) {
        super(table);
    }

    @Override
    public void add(@NotNull String id, @NotNull Map<String, String> data) throws BackendException {

        data.forEach((key, value) -> setString(id, key, value));
    }

    @Override
    public @NotNull Optional<@Nullable SavableMap> getData(@NotNull String totalId) throws BackendException {
        return Optional.ofNullable(processQuery(totalId, select(Column.KEY, Column.VALUE).appendFilter(Column.TOTAL_ID, totalId, FilterType.EQUALS)).get(totalId));
    }

    @Override
    public @Unmodifiable @NotNull <T extends Savable> Map<@NotNull String, @NotNull SavableMap> getData(@NotNull T savable) throws BackendException {
        return processQuery(select(Column.TOTAL_ID, Column.KEY, Column.VALUE).appendFilter(Column.TOTAL_ID, savable.getSaveID(), FilterType.START));
    }

    private @NotNull Map<String, SavableMap> processQuery(@NotNull QuerySelect querySelect) throws BackendException {
        Map<String, SavableMap> dataContainer = new HashMap<>();
        for (String[] current : execute(querySelect)) {
            insertData(dataContainer, current[0], current[1], current[2]);
        }
        return dataContainer;
    }

    private @NotNull Map<String, SavableMap> processQuery(@NotNull String totalID, @NotNull QuerySelect querySelect) throws BackendException {
        Map<String, SavableMap> dataContainer = new HashMap<>();
        for (String[] current : execute(querySelect)) {
            insertData(dataContainer, totalID, current[0], current[1]);
        }
        return dataContainer;
    }

    private void insertData(Map<String, SavableMap> dataContainer, @NotNull String totalID, String key, String value) {
        dataContainer.putIfAbsent(totalID, new KissenSavableMap(totalID, KissenObjectMongoMeta.this));
        SavableMap savableMap = dataContainer.get(totalID);
        if (key.startsWith("_")) {
            savableMap.putList(key.substring(1), Arrays.asList(value.substring(1, value.length() - 1).split(", ")));
        } else {
            savableMap.put(key, value);
        }
        dataContainer.remove(totalID);
        dataContainer.put(totalID, savableMap);
    }
}
