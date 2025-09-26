package net.kissenpvp.api.base;


import net.kissenpvp.api.database.ConnectionProvider;
import net.kissenpvp.api.localization.GlobalLocaleRegistry;
import org.jspecify.annotations.NonNull;

import java.util.UUID;

public interface Kissen
{
    @NonNull ConnectionProvider connectionProvider();

    @NonNull GlobalLocaleRegistry localeRegistry();

    boolean started();

    @NonNull UUID serverUid();
}
