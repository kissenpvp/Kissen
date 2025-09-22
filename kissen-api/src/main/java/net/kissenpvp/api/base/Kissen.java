package net.kissenpvp.api.base;


import net.kissenpvp.api.database.ConnectionProvider;
import net.kissenpvp.api.localization.GlobalLocaleRegistry;
import org.jspecify.annotations.NonNull;

public interface Kissen
{
    @NonNull ConnectionProvider connectionProvider();

    @NonNull GlobalLocaleRegistry localeRegistry();

    boolean started();
}
