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
    private final Map<KissenPlugin, LocaleRepository> repositories;

    public InternalGlobalLocaleRegistry()
    {
        locales = new HashSet<>();
        repositories = new HashMap<>();
    }

    public void register(@NotNull KissenPlugin plugin)
    {
        repositories.put(plugin, new InternalLocaleRepository() {
            @Override protected @NotNull Optional<Locale> locale(@NotNull String localeName)
            {
                Locale locale = Translator.parseLocale(localeName);
                if(Objects.nonNull(locale))
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

    @Override public @NotNull LocaleRepository localeRepository(@NotNull KissenPlugin plugin)
    {
        // if(!repositories.containsKey(plugin))
        // {
        //     register(plugin);
        // }

        return repositories.get(plugin);
    }
}
