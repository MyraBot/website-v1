package com.m5rian.github.myraWebsite.Pages.dashboard;

import com.m5rian.github.myraWebsite.LoginRedirection;
import com.m5rian.github.myraWebsite.discord.GuildChannels;
import com.m5rian.github.myraWebsite.utilities.Reader;
import org.json.JSONArray;
import org.json.JSONObject;
import spark.Request;
import spark.Response;

public class Leveling {

    public static Object onGet(Request req, Response res) {
        try {
            // User has an active session
            if (req.cookie("session") != null) {
                final StringBuilder htmlChannels = new StringBuilder(); // Stores the channels
                final JSONArray channels = GuildChannels.getChannels(req.params("guildId")); // Get all guild channels
                for (int i = 0; i < channels.length(); i++) {
                    final JSONObject channel = channels.getJSONObject(i); // Get current channel

                    final int type = channel.getInt("type"); // Get channel type
                    // Channel is voice call or category
                    if (type == GuildChannels.GUILD_VOICE || type == GuildChannels.GUILD_CATEGORY) continue;
                    final String id = channel.getString("id"); // Get channel id
                    final String name = channel.getString("name"); // Get channel name

                    htmlChannels.append("<div class=\"option\" data-value=\"").append(id).append("\"><p>").append(name).append("</p></div>"); // Add channel to selection
                }

                final String html = new Reader(req, res)
                        .requiresAccount()
                        .read("dashboard/categories/leveling/leveling"); // Read raw html file
                return html
                        .replace("{channels}", htmlChannels); // Level-up channel selection

            }

            // User hasn't an active session
            res.cookie("/", "fromPage", req.url(), Integer.MAX_VALUE, false, false); // Save current url
            res.redirect("/login"); // Login
            return null;
        } catch (
                LoginRedirection redirection) {
            return null;
        }
    }

}
