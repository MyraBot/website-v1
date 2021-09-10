package com.m5rian.github.myraWebsite.utilities;

import com.m5rian.github.myraWebsite.LoginRedirection;
import com.m5rian.github.myraWebsite.discord.DiscordUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Reader {
    private static final Logger logger = LoggerFactory.getLogger(Reader.class);

    private final Request req;
    private final Response res;

    private boolean requiresAccount = false;
    private boolean isOnDashboard = false;

    public Reader(Request req, Response res) {
        this.req = req;
        this.res = res;
    }

    public static String readFile(String filePath) {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        try {
            InputStream inputStream = Reader.class.getResourceAsStream(filePath);

            byte[] buffer = new byte[1024];
            for (int length; (length = inputStream.read(buffer)) != -1; ) {
                result.write(buffer, 0, length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString(StandardCharsets.UTF_8)
                .replace("{$myra.icon}", Utils.MYRA_ICON)
                .replace("{$blue}", Utils.blue)
                .replace("{$red}", Utils.red);
    }

    public Reader requiresAccount() {
        this.requiresAccount = true;
        return this;
    }

    public Reader isOnDashboard() {
        this.isOnDashboard = true;
        return this;
    }

    public String read(String fileName) throws LoginRedirection {
        String html = readFile("/pages/" + fileName + ".html")
                .replace("{$info-popup}", readFile("/utilities/info-popup.html")) // Information container
                .replace("{$footer}", readFile("/utilities/footer/footer.html"));

        final Iterator<String> iterator = Arrays.stream(html.split("\\s+")).iterator();
        while (iterator.hasNext()) {
            final String word = iterator.next();
            if (word.matches("\\{\\$wave\\.([a-z]|-)*\\[([a-z]|-)*=.*]}")) {
                final String position = word.split("\\.")[1].split("\\[")[0];
                final String colour = word.split("=")[1].substring(0, word.split("=")[1].length() - 2);

                String wave;
                switch (position) {
                    case "top" -> wave = readFile("/utilities/waves/top");
                    default -> wave = "";
                }
                wave = wave.replace("{$colour}", colour);
                html = html.replace(word, wave);
            }
        }

        // Page is a dashboard page, which requires the user to be logged in
        if (this.isOnDashboard) {
            // User has no active session or token is missing
            if (req.cookie("session") == null || req.cookie("oAuth2Token") == null) {
                logger.info("Users session or oAuth2 token is null");

                throw new LoginRedirection(this.req, this.res);
            }

            final String guildId = req.params("guildId"); // Get guild id
            final List<DiscordUser.FeaturedGuild> userServers = DiscordUser.getUserGuilds(req, res); // Get servers of user
            final boolean isAdminOnServer = userServers.stream().anyMatch(guild -> guild.id.equals(guildId) && Utils.isAdministrator(guild.permissions)); // Check if user has administrator permissions on server
            if (!isAdminOnServer) Spark.halt(404); // Throw 404 exception

            // User has an active session
            final DiscordUser.CookieUser discordUser = DiscordUser.getDiscordUser(req, res); // Get discord user information
            return html
                    .replace("{$nav_bar}", readFile("/utilities/navigationBar/navigation-bar-logged-in.html")) // Navigation bar
                    .replace("{$account.name}", discordUser.getTag()) // Name of user
                    .replace("{$account.avatar}", discordUser.avatarUrl) // Avatar of user
                    .replace("{$myra.icon}", Utils.MYRA_ICON)
                    .replace("{$blue}", Utils.blue)
                    .replace("{$red}", Utils.red)
                    .replace("{$settings.categories}", readFile("/pages/dashboard/settings-categories.html")); // Dashboard category selection
        }
        // User needs to be logged in to see the page
        else if (this.requiresAccount) {
            // User has no active session or token is missing
            if (req.cookie("session") == null || req.cookie("oAuth2Token") == null) {
                logger.info("Users session or oAuth2 token is null");

                throw new LoginRedirection(this.req, this.res);
            }
            // User has an active session
            final DiscordUser.CookieUser discordUser = DiscordUser.getDiscordUser(req, res); // Get discord user information
            return html
                    .replace("{$nav_bar}", readFile("/utilities/navigationBar/navigation-bar-logged-in.html")) // Navigation bar
                    .replace("{$account.name}", discordUser.getTag()) // Name of user
                    .replace("{$account.avatar}", discordUser.avatarUrl) // Avatar of user
                    .replace("{$myra.icon}", Utils.MYRA_ICON)
                    .replace("{$blue}", Utils.blue)
                    .replace("{$red}", Utils.red)
                    .replace("{$settings.categories}", readFile("/pages/dashboard/settings-categories.html")); // Dashboard category selection
        }

        // User doesn't need to be logged in
        else {
            // User is a guest -> has no active session
            if (req.cookie("session") == null) {
                logger.info("Guest user logs in");
                return html
                        .replace("{$nav_bar}", readFile("/utilities/navigationBar/navigation-bar-guest.html")) // Navigation bar
                        .replace("{$myra.icon}", Utils.MYRA_ICON)
                        .replace("{$blue}", Utils.blue)
                        .replace("{$red}", Utils.red);
            }

            // User is logged in, even if he doesn't need to
            logger.info("Logged in user visits non-required-logged in page");
            final DiscordUser.CookieUser discordUser = DiscordUser.getDiscordUser(req, res); // Get discord user information
            return html
                    .replace("{$nav_bar}", readFile("/utilities/navigationBar/navigation-bar-logged-in.html")) // Navigation bar
                    .replace("{$account.name}", discordUser.getTag())
                    .replace("{$account.avatar}", discordUser.avatarUrl)
                    .replace("{$myra.icon}", Utils.MYRA_ICON)
                    .replace("{$blue}", Utils.blue)
                    .replace("{$red}", Utils.red);
        }

    }

}
