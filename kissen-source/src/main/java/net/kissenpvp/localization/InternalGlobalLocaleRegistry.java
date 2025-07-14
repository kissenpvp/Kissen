package net.kissenpvp.localization;

import net.kissenpvp.api.base.KissenPlugin;
import net.kissenpvp.api.localization.GlobalLocaleRegistry;
import net.kissenpvp.api.localization.LocaleRepository;
import net.kyori.adventure.translation.Translator;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class InternalGlobalLocaleRegistry implements GlobalLocaleRegistry
{
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

    public void register(@NotNull KissenPlugin plugin) throws NullPointerException
    {
        Objects.requireNonNull(plugin, "plugin cannot be null");

        repositories.put(plugin, new InternalLocaleRepository()
        {
            @Override protected @NotNull Optional<Locale> locale(@NotNull String localeName)
            {
                Locale locale = Translator.parseLocale(localeName);
                if (Objects.nonNull(locale))
                {
                    locales.add(locale);
                }
                return Optional.ofNullable(locale);
            }

            @Override public @NotNull KissenPlugin plugin()
            {
                return plugin;
            }
        });
    }

    @Override
    public @NotNull LocaleRepository localeRepository(@NotNull KissenPlugin plugin) throws NullPointerException, IllegalArgumentException
    {
        Objects.requireNonNull(plugin, "plugin cannot be null");

        if (!repositories.containsKey(plugin))
        {
            if (!initialized)
            {
                String message = "The plugin %s has not registered a locale repository.";
                throw new IllegalArgumentException(String.format(message, plugin));
            }
            register(plugin);
        }

        return repositories.get(plugin);
    }
}
