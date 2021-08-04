package com.m5rian.github.myraWebsite.Pages.dashboard;

import com.m5rian.github.myraWebsite.LoginRedirection;
import com.m5rian.github.myraWebsite.database.Db;
import com.m5rian.github.myraWebsite.discord.DiscordUser;
import com.m5rian.github.myraWebsite.discord.Guild;
import com.m5rian.github.myraWebsite.utilities.Reader;
import com.m5rian.github.myraWebsite.utilities.Utils;
import org.bson.Document;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class General {

    public static Object onGet(Request req, Response res) {
        try {
            // User has no active session
            if (req.cookie("session") == null) return DashboardUtils.embed(req);

            final String guildId = req.params("guildId"); // Get guild id

            List<DiscordUser.FeaturedGuild> userServers = DiscordUser.getUserGuilds(req, res);
            final boolean isAdminOnServer = userServers.stream().anyMatch(guild -> guild.id.equals(guildId) && Utils.isAdministrator(guild.permissions));
            if (!isAdminOnServer) Spark.halt(404); // Throw 404 exception

            final String html = new Reader(req, res)
                    .requiresAccount()
                    .read("dashboard/general/general"); // Read raw html file

            final Document guildDocument = Db.Cache.getGuild(guildId); // Get guild document
            final String prefix = guildDocument.getString("prefix"); // Get the guilds prefix

            final Guild.DiscordGuild guild = Guild.getGuild(guildId); // Get information about the guild
            return html
                    .replace("{$guild.icon}", guild.icon)
                    .replace("{$guild.name}", guild.name)
                    .replace("{$prefix}", prefix); // Guild prefix

        } catch (LoginRedirection redirection) {
            return null;
        }
    }

}
