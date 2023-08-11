package net.kissenpvp.core.database;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kissenpvp.core.api.database.DataImplementation;
import net.kissenpvp.core.api.database.meta.BackendException;
import net.kissenpvp.core.api.database.meta.Meta;
import net.kissenpvp.core.api.database.queryapi.*;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.database.queryapi.KissenQuerySelect;
import net.kissenpvp.core.database.queryapi.KissenQueryUpdate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Getter
public abstract class KissenBaseMeta implements Meta {

    private static final String UNDEFINED = "_UNDEFINED_";
    private final String table, totalIDColumn, keyColumn, valueColumn;

    @Override
    public boolean metaContains(@NotNull String totalID, @NotNull String key) throws BackendException {
        return getString(totalID, key).isPresent();
    }

    @Override
    public void setString(@NotNull String key, @Nullable String value) throws BackendException {
        setString(UNDEFINED, key, value);
    }

    @Override
    public void setLong(@NotNull String totalID, @NotNull String key, long value) throws BackendException {
        setString(totalID, key, String.valueOf(value));
    }

    @Override
    public void setLong(@NotNull String key, long value) throws BackendException {
        setString(key, String.valueOf(value));
    }

    @Override
    public void setDouble(@NotNull String totalID, @NotNull String key, double value) throws BackendException {
        setString(totalID, key, String.valueOf(value));
    }

    @Override
    public void setDouble(@NotNull String key, double value) throws BackendException {
        setString(key, String.valueOf(value));
    }

    @Override
    public void setFloat(@NotNull String totalID, @NotNull String key, float value) throws BackendException {
        setString(totalID, key, String.valueOf(value));
    }

    @Override
    public void setFloat(@NotNull String key, float value) throws BackendException {
        setString(key, String.valueOf(value));
    }

    @Override
    public void setInt(@NotNull String totalID, @NotNull String key, int value) throws BackendException {
        setString(totalID, key, String.valueOf(value));
    }

    @Override
    public void setInt(@NotNull String key, int value) throws BackendException {
        setString(key, String.valueOf(value));
    }

    @Override
    public void setShort(@NotNull String totalID, @NotNull String key, short value) throws BackendException {
        setString(totalID, key, String.valueOf(value));
    }

    @Override
    public void setShort(@NotNull String key, short value) throws BackendException {
        setString(key, String.valueOf(value));
    }

    @Override
    public void setByte(@NotNull String totalID, @NotNull String key, byte value) throws BackendException {
        setString(totalID, key, String.valueOf(value));
    }

    @Override
    public void setByte(@NotNull String key, byte value) throws BackendException {
        setString(key, String.valueOf(value));
    }

    @Override
    public void setBoolean(@NotNull String totalID, @NotNull String key, boolean value) throws BackendException {
        setString(totalID, key, String.valueOf(value));
    }

    @Override
    public void setBoolean(@NotNull String key, boolean value) throws BackendException {
        setString(key, String.valueOf(value));
    }

    @Override
    public void setStringList(@NotNull String key, @Nullable List<String> value) throws BackendException {
        setStringList(UNDEFINED, key, value);
    }

    @Override
    public void setRecordList(@NotNull String totalID, @NotNull String key, @NotNull List<? extends Record> value) throws BackendException {
        setStringList(totalID, key, value.stream().map(record -> KissenCore.getInstance().getImplementation(DataImplementation.class).toJson(record))
                .toList());
    }

    @Override
    public void setRecordList(@NotNull String key, @NotNull List<? extends Record> value) throws BackendException {
        setRecordList(UNDEFINED, key, value);
    }

    @Override
    public <T extends Record> void setRecord(@NotNull String totalID, @NotNull String key, @NotNull T value) throws BackendException {
        setString(totalID, key, KissenCore.getInstance().getImplementation(DataImplementation.class).toJson(value));
    }

    @Override
    public <T extends Record> void setRecord(@NotNull String key, @NotNull T value) throws BackendException {
        setRecord(UNDEFINED, key, value);
    }

    @Override
    public void delete(@NotNull String totalID, @NotNull String key) throws BackendException {
        setString(totalID, key, null);
    }

    @Override
    public void delete(@NotNull String key) throws BackendException {
        delete(UNDEFINED, key);
    }

    @Override
    public @NotNull Optional<@Nullable String> getString(@NotNull String totalID, @NotNull String key) throws BackendException {
        for (String[] data : execute(getDefaultQuery(totalID, key))) {
            return Optional.ofNullable(data[0]);
        }
        return Optional.empty();
    }

    @Override
    public @NotNull Optional<@Nullable String> getString(@NotNull String key) throws BackendException {
        return getString(UNDEFINED, key);
    }

    @Override
    public @NotNull Optional<@Nullable Long> getLong(@NotNull String totalID, @NotNull String key) throws BackendException {
        return getString(totalID, key).map(Long::parseLong);
    }

    @Override
    public @NotNull Optional<@Nullable Long> getLong(@NotNull String key) throws BackendException {
        return getString(key).map(Long::parseLong);
    }

    @Override
    public @NotNull Optional<@Nullable Double> getDouble(@NotNull String totalID, @NotNull String key) throws BackendException {
        return getString(totalID, key).map(Double::parseDouble);
    }

    @Override
    public @NotNull Optional<@Nullable Double> getDouble(@NotNull String key) throws BackendException {
        return getString(key).map(Double::parseDouble);
    }

    @Override
    public @NotNull Optional<@Nullable Float> getFloat(@NotNull String totalID, @NotNull String key) throws BackendException {
        return getString(totalID, key).map(Float::parseFloat);
    }

    @Override
    public @NotNull Optional<@Nullable Float> getFloat(@NotNull String key) throws BackendException {
        return getString(key).map(Float::parseFloat);
    }

    @Override
    public @NotNull Optional<@Nullable Integer> getInt(@NotNull String totalID, @NotNull String key) throws BackendException {
        return getString(totalID, key).map(Integer::parseInt);
    }

    @Override
    public @NotNull Optional<@Nullable Integer> getInt(@NotNull String key) throws BackendException {
        return getString(key).map(Integer::parseInt);
    }

    @Override
    public @NotNull Optional<@Nullable Short> getShort(@NotNull String totalID, @NotNull String key) throws BackendException {
        return getString(totalID, key).map(Short::parseShort);
    }

    @Override
    public @NotNull Optional<@Nullable Short> getShort(@NotNull String key) throws BackendException {
        return getString(key).map(Short::parseShort);
    }

    @Override
    public @NotNull Optional<@Nullable Byte> getByte(@NotNull String totalID, @NotNull String key) throws BackendException {
        return getString(totalID, key).map(Byte::parseByte);
    }

    @Override
    public @NotNull Optional<@Nullable Byte> getByte(@NotNull String key) throws BackendException {
        return getString(key).map(Byte::parseByte);
    }

    @Override
    public @NotNull Optional<@Nullable Boolean> getBoolean(@NotNull String totalID, @NotNull String key) throws BackendException {
        return getString(totalID, key).map(Boolean::parseBoolean);
    }

    @Override
    public @NotNull Optional<@Nullable Boolean> getBoolean(@NotNull String key) throws BackendException {
        return getString(key).map(Boolean::parseBoolean);
    }

    @Override
    public @NotNull <T extends Record> Optional<@Nullable T> getRecord(@NotNull String totalID, @NotNull String key, @NotNull Class<T> record) throws BackendException {
        for (String[] data : execute(getDefaultQuery(totalID, key))) {
            return Optional.of(KissenCore.getInstance().getImplementation(DataImplementation.class).fromJson(data[0], record));
        }
        return Optional.empty();
    }

    @Override
    public @NotNull <T extends Record> Optional<@Nullable T> getRecord(@NotNull String key, @NotNull Class<T> record) throws BackendException {
        return getRecord(UNDEFINED, key, record);
    }

    @Override
    public @NotNull QueryUpdate update(@NotNull QueryUpdateDirective... queryUpdateDirective) {
        return new KissenQueryUpdate(queryUpdateDirective);
    }

    @Override
    public @NotNull QuerySelect select(@NotNull Column... columns) {
        return new KissenQuerySelect(columns);
    }

    @Override
    public @NotNull @Unmodifiable <T extends Record> List<T> getRecordList(@NotNull String totalID, @NotNull String key, @NotNull Class<T> record) throws BackendException {
        return Arrays.stream(execute(getDefaultQuery(totalID, key))).flatMap(Arrays::stream).map(data -> KissenCore.getInstance().getImplementation(DataImplementation.class).fromJson(data, record)).toList();
    }

    @Override
    public @NotNull @Unmodifiable <T extends Record> List<T> getRecordList(@NotNull String key, @NotNull Class<T> record) throws BackendException {
        return getRecordList(UNDEFINED, key, record);
    }

    @Override
    public @NotNull @Unmodifiable List<String> getStringList(@NotNull String totalID, @NotNull String key) throws BackendException {
        return Arrays.stream(execute(getDefaultQuery(totalID, key))).flatMap(Arrays::stream).toList();
    }

    @Override
    public @NotNull @Unmodifiable List<String> getStringList(@NotNull String key) throws BackendException {
        return getStringList(UNDEFINED, key);
    }

    protected @NotNull String getColumn(@NotNull Column column) {
        return switch (column) {
            case TOTAL_ID -> getTotalIDColumn();
            case KEY -> getKeyColumn();
            case VALUE -> getValueColumn();
        };
    }

    protected @NotNull QuerySelect getDefaultQuery(@NotNull String totalID, @NotNull String key) {
        return select(Column.VALUE).appendFilter(Column.TOTAL_ID, totalID, FilterType.EQUALS).appendFilter(Column.KEY, key, FilterType.EQUALS);
    }
}
