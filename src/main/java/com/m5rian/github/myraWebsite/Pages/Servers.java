package com.m5rian.github.myraWebsite.Pages;

import com.m5rian.github.myraWebsite.LoginRedirection;
import com.m5rian.github.myraWebsite.database.Db;
import com.m5rian.github.myraWebsite.discord.DiscordUser;
import com.m5rian.github.myraWebsite.discord.Guild;
import com.m5rian.github.myraWebsite.utilities.Reader;
import com.m5rian.github.myraWebsite.utilities.Utils;
import spark.Request;
import spark.Response;

import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class Servers {

    public static Object onGet(Request req, Response res) {
        try {
            final String html = new Reader(req, res)
                    .requiresAccount()
                    .read("servers/servers");

            final StringBuilder htmlGuilds = new StringBuilder(); // Store all guilds as html
            final String guildTemplate = Reader.readFile("/pages/servers/guilds/guild.html"); // Get guild html template
            final String unknownGuildTemplate = Reader.readFile("/pages/servers/guilds/unknownGuild.html"); // Get guild html template for guilds, where myra isn't in

            final List<DiscordUser.FeaturedGuild> userServers = DiscordUser.getUserGuilds(req, res); // Get users servers
            for (DiscordUser.FeaturedGuild guild : userServers) {
                // Member has administrator permissions
                if (Utils.isAdministrator(guild.permissions)) {
                    // Myra is in the server
                    if (Guild.hasMyra(guild.id)) {
                        final String guildHtml = guildTemplate
                                .replace("{guildId}", guild.id) // Replace guild id
                                .replace("{icon}", Utils.getGuildIcon(guild.id, guild.icon)) // Replace icon
                                .replace("{name}", guild.name); // Replace name
                        htmlGuilds.append(guildHtml); // Add server to the shown ones
                    }
                    // Myra isn't in the server
                    else {
                        final String guildHtml = unknownGuildTemplate
                                .replace("{icon}", Utils.getGuildIcon(guild.id, guild.icon)) // Replace icon
                                .replace("{name}", guild.name); // Replace name
                        htmlGuilds.append(guildHtml); // Add server to the shown ones
                    }
                }
            }

            return html.replace("{guilds}", htmlGuilds); // Replace guilds

        } catch (LoginRedirection redirection) {
            return null;
        }
    }

}
