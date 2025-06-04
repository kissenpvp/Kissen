package net.kissenpvp.localization;

import com.google.gson.*;
import net.kissenpvp.api.localization.LocaleRepository;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.translation.TranslationStore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Rerepresenting of an internal repository for locales and their respective
 * message translations. It provides mechanisms for loading, registering, and processing locale-specific
 * message formats while leveraging stored localization files.
 * <p>
 * This class interacts with localization files stored in a designated directory associated with the plugin's
 * data folder. The files are assumed to be in JSON format, containing key-value pairs where keys represent
 * message identifiers and values are messages formatted for a specific locale.
 *
 * @author Ivo Quiring
 */
public abstract class InternalLocaleRepository implements LocaleRepository
{
    private static final Logger log = LoggerFactory.getLogger(InternalLocaleRepository.class);

    private final Map<String, MessageFormat> defaultMessages;

    private final Set<Locale> locallyKnown;
    private TranslationStore<MessageFormat> translationStore;

    /**
     * Constructs a new instance of the {@code InternalLocaleRepository} class.
     * <p>
     * This constructor initializes the internal data structures used to manage
     * localization data. It sets up a {@code HashSet} to store locales that
     * are locally known and a {@code HashMap} to store default messages
     * associated with localization keys.
     */
    public InternalLocaleRepository()
    {
        locallyKnown = new HashSet<>();
        defaultMessages = new HashMap<>();
    }

    /**
     * Creates a {@link Collector} that transforms a stream of {@link Map.Entry} objects
     * into a {@link Map} where each entry consists of the original key and a {@link MessageFormat}
     * object created from the string representation of the corresponding value.
     *
     * @return a collector that maps string keys to {@link MessageFormat} instances based on their
     * string representations in the input entries
     */
    private static @NotNull Collector<Map.Entry<String, JsonElement>, ?, Map<String, MessageFormat>> jsonCollector()
    {
        return Collectors.toMap(Map.Entry::getKey, entry -> new MessageFormat(entry.getValue().getAsString()));
    }

    @Override public @Nullable MessageFormat register(@NotNull String key, @NotNull MessageFormat format)
    {
        return defaultMessages.put(key, format);
    }

    @Override public @NotNull Key key()
    {
        return Key.key("kissen", plugin().getName());
    }

    /**
     * Loads localization files from the designated language directory within the plugin's data folder.
     * <p>
     * This method initializes the translation store using the plugin's key and attempts to load all
     * localization files found in a directory named "lang" located within the plugin's data folder.
     * If the "lang" directory does not exist, it attempts to create it. Any issues with directory
     * creation, incorrect directory structure, or file permissions are logged as warnings.
     * <p>
     * The method processes all JSON files in the "lang" directory that have a .json file extension
     * and passes them to {@link #loadFile(File)} for further handling.
     * <p>
     * This method ensures only valid localization files following the expected format are processed.
     */
    public void load()
    {
        if (defaultMessages.isEmpty())
        {
            log.info("Plugin {} has got no translations registered. Skip loading files...", plugin().getName());
        }

        translationStore = TranslationStore.messageFormat(key());

        File file = new File(plugin().getDataFolder(), "lang");
        String absolutePath = file.getAbsolutePath();

        if (!file.exists() && !file.mkdir())
        {
            log.warn("Failed to create language directory at {}. Please check file permissions.", absolutePath);
            return;
        }

        if (!file.isDirectory())
        {
            log.warn(
                    "Expected {} to be a directory but found a file. Please remove the file and try again.",
                    absolutePath
            );
            return;
        }

        File[] localeFiles = file.listFiles((dir, name) -> name.toLowerCase(Locale.ROOT).endsWith(".json"));
        if (Objects.isNull(localeFiles))
        {
            log.warn("Unable to list JSON files in directory {}. Please check directory permissions.", absolutePath);
            return;
        }

        for (File localeFile : localeFiles)
        {
            loadFile(localeFile);
        }
    }

    /**
     * Processes a file containing localization data by determining its associated locale,
     * registering translations, and adding the locale to the set of known locales.
     * This method expects the file name to determine the locale, and it should follow
     * a recognized format to properly resolve the locale.
     *
     * @param file the localization file to be processed must not be null and should follow the expected naming conventions
     */
    private void loadFile(@NotNull File file)
    {
        String fileName = file.getName();
        String localeName = fileName.substring(0, fileName.length() - 5);

        Optional<Locale> optionalLocale = locale(localeName);

        if (optionalLocale.isEmpty())
        {
            log.warn(
                    "Could not determine the locale for file {}. Please ensure the filename follows the correct format.",
                    fileName
            );
            return;
        }

        Locale locale = optionalLocale.get();
        translationStore.registerAll(locale, readFile(file));
        locallyKnown.add(locale); // can't be duplicated because locallyKnown is a set
    }

    /**
     * Reads data from a JSON file and merges it with a map of default messages.
     * If the file contains certain keys missing in the default messages, they are excluded from the processed output.
     * The resulting map links strings to {@link MessageFormat} objects.
     *
     * @param file the JSON file to read and process; must not be null
     * @return a map where each key is a string from the processed JSON, and each value
     * is a {@link MessageFormat} derived from the data
     */
    private @NotNull Map<String, MessageFormat> readFile(@NotNull File file)
    {
        JsonObject object = readJson(file).orElse(new JsonObject());

        for (Map.Entry<String, MessageFormat> entry : defaultMessages.entrySet())
        {
            if (object.has(entry.getKey()))
            {
                continue;
            }

            object.addProperty(entry.getKey(), entry.getValue().toString());
        }

        return object.asMap().entrySet().stream().collect(jsonCollector());
    }

    /**
     * Reads and parses a JSON file into a {@link JsonObject}.
     * If an error occurs during file reading or parsing, an empty {@code Optional} is returned.
     *
     * @param file the JSON file to be read and parsed must not be null
     * @return an {@code Optional} containing the parsed {@link JsonObject} if successful,
     * or an empty {@code Optional} if an error occurs or the input is invalid
     */
    private @NotNull Optional<JsonObject> readJson(@NotNull File file)
    {
        try (FileReader fileReader = new FileReader(file, StandardCharsets.UTF_8))
        {
            try (BufferedReader bufferedReader = new BufferedReader(fileReader))
            {
                return Optional.ofNullable(JsonParser.parseReader(bufferedReader).getAsJsonObject());
            }
        } catch (IOException | JsonIOException | JsonSyntaxException exception)
        {
            log.warn("Failed to parse JSON file {}: {}", file.getName(), exception.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Attempts to resolve a {@link Locale} object based on the provided locale name using {@link net.kyori.adventure.translation.Translator#parseLocale}.
     * If the parsing is successful, the locale is automatically registered in the global locale set.
     *
     * @param localeName the name of the locale to be parsed (e.g., "en", "en-US", "de-DE")
     * @return an {@code Optional} containing the resolved {@link Locale} if successful, or an empty {@code Optional} if the locale name is invalid or null
     * @see net.kyori.adventure.translation.Translator#parseLocale(String)
     */

    protected abstract @NotNull Optional<Locale> locale(@NotNull String localeName);
}