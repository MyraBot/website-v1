package com.m5rian.github.myraWebsite;

import com.m5rian.github.myraWebsite.Pages.*;
import com.m5rian.github.myraWebsite.Pages.dashboard.*;
import com.m5rian.github.myraWebsite.utilities.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;

import java.util.function.Function;

import static spark.Spark.*;

public class WebApp {
    private final static Logger LOGGER = LoggerFactory.getLogger(WebApp.class);

    /**
     * The {@link spark.Spark} library uses Jetty.
     *
     * @param args Some useless arguments
     */
    public static void main(String[] args) {
        port(Utils.WEBSITE_PORT);
        staticFileLocation("/"); // Set the location for files
        Utils.setup();

        // Main pages
        get("/", Home::onGet); // Home page
        get("/leaderboard/:guildId/level", Leaderboard::getGuildLevelLeaderboard); // Guild level leaderboard
        get("/leaderboard/:guildId/balance", Leaderboard::getGuildBalanceLeaderboard); // Guild balance leaderboard
        get("/leaderboard/:guildId/voice", Leaderboard::getGuildVoiceCallLeaderboard); // Guild voice call time leaderboard
        get("/leaderboard/:guildId/embed", EmbedBuilder::onGet); // Dashboard
        get("/profile/:userId", Profile::onGet); // User profile
        // Dashboard
        get("/servers", Servers::onGet); // Server selection
        get("dashboard/:guildId/modules", Modules::onGet); // Module selection
        get("/dashboard/:guildId/general", General::onGet); // Dashboard
        get("/dashboard/:guildId/leveling", Leveling::onGet); // Leveling settings
        get("/dashboard/:guildId/welcoming", Welcoming::onGet); // Leveling settings
        get("/dashboard/:guildId/embed", Embed::onGet);
        // Other
        get("/login", Login::login); // Discord login
        get("/terms", TermsOfService::onGet); // Terms of service
        get("/team", Team::onGet); // The team members
        get("/invite", Invite::onGet); // Invite discord bot
        get("/support", Support::onGet); // Discord support server

        // Redirects
        redirect("/dashboard/:guildId", req -> "dashboard/$guildId/modules");
        redirect("/leaderboard/:guildId", req -> "leaderboard/$guildId/level");
        redirect("/leaderboard/:guildId/", req -> "leaderboard/$guildId/level");
    }

    public static void redirect(String fromPath, Function<Request, String> toPath) {
        get(fromPath, (req, res) -> {
            String path = req.url().replace(req.uri(), "") + "/" + toPath.apply(req);
            for (String directory : toPath.apply(req).split("/")) {
                if (directory.startsWith("$")) {
                    path = path.replace(directory, req.params(directory.substring(1)));
                }
            }
            res.redirect(path);
            return null;
        });
    }

}