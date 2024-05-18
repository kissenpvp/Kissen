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
import lombok.extern.slf4j.Slf4j;
import net.kissenpvp.core.api.base.ServerName;
import net.kissenpvp.core.api.base.plugin.KissenPlugin;
import net.kissenpvp.core.api.config.ConfigurationImplementation;
import net.kissenpvp.core.api.config.Option;
import net.kissenpvp.core.api.config.UnregisteredException;
import net.kissenpvp.core.base.KissenImplementation;
import net.kissenpvp.core.database.settings.DatabaseDns;
import net.kissenpvp.core.database.settings.KeepSqliteFile;
import net.kissenpvp.core.message.localization.settings.HighlightVariables;
import net.kissenpvp.core.message.localization.settings.InsertMissingTranslation;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;


@Getter @Slf4j(topic = "Kissen")
public abstract class KissenConfigurationImplementation implements ConfigurationImplementation, KissenImplementation {

    private final Set<Option<?, ?>> internalSettings;
    private final Map<KissenPlugin, Set<Option<?, ?>>> pluginSettings;

    public KissenConfigurationImplementation() {
        pluginSettings = new HashMap<>();
        internalSettings = new HashSet<>();

        registerInternalSetting(new DatabaseDns());
        registerInternalSetting(new HighlightVariables());
        registerInternalSetting(new InsertMissingTranslation());
        registerInternalSetting(new ServerName());
        registerInternalSetting(new KeepSqliteFile());
//        registerInternalSetting(new EnableSSLEncryption());
//        registerInternalSetting(new ServerCertificateLocation());
//        registerInternalSetting(new ServerCertificatePassword());
//        registerInternalSetting(new ClientCertificateLocation());
//        registerInternalSetting(new ClientCertificatePassword());
    }

    @Override
    public void postEnable(@NotNull KissenPlugin kissenPlugin) {
        loadPlugin(kissenPlugin);
    }

    protected static <T> void setDefault(@NotNull Option<T, ?> option) {
        option.setValue(option.getDefault());
    }

    protected static <T, S> void setValue(@NotNull Option<T, S> option, @NotNull Object value)
    {
        option.setValue(option.deserialize((S) value));
    }

    protected static <T, S> @NotNull T getDeserialized(@NotNull Option<T, S> option, @NotNull S value) {
        return option.deserialize(value);
    }

    protected static <T, S> @NotNull S getSerialized(@NotNull Option<T, S> option) {
        return option.serialize(option.getValue());
    }

    public void loadInternalConfiguration() throws IOException {
        File file = getFile();

        List<Option<?, ?>> sorted = getSorted(internalSettings);

        load(file, sorted);
        write(getFile(), sorted);
    }

    @Override
    public @NotNull <T> Option<T, ?> getOption(@NotNull Class<? extends Option<T, ?>> clazz) {
        return Stream.concat(internalSettings.stream(), pluginSettings.values().stream().flatMap(Collection::stream)).filter(setting -> setting.getClass().equals(clazz)).findFirst().map(setting -> (Option<T, ?>) setting)
                .orElseThrow(UnregisteredException::new);
    }

    public void registerInternalSetting(@NotNull Option<?, ?> option) {
        internalSettings.add(option);
    }

    @Override
    public void registerSetting(@NotNull KissenPlugin kissenPlugin, @NotNull Option<?, ?> option) {
        pluginSettings.computeIfAbsent(kissenPlugin, plugin -> new HashSet<>()).add(option);
    }

    /**
     * Loads configuration settings for the specified plugin.
     *
     * <p>This method loads configuration settings for the specified {@code plugin}. If the plugin settings are not available,
     * the method returns without performing any action. Otherwise, it logs a message indicating that configuration is being
     * loaded for the plugin and proceeds to load and write the settings.</p>
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * {@code
     * KissenPlugin plugin = // obtain KissenPlugin instance
     * loadPlugin(plugin);
     * }
     * </pre>
     *
     * @param plugin the KissenPlugin for which configuration settings are to be loaded
     * @throws NullPointerException if the specified plugin is `null`
     */
    public void loadPlugin(@NotNull KissenPlugin plugin) {
        if (!getPluginSettings().containsKey(plugin)) {
            return;
        }
        log.info("Load configuration for plugin {}.", plugin.getName());
        loadPlugin(plugin, getSorted(plugin));
    }

    /**
     * Loads configuration settings for the specified plugin using the provided settings list.
     *
     * <p>This method is called by the {@link #loadPlugin(KissenPlugin)} method to load configuration settings for the specified
     * {@code plugin}. It attempts to load settings from a file associated with the plugin, and if successful, writes the settings
     * back to the file. Any {@link IOException} encountered during this process is logged.</p>
     *
     * @param plugin   the KissenPlugin for which configuration settings are to be loaded
     * @param settings the list of settings to be loaded
     * @throws NullPointerException if the specified plugin or settings list is `null`
     */
    private void loadPlugin(@NotNull KissenPlugin plugin, List<Option<?, ?>> settings) {
        try {
            File file = getFile(plugin);
            load(file, settings);
            write(file, settings);
        } catch (IOException ioException) {
            log.error("An exception occurred when loading config file from plugin {}.", plugin, ioException);
        }
    }

    /**
     * Retrieves the plugin settings sorted by group.
     *
     * <p>This method retrieves the plugin settings associated with the specified {@code plugin} and sorts them by group.
     * The settings are sorted using a comparator that compares the group names of the settings. The sorted list of settings
     * is then returned.</p>
     *
     * @param plugin the KissenPlugin for which the settings are to be retrieved and sorted
     * @return a sorted list of plugin settings
     * @throws NullPointerException if the specified plugin is `null`
     */
    private @NotNull List<Option<?, ?>> getSorted(@NotNull KissenPlugin plugin) {
        return getSorted(getPluginSettings().get(plugin));
    }

    private @NotNull List<Option<?, ?>> getSorted(@NotNull Set<Option<?, ?>> options) {
        Comparator<Option<?, ?>> compareGroup = (o1, o2) -> CharSequence.compare(o1.getGroup(), o2.getGroup());
        return options.stream().sorted(compareGroup).toList();
    }

    /**
     * Generates a path string for the specified option.
     *
     * <p>This method generates a path string for the specified {@code option} by joining its group name (converted to lowercase)
     * and its code with a dot separator.</p>
     *
     * @param option the option for which the path string is to be generated
     * @return the path string for the specified option
     * @throws NullPointerException if the specified option is `null`
     */
    protected @NotNull String getPath(@NotNull Option<?, ?> option) {
        return String.join(".", option.getGroup().toLowerCase(), option.getCode());
    }

    /**
     * Retrieves the file associated with the internal configuration.
     *
     * <p>This method retrieves the file associated with the internal configuration.</p>
     *
     * <p>Subclasses must implement this method to provide the file associated with the internal configuration.</p>
     *
     * <p><b>Note:</b> This method is declared as {@code protected} to enforce its usage by subclasses only.</p>
     *
     * @return the file associated with the internal configuration
     * @throws NullPointerException if the file is {@code null}
     */
    protected abstract @NotNull File getFile();

    /**
     * Retrieves the file associated with the specified KissenPlugin.
     *
     * <p>This method retrieves the file associated with the specified {@code plugin}.</p>
     *
     * <p>Subclasses must implement this method to provide the logic for retrieving the file associated with a plugin.</p>
     *
     * <p>The returned file may represent a configuration file.</p>
     *
     * <p><b>Note:</b> This method is declared as {@code protected} to enforce its usage by subclasses only.</p>
     *
     * @param plugin the KissenPlugin for which to retrieve the file
     * @return the file associated with the specified plugin
     * @throws NullPointerException if the specified plugin is {@code null}
     */
    protected abstract @NotNull File getFile(@NotNull KissenPlugin plugin);

    /**
     * Loads configuration settings from the specified file.
     *
     * <p>This method loads configuration settings from the specified {@code file}. It is intended to be implemented by subclasses
     * to provide the logic for loading configuration settings.</p>
     *
     * <p>Subclasses must implement this method to provide the loading logic for configuration settings. The implementation should
     * handle the process of reading configuration settings from the specified file and populating the provided list of options accordingly.</p>
     *
     * <p>For example, a subclass may use deserialization, parsing YML, TOML, or any other format to load configuration settings from the file.</p>
     *
     * <p><b>Note:</b> This method is declared as {@code protected} to enforce its usage by subclasses only.</p>
     *
     * @param file    the file from which to load configuration settings
     * @param options the list of options to be populated with loaded settings
     * @throws NullPointerException if the specified file or options list is {@code null}
     * @see Option
     */
    protected abstract void load(@NotNull File file, @NotNull List<Option<?, ?>> options);

    /**
     * Writes configuration settings to the specified file.
     *
     * <p>This method writes configuration settings to the specified {@code file}. It is intended to be implemented by subclasses
     * to provide the logic for writing configuration settings.</p>
     *
     * <p>Subclasses must implement this method to provide the writing logic for configuration settings. The implementation should
     * handle the process of writing the provided list of options to the specified file.</p>
     *
     * <p>For example, a subclass may use serialization, YML, TOML, or any other format to write the configuration settings to the file.</p>
     *
     * <p><b>Note:</b> This method is declared as {@code protected} to enforce its usage by subclasses only.</p>
     *
     * @param file    the file to which to write configuration settings
     * @param options the list of options to be written
     * @throws IOException           if an I/O error occurs while writing to the file
     * @throws NullPointerException if the specified file or options list is {@code null}
     * @see Option
     */
    protected abstract void write(@NotNull File file, @NotNull List<Option<?, ?>> options) throws IOException;
}
