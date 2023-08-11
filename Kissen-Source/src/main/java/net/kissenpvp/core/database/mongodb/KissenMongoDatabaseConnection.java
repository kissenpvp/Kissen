package net.kissenpvp.core.database.mongodb;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import net.kissenpvp.core.api.database.connection.DatabaseDriver;
import net.kissenpvp.core.api.database.connection.MongoDatabaseConnection;
import net.kissenpvp.core.api.database.meta.BackendException;
import net.kissenpvp.core.api.database.meta.Meta;
import net.kissenpvp.core.api.database.meta.ObjectMeta;
import org.bson.BsonInt64;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

@Getter
public class KissenMongoDatabaseConnection implements MongoDatabaseConnection {

    private final String connectionID;
    private final String connectionString;
    private final DatabaseDriver driver;
    private MongoClient mongoClient;
    private MongoDatabase database;


    public KissenMongoDatabaseConnection(@NotNull String connectionID, @NotNull String connectionString) {
        this.driver = DatabaseDriver.MONGODB;
        this.connectionID = connectionID;
        this.connectionString = connectionString;
    }

    @Override
    public boolean isConnected() {
        return mongoClient != null;
    }

    @Override
    public void connect() throws BackendException {
        if (!isConnected()) {
            try {
                MongoClientSettings mongoClientSettings = MongoClientSettings.builder().applyConnectionString(new ConnectionString(connectionString)).build();
                mongoClient = MongoClients.create(mongoClientSettings);

                assert mongoClientSettings.getCredential() != null;
                database = mongoClient.getDatabase(mongoClientSettings.getCredential().getSource());
                database.runCommand(new Document("ping", new BsonInt64(1)));
            } catch (MongoException | IllegalArgumentException mongoException) {
                throw new BackendException(mongoException);
            }
            return;
        }
        throw new BackendException(new IllegalStateException("Connection already established."));
    }

    @Override
    public void disconnect() throws BackendException {
        if (isConnected()) {
            mongoClient.close();
            mongoClient = null;
            return;
        }
        throw new BackendException(new IllegalStateException("Connection cannot be closed, because there is no connection."));
    }

    @Override
    public @NotNull ObjectMeta createObjectMeta(@NotNull String table) {
        return new KissenObjectMongoMeta(table) {
            @Override
            protected @NotNull MongoCollection<Document> getCollection() {
                return database.getCollection(table);
            }
        };
    }

    @Override
    public @NotNull Meta createMeta(@NotNull String table, @NotNull String uuidColumn, @NotNull String keyColumn, @NotNull String valueColumn) {
        return new KissenMongoMeta(table, uuidColumn, keyColumn, valueColumn) {
            @Override
            protected @NotNull MongoCollection<Document> getCollection() {
                return database.getCollection(table);
            }
        };
    }
}
