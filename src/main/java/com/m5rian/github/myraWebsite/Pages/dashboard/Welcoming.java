package com.m5rian.github.myraWebsite.Pages.dashboard;

import com.m5rian.github.myraWebsite.LoginRedirection;
import com.m5rian.github.myraWebsite.database.Db;
import com.m5rian.github.myraWebsite.discord.Guild;
import com.m5rian.github.myraWebsite.discord.GuildChannels;
import com.m5rian.github.myraWebsite.discord.channels.TextChannel;
import com.m5rian.github.myraWebsite.utilities.Reader;
import org.bson.Document;
import org.json.JSONArray;
import spark.Request;
import spark.Response;

public class Welcoming {
    public static Object onGet(Request req, Response res) {
        try {
            // User has no active session
            if (req.cookie("session") == null) return DashboardUtils.embed(req);

            final String html = new Reader(req, res)
                    .requiresAccount()
                    .read("dashboard/welcoming/welcoming");
            final String guildId = req.params("guildId");

            final StringBuilder channelsHtml = new StringBuilder(); // Stores the channels
            final JSONArray channels = GuildChannels.getChannels(guildId); // Get all guild channels
            final String itemTemplate = Reader.readFile("/utilities/dropdown-item.html"); // Get template for dropdown items
            for (int i = 0; i < channels.length(); i++) {
                final TextChannel channel = new TextChannel(channels.getJSONObject(i)); // Get current channel

                // Channel is voice call or category
                if (channel.getType() == GuildChannels.GUILD_VOICE || channel.getType() == GuildChannels.GUILD_CATEGORY) continue;

                channelsHtml.append(itemTemplate
                        .replace("{$value}", channel.getId())
                        .replace("{$display}", channel.getName()));
            }

            // Get database documents

            final Document guildDocument = Db.Cache.getGuild(guildId); // Get guild document
            System.out.println(guildDocument.toJson());
            final Document listeners = guildDocument.get("listeners", Document.class); // Get listeners document
            final Document welcomeDocument = guildDocument.get("welcome", Document.class); // Get welcome document

            // Welcome channel
            final String welcomeChannelId = welcomeDocument.getString("welcomeChannel"); // Get welcome channel
            String welcomeChannelName;
            // No welcome channel has been set up yet
            if (welcomeChannelId.equals("not set")) {
                welcomeChannelName = "not set";
            } else {
                welcomeChannelName = GuildChannels.getChannelById(welcomeChannelId).name; // Get infos about the welcome channel
            }

            // Welcome image background url
            String welcomeImageBackground = welcomeDocument.getString("welcomeImageBackground"); // Get welcome image background url
            if (welcomeImageBackground.equals("not set")) welcomeImageBackground = "";

            final Guild.DiscordGuild guild = Guild.getGuild(guildId); // Get information about the guild
            return html
                    .replace("{$guild.icon}", guild.icon)
                    .replace("{$guild.name}", guild.name)
                    .replace("{$welcome_embed_toggled}", listeners.getBoolean("welcomeEmbed") ? "checked" : "") // Are welcome embeds toggled on?
                    .replace("{$welcome_image_toggled}", listeners.getBoolean("welcomeImage") ? "checked" : "") // Are welcome embeds toggled on?
                    .replace("{$welcome_direct_message_toggled}", listeners.getBoolean("welcomeDirectMessage") ? "checked" : "") // Are welcome embeds toggled on?
                    .replace("{$current_welcome_embed_message}", welcomeDocument.getString("welcomeEmbedMessage"))
                    .replace("{$current_welcome_image_url}", welcomeImageBackground)
                    .replace("{$current_welcome_direct_message}", welcomeDocument.getString("welcomeDirectMessage"))
                    .replace("{$current_welcome_channel_name}", welcomeChannelName)
                    .replace("{$current_welcome_channel_id}", welcomeChannelId)
                    .replace("{$channels}", channelsHtml);
        } catch (
                LoginRedirection redirection) {
            return null;
        }
    }


}
