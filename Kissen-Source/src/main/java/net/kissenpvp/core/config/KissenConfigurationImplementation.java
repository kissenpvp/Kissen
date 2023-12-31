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

package net.kissenpvp.core.config;

import lombok.Getter;
import net.kissenpvp.core.api.base.ServerName;
import net.kissenpvp.core.api.base.plugin.KissenPlugin;
import net.kissenpvp.core.api.config.ConfigurationImplementation;
import net.kissenpvp.core.api.config.InvalidValueException;
import net.kissenpvp.core.api.config.Option;
import net.kissenpvp.core.api.config.UnregisteredException;
import net.kissenpvp.core.api.message.settings.DefaultLanguage;
import net.kissenpvp.core.api.reflection.ReflectionImplementation;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.database.settings.DatabaseDNS;
import net.kissenpvp.core.database.settings.KeepSQLiteFile;
import net.kissenpvp.core.message.localization.settings.HighlightVariables;
import net.kissenpvp.core.message.localization.settings.InsertMissingTranslation;
import net.kissenpvp.core.message.settings.*;
import net.kissenpvp.core.message.settings.prefix.*;
import net.kissenpvp.core.networking.socket.ssl.settings.*;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Stream;


@Getter
public class KissenConfigurationImplementation implements ConfigurationImplementation {

    private final Set<Option<?>> internalOptions;
    private final Map<KissenPlugin, Set<Option<?>>> kissenPluginSetMap;

    public KissenConfigurationImplementation() {
        kissenPluginSetMap = new HashMap<>();
        internalOptions = new HashSet<>();
        internalOptions.add(new ClientCertificateLocation());
        internalOptions.add(new ClientCertificatePassword());
        internalOptions.add(new DatabaseDNS());
        internalOptions.add(new DefaultColor());
        internalOptions.add(new DefaultDisabledColor());
        internalOptions.add(new DefaultEnabledColor());
        internalOptions.add(new DefaultLanguage());
        internalOptions.add(new DefaultPrimaryColor());
        internalOptions.add(new DefaultSecondaryColor());
        internalOptions.add(new EnableSSLEncryption());
        internalOptions.add(new HighlightVariables());
        internalOptions.add(new InsertMissingTranslation());
        internalOptions.add(new KeepSQLiteFile());
        internalOptions.add(new PrefixError());
        internalOptions.add(new PrefixEvent());
        internalOptions.add(new PrefixNormal());
        internalOptions.add(new PrefixSupport());
        internalOptions.add(new PrefixTeam());
        internalOptions.add(new ServerCertificateLocation());
        internalOptions.add(new ServerCertificatePassword());
        internalOptions.add(new ServerName());
    }

    @Override
    public @NotNull <T> Option<T> getOption(@NotNull Class<? extends Option<T>> clazz) {
        return Stream.concat(internalOptions.stream(), kissenPluginSetMap.values().stream())
                .filter(setting -> setting.getClass().getName().equals(clazz.getName()))
                .findFirst()
                .map(setting -> (Option<T>) setting)
                .orElseThrow(UnregisteredException::new);
    }

    public void registerOption(@NotNull Class<? extends Option<?>> option) {
        internalOptions.add((Option<?>) KissenCore.getInstance()
                .getImplementation(ReflectionImplementation.class)
                .loadClass(option)
                .newInstance());
    }

    @Override
    public void registerOption(@NotNull KissenPlugin kissenPlugin, @NotNull Class<? extends Option<?>> option) {
        if (!kissenPluginSetMap.containsKey(kissenPlugin)) {
            kissenPluginSetMap.put(kissenPlugin, new HashSet<>());
        }
        Option<?> optionInstance = (Option<?>) KissenCore.getInstance()
                .getImplementation(ReflectionImplementation.class)
                .loadClass(option)
                .newInstance();
        kissenPluginSetMap.get(kissenPlugin).add(optionInstance);
    }

    public void loadInternalConfiguration(@NotNull File file) throws IOException {
        if (!file.exists()) {
            if (!file.createNewFile()) {
                throw new IOException("Could not create config file!");
            }
        }

        Class<KissenConfigurationImplementation> clazz = KissenConfigurationImplementation.class;
        KissenCore instance = KissenCore.getInstance();

        KissenConfigurationImplementation kissenConfigurationImplementation = instance.getImplementation(clazz);
        write(file, internalOptions.stream().sorted((o1, o2) -> CharSequence.compare(o1.getGroup(), o2.getGroup())).toList());
    }

    public void loadPlugin(@NotNull KissenPlugin plugin) {
        if (!kissenPluginSetMap.containsKey(plugin)) {
            return;
        }

        write(new File(plugin.getDataFolder() + "/config.yml"), kissenPluginSetMap.get(plugin)
                .stream()
                .sorted((o1, o2) -> CharSequence.compare(o1.getGroup(), o2.getGroup()))
                .toList());
    }

    private void write(@NotNull File file, List<Option<?>> options) {

        Map<String, String> fileData = new HashMap<>();

        if (file.exists()) {
            try (BufferedReader bufferedReader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    if (!line.startsWith("#") && line.contains("=")) {
                        String key = line.substring(0, line.indexOf('='));
                        String value = line.substring(line.indexOf('=') + 1);
                        fileData.put(key, value);
                    }
                }
            } catch (IOException ioException) {
                KissenCore.getInstance()
                        .getLogger()
                        .error("The existing config file '{}' could not be read and will be reverted to default.", file.getAbsolutePath(), ioException);
            }
        }

        final Map<String, List<Option<?>>> sort = new TreeMap<>(CharSequence::compare);
        options.forEach(option -> {
            if (!sort.containsKey(option.getGroup().toLowerCase(Locale.ROOT))) {
                sort.put(option.getGroup().toLowerCase(Locale.ROOT), new ArrayList<>());
            }
            sort.get(option.getGroup().toLowerCase(Locale.ROOT)).add(option);
        });

        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING)) {
            bufferedWriter.append("""
                    # # # # # # # # # # # # # # # # # # # # # # # # # #
                    #                                                 #
                    #   This Config File was created automatically    #
                    #                                                 #
                    #            Incorrect & manual changes           #
                    #       are not corrected by the programmer.      #
                    #                                                 #
                    #         Thank you for using the plugin!         #
                    #                                                 #
                    # # # # # # # # # # # # # # # # # # # # # # # # # #""");
            bufferedWriter.newLine();

            for (Map.Entry<String, List<Option<?>>> entry : sort.entrySet()) {
                bufferedWriter.newLine();
                bufferedWriter.append("## ").append(entry.getKey().toUpperCase(Locale.ROOT)).append(" ##");
                bufferedWriter.newLine();
                entry.getValue().sort(Comparator.comparingInt(Option::getPriority));
                for (Option<?> currentOption : entry.getValue()) {
                    loadSetting(bufferedWriter, fileData, currentOption);
                }
            }
        } catch (IOException ioException) {
            KissenCore.getInstance()
                    .getLogger()
                    .error("The config file '{}' could not be written, please shut down the server to prevent any further damages to the systems config.", file.getAbsolutePath(), ioException);
        }
    }

    private <T> void loadSetting(@NotNull BufferedWriter bufferedWriter, @NotNull Map<String, String> fileData, @NotNull Option<T> entry) throws IOException {
        bufferedWriter.append(!entry.getDescription().startsWith("\n") ? "#" : "")
                .append(entry.getDescription().replace("\n", "\n#"));
        bufferedWriter.newLine();

        try {
            if (fileData.containsKey(entry.getCode())) {
                entry.setValue(entry.deserialize(fileData.get(entry.getCode())));
            } else {
                throw new InvalidValueException(null, entry.getCode());
            }
        } catch (InvalidValueException invalidValueException) {
            KissenCore.getInstance()
                    .getLogger()
                    .warn("{} It will be reverted to the default value '{}'.", invalidValueException.getMessage(), entry.getDefault());
            entry.setValue(entry.getDefault());
        }

        bufferedWriter.append(entry.getCode())
                .append("=")
                .append(Optional.ofNullable(fileData.get(entry.getCode())).orElse(entry.serialize(entry.getDefault())));
        bufferedWriter.newLine();
    }
}
