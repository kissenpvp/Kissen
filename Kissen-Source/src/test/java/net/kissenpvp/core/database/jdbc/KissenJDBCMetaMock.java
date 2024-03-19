package net.kissenpvp.core.database.jdbc;

import net.kissenpvp.core.api.database.connection.PreparedStatementExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class KissenJDBCMetaMock extends KissenObjectJDBCMeta {

    private final CompletableFuture<String> result;

    public KissenJDBCMetaMock() {
        super("test");
        this.result = new CompletableFuture<>();
    }

    public @NotNull CompletableFuture<String> getResult() {
        return result;
    }

    @Override
    protected void generateTable() {
        // skip
    }

    @Override
    public void getPreparedStatement(@NotNull String query, @NotNull PreparedStatementExecutor preparedStatementExecutor) {
        this.result.complete(query);
    }
}
