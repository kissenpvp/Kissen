package net.kissenpvp.core.database.mongodb;

public abstract class KissenNativeMongoMeta extends KissenMongoMeta {
    public KissenNativeMongoMeta(String table) {
        super(table, "uuid", "key", "value");
    }
}
