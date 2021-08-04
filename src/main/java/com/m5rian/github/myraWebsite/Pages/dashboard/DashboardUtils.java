package com.m5rian.github.myraWebsite.Pages.dashboard;

import com.m5rian.github.myraWebsite.discord.Guild;
import com.m5rian.github.myraWebsite.utilities.Reader;
import spark.Request;

public class DashboardUtils {

    /**
     * @param req A {@link Request}.
     * @return Returns empty html file with an embed.
     */
    public static Object embed(Request req) {
        final String guildId = req.params("guildId"); // Get guild id
        final Guild.DiscordGuild guild = Guild.getGuild(guildId); // Get information about the guild

        return Reader.readFile("/pages/dashboard/preview.html")
                .replace("{$guild.icon}", guild.icon)
                .replace("{$guild.name}", guild.name);

    }

}
