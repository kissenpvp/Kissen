package net.kissenpvp.core.api.database.connection;

import java.util.Optional;

public enum DatabaseDriver {

    MYSQL("com.mysql.cj.jdbc.Driver"),
    SQLITE("org.sqlite.JDBC"),
    MONGODB();

    private final String clazz;

    DatabaseDriver(String clazz) {
        this.clazz = clazz;
    }

    DatabaseDriver() {
        this(null);
    }

    @Override
    public String toString() {
        return Optional.ofNullable(clazz).orElse(name().toLowerCase());
    }
}
