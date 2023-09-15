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

package net.kissenpvp.core.database.file;

import net.kissenpvp.core.api.database.meta.BackendException;
import net.kissenpvp.core.api.database.meta.Meta;
import net.kissenpvp.core.api.database.queryapi.*;
import net.kissenpvp.core.database.KissenBaseMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KissenFileMeta extends KissenBaseMeta implements Meta {

    private final Path file;

    public KissenFileMeta(String file) {
        super(file, "", "", ""); //not a table but a file
        this.file = new File(file).toPath();
    }

    @Override
    public void purge(@NotNull String totalID) throws BackendException {
        writeFileContent(getFileData().stream().filter(line -> !line.startsWith(totalID)).toList());
    }

    @Override
    public void setString(@NotNull String totalID, @NotNull String key, @Nullable String value) throws BackendException {
        writeFileContent(Stream.concat(getFileData().stream().filter(data -> !data.startsWith(totalID + "." + key + ":")), Stream.of(totalID + "." + key + ":" + value)).toList());
    }

    @Override
    public void setStringList(@NotNull String totalID, @NotNull String key, @Nullable List<String> value) throws BackendException {
        if (value == null) {
            setString(totalID, key, null);
            return;
        }
        setString(totalID, key, value.toString());
    }

    @Override
    public @NotNull @Unmodifiable List<String> getStringList(@NotNull String totalID, @NotNull String key) throws BackendException {
        return getString(totalID, key).map(string -> string.substring(1, string.length() - 1)).map(string -> Arrays.asList(string.split(", "))).orElse(new ArrayList<>());
    }

    @Override
    public @NotNull String[][] execute(@NotNull QuerySelect querySelect) throws BackendException {
        List<String[]> selected = new ArrayList<>();
        for (String line : new ArrayList<>(getFilteredData(getFileData(), querySelect.getFilterQueries()))) {
            String[] result = new String[querySelect.getColumns().length];
            for (int i = 0; i < result.length; i++) {
                result[i] = getColumnValue(querySelect.getColumns()[i], line);
            }
            selected.add(result);
        }

        return selected.toArray(new String[0][]);
    }

    @Override
    public long execute(@NotNull QueryUpdate queryUpdate) throws BackendException {

        List<String> data = new ArrayList<>(getFileData());
        List<String> filtered = new ArrayList<>(getFilteredData(data, queryUpdate.getFilterQueries()));

        int count = 0;
        for (QueryUpdateDirective queryUpdateDirective : queryUpdate.getColumns()) {
            for (String current : filtered) {
                if (data.remove(current)) {
                    count++;
                }
                data.add(switch (queryUpdateDirective.column()) {
                    case TOTAL_ID ->
                            queryUpdateDirective.value() + "." + current.split("\\.")[1].split(":")[0] + ":" + current.split(":")[1];
                    case KEY ->
                            current.split("\\.")[0] + "." + queryUpdateDirective.value() + ": " + current.split(":")[1];
                    case VALUE ->
                            current.split("\\.")[0] + "." + current.split("\\.")[1].split(":")[0] + ":" + queryUpdateDirective.value();
                });
            }
        }

        writeFileContent(data);
        return count;
    }

    private @NotNull @Unmodifiable List<String> getFilteredData(@NotNull List<String> data, @NotNull FilterQuery @NotNull ... filterQueries) {
        List<String> filtered = new ArrayList<>(data);
        for (FilterQuery filterQuery : filterQueries) {
            filtered.removeAll(data.stream().filter(line ->
            {
                String value = getColumnValue(filterQuery.getColumn(), line);
                return !switch (filterQuery.getFilterType()) {
                    case EQUALS -> filterQuery.getValue().equals(value);
                    case START -> value.startsWith(filterQuery.getValue());
                    case END -> value.endsWith(filterQuery.getValue());
                };
            }).collect(Collectors.toSet()));
        }
        return Collections.unmodifiableList(filtered);
    }

    private @NotNull String getColumnValue(@NotNull Column column, @NotNull String line) {
        return switch (column) {
            case TOTAL_ID -> line.split("\\.")[0];
            case KEY -> line.split("\\.")[1].split(":")[0];
            case VALUE -> line.split(":")[1];
        };
    }

    private void writeFileContent(@NotNull List<String> data) throws BackendException {
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(file, StandardOpenOption.TRUNCATE_EXISTING)) {

            for (String line : data) {
                bufferedWriter.append(line);
                bufferedWriter.newLine();
            }

        } catch (IOException ioException) {
            throw new BackendException(ioException);
        }
    }

    private @NotNull @Unmodifiable List<String> getFileData() throws BackendException {
        List<String> data = new ArrayList<>();
        try (BufferedReader bufferedReader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                data.add(line);
            }
        } catch (IOException ioException) {
            throw new BackendException(ioException);
        }
        return Collections.unmodifiableList(data);
    }
}
