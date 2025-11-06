package net.kissenpvp.api.base;


import net.kissenpvp.api.database.ConnectionProvider;
import net.kissenpvp.api.localization.GlobalLocaleRegistry;
import org.jspecify.annotations.NonNull;

import java.util.UUID;

public interface Kissen {

    @NonNull ConnectionProvider connectionProvider();

    @NonNull GlobalLocaleRegistry localeRegistry();

    /**
     * Returns whether the Kissen instance has been fully initialized.
     *
     * @return true if the instance is initialized; false otherwise.
     */
    boolean started();

    /**
     * Returns a unique server UID.
     * <p>
     * This UID is generated during the server’s first startup using {@link UUID#randomUUID()}
     * and then stored in a file named <code>.server_uid</code>.
     * <p>
     * It can be used to associate data with a specific server instance when the same database
     * is shared by multiple servers.
     *
     * @return a randomly generated unique server UID
     */
    @NonNull UUID serverUid();
}
