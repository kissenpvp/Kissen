package net.kissenpvp.localization;

import com.google.common.base.Preconditions;
import com.google.gson.*;
import net.kissenpvp.api.localization.LocaleRepository;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationStore;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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
    private static @NonNull Collector<Map.Entry<String, JsonElement>, ?, Map<String, MessageFormat>> jsonCollector()
    {
        return Collectors.toMap(Map.Entry::getKey, entry -> new MessageFormat(entry.getValue().getAsString()));
    }

    @Override public @Nullable MessageFormat register(
            @NonNull String key,
            @NonNull MessageFormat format
    ) throws NullPointerException
    {
        Preconditions.checkNotNull(key, "The key cannot be null.");
        Preconditions.checkNotNull(format, "The message format cannot be null.");

        return defaultMessages.put(key, format);
    }

    @SuppressWarnings("PatternValidation") @Override public @NonNull Key key()
    {
        String pluginName = plugin().getName().toLowerCase(Locale.ROOT);
        return Key.key("kissen", pluginName);
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
            return;
        }

        translationStore = TranslationStore.messageFormat(key());
        GlobalTranslator.translator().addSource(translationStore);

        File file = new File(plugin().getDataFolder(), "lang");
        String absolutePath = file.getAbsolutePath();

        if(!file.exists() || !file.mkdirs())
        {
            insertDefault();
            return;
        }

        if (!file.isDirectory())
        {
            log.warn(
                    "Expected {} to be a directory but found a file. This prevents translation files from being loaded.",
                    absolutePath
            );
            insertDefault();
            return;
        }

        File[] localeFiles = file.listFiles((dir, name) -> name.toLowerCase(Locale.ROOT).endsWith(".json"));
        if (Objects.isNull(localeFiles))
        {
            log.warn("Unable to list JSON files in directory {}. Please check directory permissions.", absolutePath);
            return;
        }

        if(localeFiles.length == 0)
        {
            insertDefault();
            return;
        }

        for (File localeFile : localeFiles)
        {
            loadFile(localeFile);
        }
    }

    /**
     * Populates the translation store with the default values.
     * Is called when no files where found, the directory is non-existent, or the path is obstructed because of a file
     */
    private void insertDefault()
    {
        translationStore.registerAll(Locale.ENGLISH, defaultMessages);
        log.info("No locale files where detected. Adding default translations.");
    }

    /**
     * Processes a given localization file, attempts to determine its locale based on its filename,
     * and registers its contents into the translation store if the locale is valid.
     * Files must follow a specific naming convention to properly identify the locale.
     *
     * @param file the file to process; must not be null
     * @throws NullPointerException if the provided file is null
     */
    private void loadFile(@NonNull File file) throws NullPointerException
    {
        Preconditions.checkNotNull(file, "The file cannot be null.");

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
     * Reads a JSON file and integrates its content with default messages.
     * If the JSON file lacks any keys present in the default messages, those keys are added with their default values.
     *
     * @param file the JSON file to be processed; must not be null
     * @return a {@link Map} where the keys are strings and the values are {@link MessageFormat} objects
     * representing the fully populated message data
     * @throws NullPointerException if the provided file is null
     */
    private @NonNull Map<String, MessageFormat> readFile(@NonNull File file) throws NullPointerException
    {
        Preconditions.checkNotNull(file, "The file cannot be null.");

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
     * Reads a JSON file and parses its content into a {@link JsonObject}.
     * If the file cannot be read or parsed or is not valid JSON, an empty {@code Optional} is returned.
     *
     * @param file the JSON file to be read and parsed; must not be null
     * @return an {@code Optional} containing the parsed {@link JsonObject}, or an empty {@code Optional} if parsing
     * fails
     * @throws NullPointerException if the provided file is null
     */
    private @NonNull Optional<JsonObject> readJson(@NonNull File file) throws NullPointerException
    {
        Preconditions.checkNotNull(file, "The file cannot be null.");

        try (FileReader fileReader = new FileReader(file, StandardCharsets.UTF_8))
        {
            try (BufferedReader bufferedReader = new BufferedReader(fileReader))
            {
                return Optional.ofNullable(JsonParser.parseReader(bufferedReader).getAsJsonObject());
            }
        }
        catch (IOException | JsonIOException | JsonSyntaxException exception)
        {
            log.warn("Failed to parse JSON file {}: {}", file.getName(), exception.getMessage());
        }
        return Optional.empty();
    }


    /**
     * Attempts to retrieve a {@link Locale} object based on the provided locale name. This method
     * takes a string representation of the locale name and tries to parse it into a {@link Locale}.
     * If the parsing succeeds, the resulting {@link Locale} is wrapped in an {@link Optional}.
     * If the parsing fails, an empty {@link Optional} is returned.
     *
     * @param localeName the name of the locale to be retrieved; must not be null
     * @return an {@link Optional} containing the {@link Locale} if the parsing succeeds, or an empty
     * {@link Optional} if it fails
     * @throws NullPointerException if the provided locale name is null
     * @see net.kyori.adventure.translation.Translator#parseLocale(String)
     */
    protected abstract @NonNull Optional<Locale> locale(@NonNull String localeName) throws NullPointerException;
}