package net.kissenpvp.api.base;


import net.kissenpvp.api.localization.GlobalLocaleRegistry;
import org.jspecify.annotations.NonNull;

public interface Kissen
{
    @NonNull GlobalLocaleRegistry localeRegistry();

    boolean started();
}
