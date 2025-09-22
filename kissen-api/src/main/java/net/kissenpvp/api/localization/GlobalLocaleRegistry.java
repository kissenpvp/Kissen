package net.kissenpvp.api.localization;

import net.kissenpvp.api.base.KissenPlugin;
import org.jspecify.annotations.NonNull;

public interface GlobalLocaleRegistry
{
    /**
     * Provides access to a {@link LocaleRepository} specific to the given plugin.
     *
     * @param plugin the plugin for which the locale repository is required; must not be null
     * @return a {@link LocaleRepository} instance associated with the provided plugin; never null
     */
    @NonNull LocaleRepository localeRepository(@NonNull KissenPlugin plugin) throws NullPointerException,
            IllegalArgumentException;
}
