package net.kissenpvp.api.base;


import net.kissenpvp.api.localization.GlobalLocaleRegistry;
import org.jetbrains.annotations.NotNull;

public interface Kissen
{
    @NotNull GlobalLocaleRegistry localeRegistry();

    boolean started();
}
