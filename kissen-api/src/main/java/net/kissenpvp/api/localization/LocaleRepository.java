package net.kissenpvp.api.localization;

import net.kissenpvp.api.base.KissenPlugin;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;

public interface LocaleRepository
{
    @NotNull KissenPlugin plugin();

    @Nullable MessageFormat register(@NotNull String key, @NotNull MessageFormat format);

    @NotNull Key key();

}
