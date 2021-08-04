package com.m5rian.github.myraWebsite.Pages;

import com.m5rian.github.myraWebsite.LoginRedirection;
import com.m5rian.github.myraWebsite.database.Db;
import com.m5rian.github.myraWebsite.utilities.Reader;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import spark.Request;
import spark.Response;

public class Team {

    public static Object onGet(Request req, Response res) {
        try {
            String html = new Reader(req, res).read("team/team");// Read raw html file
            final String userCardTemplate = Reader.readFile("/pages/team/user.html"); // Read user card
            final MongoCollection<Document> users = Db.getDatabase().getCollection("users"); // Get users from database

            final String[] words = html.split("\\s+");
            for (String word : words) {
                // Word is a variable for an user
                if (word.startsWith("{$card.")) {
                    final String userId = word.replaceAll("\\D", ""); // Get user id by removing non digits
                    final Document user = users.find(Filters.eq("userId", userId)).first(); // Get user from database
                    if (user == null) System.out.println(userId);
                    final String userCard = userCardTemplate
                            .replace("{$user.avatar}", user.getString("avatar"))
                            .replace("{$user.name}", user.getString("name"))
                            .replace("{$user.id}", userId);
                    html = html.replace(word, userCard); // Replace variable
                }
            }

            return html;
        } catch (LoginRedirection redirection) {
            return null;
        }
    }

}
