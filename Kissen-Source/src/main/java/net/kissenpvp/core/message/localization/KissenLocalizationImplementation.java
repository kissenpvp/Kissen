package net.kissenpvp.core.message.localization;

import com.google.gson.*;
import net.kissenpvp.core.api.base.plugin.KissenPlugin;
import net.kissenpvp.core.api.config.ConfigurationImplementation;
import net.kissenpvp.core.api.message.localization.LocalizationImplementation;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.base.KissenImplementation;
import net.kissenpvp.core.message.localization.settings.InsertMissingTranslation;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.KeyPattern;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationRegistry;
import net.kyori.adventure.translation.Translator;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The KissenLocalizationImplementation class is an implementation of both the LocalizationImplementation and KissenImplementation interfaces.
 * It provides the functionality for handling localization and translations within the kissen framework.
 * <p>
 * This class serves as the main localization manager for plugins and offers methods for loading, registering, and managing translation data.
 * It utilizes the TranslationRegistry class to store and organize translations for each plugin's locale.
 * <p>
 * The KissenLocalizationImplementation class implements the LocalizationImplementation interface,
 * which defines methods for localization support and accessing translations based on plugin and locale.
 * <p>
 * Additionally, the class implements the KissenImplementation interface, to interact with the plugin system from the specified system this is running on.
 * <p>
 * Plugins can use this class to manage their localization needs and handle translations for various locales.
 * The class facilitates loading language files, registering translations, and providing translation data to plugins when needed.
 * It is a central component in the kissen framework that helps make plugins more user-friendly by offering multilingual support.
 */
public class KissenLocalizationImplementation implements LocalizationImplementation, KissenImplementation {

    private final Set<LocaleData> localeDataSet;
    private final Set<Locale> globallyKnown;

    /**
     * Constructs a new instance of the KissenLocalizationImplementation class.
     * This constructor initializes the localeDataSet and globallyKnown data structures,
     * which are used to manage LocaleData and keep track of globally known locales, respectively.
     * <p>
     * The localeDataSet is initialized as a HashSet, and it is used to store LocaleData instances.
     * Each LocaleData contains information about a specific plugin's localization and translations.
     * <p>
     * The globallyKnown set is also initialized as a HashSet, and it keeps track of the Locale instances that are known globally.
     * These are the locales for which translations have been discovered and registered across all LocaleData instances.
     * <p>
     * This constructor is called when creating a new instance of the KissenLocalizationImplementation,
     * and it prepares the data structures to manage and handle translations and localization for plugins.
     */
    public KissenLocalizationImplementation() {
        this.localeDataSet = new HashSet<>();
        this.globallyKnown = new HashSet<>();
    }

    @Override
    public boolean preStart() {
        localeDataSet.add(new LocaleData("$system", new File("lang"), createTranslationRegistry("system", "systemtranslations")));
        return LocalizationImplementation.super.preStart();
    }

    @Override
    public void setupComplete() {
        try {
            loadFiles(getData("$system"));
        } catch (IOException ioException) {
            KissenCore.getInstance()
                    .getLogger()
                    .error("The system was unable to load its translation files.", ioException);
        }
    }

    @Override
    public void stop() {
        clearData();
        LocalizationImplementation.super.stop();
    }

    @Override
    public boolean prepareReload() {
        clearData();
        return LocalizationImplementation.super.prepareReload();
    }

    @Override
    public void load(@NotNull KissenPlugin kissenPlugin) {
        initializePlugin(kissenPlugin);
    }

    @Override
    public void postEnable(@NotNull KissenPlugin kissenPlugin) {
        try {
            loadFiles(getData(kissenPlugin.getName()));
        } catch (IOException ioException) {
            KissenCore.getInstance()
                    .getLogger()
                    .error("The system was unable to create or load the translation files for plugin '{}'.", kissenPlugin.getName(), ioException);
        }
    }

    @Override
    public @Unmodifiable @NotNull Set<Locale> getAvailableLocales(@NotNull KissenPlugin kissenPlugin) {
        return Collections.unmodifiableSet(getData(kissenPlugin.getName()).installed);
    }

    @Override
    public void register(@NotNull KissenPlugin kissenPlugin, @NotNull String key, @NotNull MessageFormat defaultMessage) {
        LocaleData localeData = getData(kissenPlugin.getName());
        localeData.data().put(key, defaultMessage);
    }

    @Override
    public @NotNull Locale getDefaultLocale() {
        return Locale.US; //I don't know, maybe this should be variable some day
    }

    @Override
    public @NotNull Locale getLocale(@NotNull String localeTag) {
        return getInternalLocale(localeTag).orElse(buildLocale(localeTag));
    }

    public @NotNull Optional<Locale> getInternalLocale(@NotNull String localeTag) {
        return globallyKnown.stream()
                .filter(locale ->
                        locale.toString().equalsIgnoreCase(localeTag))
                .findFirst();
    }

    @Override
    public @NotNull Component getLocaleName(@NotNull Locale locale) {
        return Component.text(locale.getDisplayName());
    }

    @Override
    public @NotNull Component translate(@NotNull KissenPlugin kissenPlugin, @NotNull String key, @NotNull Locale locale, @NotNull String... var) {
        return translate(kissenPlugin.getName(), key, locale, var);
    }

    @Override
    public @NotNull Component translate(@NotNull KissenPlugin kissenPlugin, @NotNull TranslatableComponent translatableComponent, @NotNull Locale locale) {
        return Objects.requireNonNull(checkKey(kissenPlugin.getName(), translatableComponent.key()).translate(translatableComponent, locale));
    }

    /**
     * Translates a given translation key to the specified locale and formats the translated text with the provided variables.
     * <p>
     * This method retrieves the TranslationRegistry associated with the "system" plugin identifier using the checkKey method,
     * and then invokes the translation method on the TranslationRegistry to get the translated text for the given key and locale.
     * The method then formats the translated text with the provided variables using the format method.
     *
     * @param key    The translation key to be translated. Must not be null.
     * @param locale The locale for which the translation is requested. Must not be null.
     * @param var    The variables to be used for formatting the translated text, if applicable.
     *               These variables will be substituted in place of placeholders in the translated text.
     * @return A Component representing the translated text with formatted variables, if any.
     * @throws IllegalArgumentException If the provided translation key is not known to the translation service and cannot be localized.
     * @throws NullPointerException     If either the key or locale parameter is null.
     */
    public @NotNull Component translate(@NotNull String key, @NotNull Locale locale, @NotNull String... var) {
        return translate("$system", key, locale, var);
    }

    /**
     * This method is responsible for translation based on provided namespace, key and locale, using MiniMessage's deserialization. It also formats the translated message if any placeholders are given.
     * <p>
     * The method does the following steps:
     * <ol>
     * <li>It calls <code>checkKey(namespace, key).translate(key, locale).format(var)</code> to get the translated message string based on the given key and locale, and also applies any placeholder if provided.</li>
     * <li>The resulting string is then deserialized using the MiniMessage's deserialization function into a Component.</li>
     * <li>If the resulting component is an instance of TranslatableComponent, which means the string starts with '<lang:', it gets the original key from the TranslatableComponent and creates a new TranslatableComponent along with the names of any placeholders as arguments.</li>
     * <li>If the component is not a TranslatableComponent, it directly returns the component, which represents the translated message.</li>
     * </ol>
     *
     * @param namespace A string representing the namespace of the key, which is used in <code>checkKey</code> method.
     * @param key       The translation key. This key is used to find the correct translated message in your localization file.
     * @param locale    The Locale object representing the language to translate the message into.
     * @param var       Optional var args of strings which represent placeholders to replace in the translated string.
     * @return A Component instance that represents the translated message, which may be a simple Component or a TranslatableComponent based on if the message string starts with '<lang:'.
     * @throws NullPointerException     If the <code>checkKey(namespace, key).translate(key, locale)</code> returns null.
     * @throws IllegalArgumentException or other RuntimeExceptions if the <code>checkKey(namespace, key).translate(key, locale)</code> returns an untranslated key or error occurs during the deserialization of MiniMessage.
     */
    private @NotNull Component translate(@NotNull String namespace, @NotNull String key, @NotNull Locale locale, @NotNull String... var) {
        Component component = MiniMessage.miniMessage()
                .deserialize(Objects.requireNonNull(checkKey(namespace, key).translate(key, locale)).format(var));
        if (component instanceof TranslatableComponent translatable) {
            return Component.translatable(translatable.key(), Arrays.stream(var)
                    .map(Component::text)
                    .toList());
        }
        return component;
    }

    /**
     * Translates a TranslatableComponent to the specified locale.
     * <p>
     * This method retrieves the TranslationRegistry associated with the "system" plugin identifier using the checkKey method,
     * and then invokes the translation method on the TranslationRegistry to get the translated text for the TranslatableComponent and locale.
     * If the TranslatableComponent contains additional placeholders, they will be substituted with values from the TranslatableComponent itself.
     *
     * @param translatableComponent The TranslatableComponent to be translated. Must not be null.
     * @param locale                The locale for which the translation is requested. Must not be null.
     * @return A Component representing the translated text of the TranslatableComponent.
     * @throws IllegalArgumentException If the provided translation key (from the TranslatableComponent) is not known to the translation service and cannot be localized.
     * @throws NullPointerException     If either the translatableComponent or locale parameter is null.
     */
    public @NotNull Component translate(@NotNull TranslatableComponent translatableComponent, @NotNull Locale locale) {
        return Objects.requireNonNull(checkKey("$system", translatableComponent.key()).translate(translatableComponent, locale));
    }

    /**
     * Registers a new translation entry with the provided key and default message.
     * This method retrieves the LocaleData associated with the "$system" plugin identifier using the getData method.
     * It then adds the provided translation key and default message to the LocaleData's translation data Map.
     * After that, it registers the translation entry in the associated TranslationRegistry with the default locale.
     *
     * @param key            The translation key to register. Must not be null.
     * @param defaultMessage The default message (MessageFormat) associated with the translation key. Must not be null.
     * @throws NullPointerException If either the key or defaultMessage parameter is null.
     */
    public void register(@NotNull String key, @NotNull MessageFormat defaultMessage) {
        LocaleData localeData = getData("$system");
        localeData.data().put(key, defaultMessage);
    }

    /**
     * Checks the availability of a translation key within the TranslationRegistry associated with the given plugin identifier (id).
     * This method retrieves the LocaleData associated with the provided plugin identifier using the getData method,
     * and then obtains the TranslationRegistry from the LocaleData.
     * <p>
     * The method checks if the provided translation key is present in the TranslationRegistry.
     * If the key is not found, the method throws an IllegalArgumentException with an informative error message.
     * The message indicates that the key is not known to the translation service and cannot be localized.
     *
     * @param id  The identifier of the plugin for which to check the translation key. Must not be null.
     * @param key The translation key to check for availability in the TranslationRegistry. Must not be null.
     * @return The TranslationRegistry associated with the provided plugin identifier (id).
     * @throws IllegalArgumentException If the provided translation key is not known to the translation service and cannot be localized.
     * @throws NullPointerException     If either the id or key parameter is null.
     */
    private @NotNull TranslationRegistry checkKey(@NotNull String id, @NotNull String key) {
        TranslationRegistry translationRegistry = getData(id).translationRegistry();
        if (!translationRegistry.contains(key)) {
            throw new IllegalArgumentException(String.format("Key %s is not known to the translation service and can not be localized therefore.", key));
        }
        return translationRegistry;
    }

    /**
     * Initializes the provided KissenPlugin by creating and adding a new {@link TranslationRegistry} to the {@code localeDataSet}.
     * The TranslationRegistry is associated with the plugin's namespace and "translations" as the value.
     *
     * @param kissenPlugin The KissenPlugin instance to be initialized. Must not be null.
     * @throws NullPointerException If the provided kissenPlugin is null.
     */
    private void initializePlugin(@NotNull KissenPlugin kissenPlugin) {
        @Subst("plugin") String namespace = kissenPlugin.getName().toLowerCase();
        localeDataSet.add(new LocaleData(kissenPlugin.getName(), new File(kissenPlugin.getDataFolder(), "lang"), createTranslationRegistry(namespace, "translations")));
    }

    /**
     * Creates a new {@link TranslationRegistry} based on the provided namespace and value.
     * The TranslationRegistry is initialized with the specified namespace and value as the key pattern.
     * Additionally, the method sets the default locale and adds the TranslationRegistry as a source to the GlobalTranslator.
     *
     * @param namespace The namespace to be used for the TranslationRegistry's key pattern. Must not be null.
     * @param value     The value to be used for the TranslationRegistry's key pattern. Must not be null.
     * @return The newly created TranslationRegistry instance with the specified key pattern and default locale set.
     * @throws NullPointerException If either the namespace or value parameter is null.
     */
    private @NotNull TranslationRegistry createTranslationRegistry(@KeyPattern.Namespace @NotNull String namespace, @KeyPattern.Value @NotNull String value) {
        TranslationRegistry translationRegistry = TranslationRegistry.create(Key.key(namespace, value));
        translationRegistry.defaultLocale(getDefaultLocale());
        GlobalTranslator.translator().addSource(translationRegistry);
        return translationRegistry;
    }

    /**
     * Clears the data associated with the TranslationRegistries and the global translation settings.
     * This method removes all the TranslationRegistries' sources from the GlobalTranslator,
     * clears the globallyKnown keys, and clears the localeDataSet.
     * After calling this method, all the existing translation data and settings will be removed,
     * and the TranslationRegistries will no longer be used for translation.
     */
    private void clearData() {
        for (LocaleData localeData : localeDataSet) {
            GlobalTranslator.translator().removeSource(localeData.translationRegistry);
        }
        globallyKnown.clear();
        localeDataSet.clear();
    }

    /**
     * Loads language files for the given LocaleData, which contains a TranslationRegistry associated with a KissenPlugin.
     * This method is responsible for reading and processing the language files from the "lang" directory within the plugin's data folder.
     * The method loads JSON files (with ".json" extension) from the "lang" directory, and for each file found, it extracts the JSON data,
     * registers the translation keys and their associated localized values into the TranslationRegistry.
     * Additionally, it checks if the default locale is installed for the LocaleData and writes the default locale file if not already installed.
     *
     * @param localeData The LocaleData containing the TranslationRegistry and KissenPlugin. Must not be null.
     * @throws IOException          If there is an error during file I/O operations, such as file not found or read/write errors.
     * @throws NullPointerException If the provided localeData is null.
     */
    private void loadFiles(@NotNull LocaleData localeData) throws IOException {
        if (!localeData.data().isEmpty()) {
            File directory = localeData.directory();
            if (!directory.exists()) {
                if (!directory.mkdirs()) {
                    throw new IOException("Could not create language directory.");
                }
            }

            Arrays.stream(Objects.requireNonNull(directory.listFiles(file -> file.isFile() && file.getName()
                    .toLowerCase()
                    .endsWith(".json"))
            )).forEach(file -> registerFile(localeData, file));

            if (!localeData.installed().contains(getDefaultLocale())) {
                writeDefaultLocaleFile(localeData, new File(directory, getDefaultLocale() + ".json"));
            }
        }
    }

    /**
     * Registers a language file represented by the given File for the specified LocaleData.
     * This method loads the JSON data from the provided File using the loadJsonFromFile method,
     * which may return an Optional containing the parsed JsonObject.
     * If the JsonObject is present, the method proceeds to register the translations for the specified LocaleData.
     * It calls the registerJson method, passing the LocaleData, JsonObject, and the locale name derived from the file name.
     *
     * @param localeData The LocaleData to which the translations from the file will be registered. Must not be null.
     * @param file       The File representing the language file to be registered. Must not be null.
     * @throws NullPointerException If either the localeData or file parameter is null.
     */
    private void registerFile(@NotNull LocaleData localeData, @NotNull File file) {
        loadJsonFromFile(file).ifPresent(jsonObject ->
                registerJson(localeData, jsonObject, file.getName()
                        .substring(0, file.getName().length() - 5)));
    }

    /**
     * Loads and parses a JSON file from the provided File object.
     * This method reads the content of the JSON file using FileReader with UTF-8 encoding,
     * and then parses it into a JsonObject using JsonParser.
     *
     * @param file The File object representing the JSON file to be loaded. Must not be null.
     * @return An Optional containing the parsed JsonObject if the file is successfully read and parsed,
     * or an empty Optional if there is an error during the process.
     */
    private @NotNull Optional<JsonObject> loadJsonFromFile(@NotNull File file) {
        try (FileReader fileReader = new FileReader(file, StandardCharsets.UTF_8)) {
            try (BufferedReader bufferedReader = new BufferedReader(fileReader)) {
                return Optional.ofNullable(JsonParser.parseReader(bufferedReader).getAsJsonObject());
            }
        } catch (IOException | JsonIOException | JsonSyntaxException exception) {
            KissenCore.getInstance()
                    .getLogger()
                    .error("An error occurred when reading language file '{}'", file.getAbsolutePath(), exception);
        }
        return Optional.empty();
    }

    /**
     * Registers translation keys and their associated MessageFormat patterns from the given JsonObject into the {@link TranslationRegistry} of the provided LocaleData.
     * <p>
     * This method iterates through the JSON object's key-value pairs and converts the values into MessageFormat objects.
     * It then registers the translation keys and their associated MessageFormat patterns into the TranslationRegistry.
     * <p>
     * If the provided locale is not an internal locale, the method attempts to parse it into a Locale object using {@link Translator#parseLocale(String)}.
     * If parsing is successful, the method adds the parsed locale to the {@code globallyKnown} set and marks it as installed in the provided LocaleData.
     *
     * @param localeData The LocaleData containing the TranslationRegistry and other locale-specific data. Must not be null.
     * @param jsonObject The JsonObject containing the translation keys and their associated patterns. Must not be null.
     * @param locale     The locale code for the translations in the JsonObject. Must not be null.
     * @throws NullPointerException If any of the provided parameters (localeData, jsonObject, locale) are null.
     */
    private void registerJson(@NotNull LocaleData localeData, @NotNull JsonObject jsonObject, @NotNull String locale) {
        Map<String, MessageFormat> stringStringMap = new HashMap<>();
        jsonObject.asMap().forEach((key, value) -> stringStringMap.put(key, new MessageFormat(value.getAsString())));

        AtomicBoolean rewrite = new AtomicBoolean(false);
        localeData.data()
                .keySet()
                .stream()
                .filter(translation -> !stringStringMap.containsKey(translation))
                .forEach(translation ->
                {
                    rewrite.set(true);
                    MessageFormat defaultMessage = localeData.data().get(translation);
                    KissenCore.getInstance()
                            .getLogger()
                            .warn("The translation '{}' was not found in the file for locale '{}' from plugin '{}'. It will be set to the default message: '{}'.", translation, locale, localeData.translationRegistry.name()
                                    .namespace(), defaultMessage.toPattern());
                    jsonObject.addProperty(translation, defaultMessage.toPattern());
                    stringStringMap.put(translation, defaultMessage);
                });
        if (rewrite.get() && KissenCore.getInstance()
                .getImplementation(ConfigurationImplementation.class)
                .getSetting(InsertMissingTranslation.class)) {
            try {
                writeJson(jsonObject, new File(localeData.directory(), locale + ".json"));
            } catch (IOException e) {
                KissenCore.getInstance().getLogger().info("");
            }
        }

        localeData.translationRegistry().registerAll(Objects.requireNonNull(getInternalLocale(locale).orElseGet(() ->
        {
            Locale parseLocale = buildLocale(locale);
            localeData.installed().add(parseLocale);
            KissenCore.getInstance()
                    .getLogger()
                    .info("Found and registered locale '{}' from '{}'.", parseLocale.getDisplayName(), localeData.translationRegistry.name());
            return parseLocale;
        })), stringStringMap);
    }

    private @NotNull Locale buildLocale(@NotNull String locale)
    {
        Locale parseLocale = Translator.parseLocale(locale.toLowerCase()); //convert I guess
        globallyKnown.add(parseLocale);
        assert parseLocale != null;
        return parseLocale;
    }

    /**
     * Writes the default locale translations to a JSON file in the specified File object.
     * <p>
     * This method converts the default locale data from the provided LocaleData into a JsonObject
     * with translation keys and their associated MessageFormat patterns.
     * It then registers the default locale translations using the registerJson method,
     * and finally, writes the JsonObject as JSON to the specified file.
     *
     * @param localeData The LocaleData containing the TranslationRegistry and default locale data. Must not be null.
     * @param file       The File object representing the JSON file to be written. Must not be null.
     * @throws IOException          If there is an error during file I/O operations, such as file not found or write errors.
     * @throws NullPointerException If either the localeData or file parameter is null.
     */
    private void writeDefaultLocaleFile(@NotNull LocaleData localeData, @NotNull File file) throws IOException {
        JsonObject jsonObject = new JsonObject();
        localeData.data().forEach((key, value) -> jsonObject.addProperty(key, value.toPattern()));
        registerJson(localeData, jsonObject.deepCopy(), file.getName().substring(0, file.getName().length() - 5));
        writeJson(jsonObject, file);
    }

    /**
     * Writes the given JsonObject as JSON to the specified File.
     * This method utilizes the Gson library to serialize the JsonObject into a JSON-formatted string
     * and writes it to the specified File using a BufferedWriter with UTF-8 encoding.
     *
     * @param jsonObject The JsonObject to be written as JSON. Must not be null.
     * @param file       The File object representing the destination file. Must not be null.
     * @throws IOException          If there is an error during file I/O operations, such as file not found or write errors.
     * @throws NullPointerException If either the jsonObject or file parameter is null.
     */
    private void writeJson(@NotNull JsonObject jsonObject, @NotNull File file) throws IOException {
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(gson.fromJson(jsonObject, TreeMap.class), bufferedWriter);
        }
    }

    /**
     * Retrieves the LocaleData associated with the given plugin identifier (id).
     * This method searches the localeDataSet to find the LocaleData instance that corresponds to the provided plugin identifier.
     * If a matching LocaleData is found, it is returned.
     * If no matching LocaleData is found, the method throws an IllegalStateException.
     *
     * @param id The identifier of the plugin for which to retrieve the LocaleData. Must not be null.
     * @return The LocaleData associated with the provided plugin identifier (id).
     * @throws IllegalStateException If no LocaleData is found for the provided plugin identifier in the localeDataSet.
     * @throws NullPointerException  If the provided id is null.
     */
    private @NotNull LocaleData getData(@NotNull String id) {
        return localeDataSet.stream()
                .filter(plugin -> plugin.name().equals(id))
                .findFirst()
                .orElseThrow(IllegalStateException::new);
    }

    /**
     * The LocaleData record represents a data structure that holds information related to localization and translations for a plugin.
     * It encapsulates the association between a plugin name, the directory for language files, a TranslationRegistry instance,
     * the translation data (key-value pairs), and a set of installed locales for the associated plugin.
     * <p>
     * This record has two constructors:
     * - The primary constructor accepts five parameters: name, directory, translationRegistry, data, and installed.
     * - The secondary constructor takes name, directory, and translationRegistry as parameters,
     * and it uses default values (empty HashMap and HashSet) for data and installed.
     * <p>
     * The primary constructor is used to create a LocaleData instance with the required information,
     * including the name of the associated plugin, the directory for language files, the TranslationRegistry,
     * translation data (data), and a set of installed locales (installed).
     * <p>
     * The data field is a Map that holds translation data, mapping translation keys to their associated MessageFormat patterns.
     * The installed field is a Set of Locales representing the installed locales for the associated plugin.
     * These are the locales for which translations have been discovered and registered for the plugin.
     * <p>
     * This record provides a convenient way to represent and manage localization data for a specific plugin,
     * making it easier to work with translation-related information in the KissenPlugin framework.
     *
     * @param name                The name of the associated plugin. Must not be null.
     * @param directory           The directory for language files within the plugin's data folder. Must not be null.
     * @param translationRegistry The TranslationRegistry instance containing the translations for the plugin. Must not be null.
     * @param data                A Map that holds the translation data, with translation keys as keys and MessageFormat patterns as values.
     *                            It maps a translation key to its associated localized MessageFormat pattern. Must not be null.
     * @param installed           A Set of Locales representing the installed locales for the associated plugin.
     *                            It keeps track of the locales for which translations have been installed. Must not be null.
     */
    private record LocaleData(@NotNull String name, @NotNull File directory,
                              @NotNull TranslationRegistry translationRegistry,
                              @NotNull Map<String, MessageFormat> data, Set<Locale> installed) {

        /**
         * Constructs a LocaleData instance with the required parameters.
         * The data and installed locales are initialized with an empty HashMap and HashSet, respectively.
         *
         * @param name                The name of the associated plugin. Must not be null.
         * @param directory           The directory for language files within the plugin's data folder. Must not be null.
         * @param translationRegistry The TranslationRegistry instance containing the translations for the plugin. Must not be null.
         */
        private LocaleData(@NotNull String name, @NotNull File directory, @NotNull TranslationRegistry translationRegistry) {
            this(name, directory, translationRegistry, new HashMap<>(), new HashSet<>());
        }
    }
}
