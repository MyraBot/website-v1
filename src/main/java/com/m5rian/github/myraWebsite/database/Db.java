package com.m5rian.github.myraWebsite.database;

import com.m5rian.github.myraWebsite.utilities.Utils;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.io.IOException;

public class Db {
    // Instance
    private final static Db MONGO_DB = new Db();

    /**
     * @return Returns a {@link Db} instance
     */
    public static Db getDatabase() {
        return MONGO_DB;
    }

    final ConnectionString connectionString = new ConnectionString("mongodb+srv://Marian:dGP3e3Iewlqypmxq@cluster0.epzcx.mongodb.net/test?w=majority");
    final MongoClientSettings settings = MongoClientSettings.builder()
            .applyConnectionString(connectionString)
            .retryWrites(true)
            .build();

    final MongoClient mongoClient = MongoClients.create(settings);
    final MongoDatabase database = mongoClient.getDatabase("Myra"); // Get database
    
    public MongoCollection<Document> getCollection(String collection) {
        return database.getCollection(collection);
    }

    public class Cache {
        public static Document getGuild(String guildId) {
            final okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(Utils.CURRENT_ADDRESS + ":" + Utils.BOT_PORT + "/api/retrieve/guild")
                    .addHeader("guildId", guildId)
                    .get()
                    .build();

            try (okhttp3.Response response = Utils.HTTP_CLIENT.newCall(request).execute()) {
                return Document.parse(response.body().string()); // Parse json response to org.bson.Document and return it
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}