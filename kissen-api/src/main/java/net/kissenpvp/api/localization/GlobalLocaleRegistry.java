package net.kissenpvp.api.localization;

import net.kissenpvp.api.base.KissenPlugin;
import org.jetbrains.annotations.NotNull;

public interface GlobalLocaleRegistry
{
    @NotNull LocaleRepository localeRepository(@NotNull KissenPlugin plugin);
}
