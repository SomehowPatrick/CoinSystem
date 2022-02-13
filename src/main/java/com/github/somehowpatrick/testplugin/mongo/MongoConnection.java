package com.github.somehowpatrick.testplugin.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoConnection {
    private final String url;

    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;

    public MongoConnection(String url) {
        this.url = url;
    }

    public void connect(String database) {
        if (this.mongoClient == null) {
            this.mongoClient = MongoClients.create(this.url);
            this.mongoDatabase = this.mongoClient.getDatabase(database);
        }
    }

    public void disable() {
        if (this.mongoClient != null) {
            this.mongoClient.close();
        }
    }

    public MongoCollection<Document> getCollection(String name) {
        return this.mongoDatabase.getCollection(name);
    }
}
