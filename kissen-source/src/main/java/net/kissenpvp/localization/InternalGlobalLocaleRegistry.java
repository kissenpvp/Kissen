package net.kissenpvp.localization;

import com.google.common.base.Preconditions;
import net.kissenpvp.api.base.KissenPlugin;
import net.kissenpvp.api.localization.GlobalLocaleRegistry;
import net.kissenpvp.api.localization.LocaleRepository;
import net.kyori.adventure.translation.Translator;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class InternalGlobalLocaleRegistry implements GlobalLocaleRegistry
{
    private static final Logger log = LoggerFactory.getLogger(InternalGlobalLocaleRegistry.class);
    private final Set<Locale> locales;
    private final Map<KissenPlugin, InternalLocaleRepository> repositories;

    private boolean initialized = false;

    public InternalGlobalLocaleRegistry()
    {
        locales = new HashSet<>();
        repositories = new HashMap<>();
    }

    public void initialize()
    {
        repositories.values().forEach(InternalLocaleRepository::load);
        initialized = true;
    }

    @Override
    public @NonNull LocaleRepository localeRepository(@NonNull KissenPlugin plugin) throws IllegalArgumentException
    {
        Preconditions.checkNotNull(plugin, "plugin cannot be null");

        if (!repositories.containsKey(plugin))
        {
            if (initialized)
            {
                String message = "The plugin %s has not registered a locale repository before initialization.";
                throw new IllegalArgumentException(String.format(message, plugin));
            }
            register(plugin);
        }

        return repositories.get(plugin);
    }

    private void register(@NonNull KissenPlugin plugin) throws NullPointerException
    {
        Preconditions.checkNotNull(plugin, "plugin cannot be null");

        repositories.put(plugin, new InternalLocaleRepository()
        {
            @Override protected @NonNull Optional<Locale> locale(@NonNull String localeName)
            {
                Locale locale = Translator.parseLocale(localeName);
                if (Objects.nonNull(locale))
                {
                    locales.add(locale);
                }
                return Optional.ofNullable(locale);
            }

            @Override public @NonNull KissenPlugin plugin()
            {
                return plugin;
            }
        });
    }
}
