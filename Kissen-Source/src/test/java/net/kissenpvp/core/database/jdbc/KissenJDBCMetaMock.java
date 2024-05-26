package net.kissenpvp.core.database.jdbc;

import net.kissenpvp.core.api.base.plugin.KissenPlugin;
import net.kissenpvp.core.api.database.connection.PreparedStatementExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class KissenJDBCMetaMock extends KissenNativeJDBCMeta {

    public KissenJDBCMetaMock() {
        this(null);
    }

    public KissenJDBCMetaMock(@Nullable KissenPlugin plugin) {
        super(new TableMock(), plugin);
    }

    @Override
    public void getPreparedStatement(@NotNull String query, @NotNull PreparedStatementExecutor preparedStatementExecutor) {
        /* ignored */
    }
}
