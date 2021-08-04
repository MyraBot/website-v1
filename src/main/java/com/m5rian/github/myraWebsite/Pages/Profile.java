package com.m5rian.github.myraWebsite.Pages;

import com.m5rian.github.myraWebsite.LoginRedirection;
import com.m5rian.github.myraWebsite.database.Db;
import com.m5rian.github.myraWebsite.database.MongoUser;
import com.m5rian.github.myraWebsite.discord.DiscordUser;
import com.m5rian.github.myraWebsite.utilities.Reader;
import com.m5rian.github.myraWebsite.utilities.Utils;
import org.bson.Document;
import spark.Request;
import spark.Response;

import static com.mongodb.client.model.Filters.eq;

public class Profile {

    public static Object onGet(Request req, Response res) {
        try {
            final String baseUrl = req.url().split("/profile", 2)[0]; // Get base url
            final String userId = req.params("userId"); // Get user id

            final String html = new Reader(req, res).read("profile/profile"); // Read raw html file

            final Document userDocument = Db.getDatabase().getCollection("users").find(eq("userId", userId)).first(); // Get user document
            final MongoUser user = new MongoUser(userDocument);

            final StringBuilder htmlBadges = new StringBuilder();
            for (DiscordUser.UserBadge badge : user.badges) {
                final String badgeImage = String.format("%s/images/badges/%s.png", baseUrl, badge.getName()); // Get badge image path
                // Create html for badged
                final String badgeHtml = String.format("<div class=\"badge tooltip-bottom\" tooltip=\"%s\">" +
                                "<img src=\"%s\">" +
                                "</div>",
                        badge.getName(), badgeImage);
                htmlBadges.append(badgeHtml); // Add badge
            }

            return html
                    .replace("{$user.id}", user.id)
                    .replace("{$user.name}", user.name)
                    .replace("{$user.discriminator}", user.discriminator)
                    .replace("{$user.avatar}", user.avatar)
                    .replace("{$user.badges}", htmlBadges) // All badges
                    .replace("{$user.level}", String.valueOf(Utils.getLevelFromXp(user.xp))) // Global level
                    .replace("{$user.messages}", String.valueOf(user.messages)); // Global message count
        } catch (LoginRedirection redirection) {
            return null;
        }
    }


}
