package com.m5rian.github.myraWebsite.Pages.dashboard;

import com.m5rian.github.myraWebsite.LoginRedirection;
import com.m5rian.github.myraWebsite.discord.Guild;
import com.m5rian.github.myraWebsite.discord.GuildChannels;
import com.m5rian.github.myraWebsite.discord.channels.TextChannel;
import com.m5rian.github.myraWebsite.utilities.Reader;
import org.json.JSONArray;
import spark.Request;
import spark.Response;

public class Embed {

    public static Object onGet(Request req, Response res) {
        try {
            // User has no active session
            if (req.cookie("session") == null) return DashboardUtils.embed(req);

            final String guildId = req.params("guildId"); // Get guild id
            final String html = new Reader(req, res)
                    .requiresAccount()
                    .isOnDashboard()
                    .read("dashboard/embed/embed"); // Read raw html file

            final JSONArray channels = GuildChannels.getChannels(guildId); // Get all guild channels
            String channelsHtml = "";
            final String itemTemplate = Reader.readFile("/utilities/dropdown-item.html"); // Get template for dropdown items
            for (int i = 0; i < channels.length(); i++) {
                final TextChannel channel = new TextChannel(channels.getJSONObject(i)); // Get current channel

                // Channel is voice call or category
                if (channel.getType() == GuildChannels.GUILD_VOICE || channel.getType() == GuildChannels.GUILD_CATEGORY) continue;

                channelsHtml += itemTemplate
                        .replace("{$value}", channel.getId())
                        .replace("{$display}", channel.getName());
            }

            final Guild.DiscordGuild guild = Guild.getGuild(guildId); // Get information about the guild
            return html
                    .replace("{$guild.icon}", guild.icon)
                    .replace("{$guild.name}", guild.name)
                    .replace("{$channels}", channelsHtml);

        } catch (LoginRedirection redirection) {
            return null;
        }
    }

}
