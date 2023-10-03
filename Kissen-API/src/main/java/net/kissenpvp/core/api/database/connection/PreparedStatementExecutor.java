package net.kissenpvp.core.api.database.connection;

import net.kissenpvp.core.api.base.ExceptionHandler;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface PreparedStatementExecutor extends ExceptionHandler<SQLException> {

    void execute(@NotNull PreparedStatement preparedStatement) throws SQLException;

    @Override
    default boolean handle(@NotNull SQLException throwable) {
        return false;
    }
}
