package net.kissenpvp.api.localization;

import net.kissenpvp.api.base.KissenPlugin;
import net.kyori.adventure.key.Key;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.text.MessageFormat;

public interface LocaleRepository
{
    @NonNull KissenPlugin plugin();

    @Nullable MessageFormat register(@NonNull String key, @NonNull MessageFormat format) throws NullPointerException;

    @NonNull Key key();

}
