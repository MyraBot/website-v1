package com.m5rian.github.myraWebsite.Pages;

import com.m5rian.github.myraWebsite.LoginRedirection;
import com.m5rian.github.myraWebsite.database.Db;
import com.m5rian.github.myraWebsite.database.MongoMember;
import com.m5rian.github.myraWebsite.discord.DiscordUser;
import com.m5rian.github.myraWebsite.discord.Guild;
import com.m5rian.github.myraWebsite.utilities.Reader;
import com.m5rian.github.myraWebsite.utilities.Utils;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.mongodb.client.model.Filters.*;

public class Leaderboard {

    public static Object getGlobalLeaderboard(Request req, Response res) {
        try {
            res.type("text/html"); // Set response type to html
            final String html = new Reader(req, res).read("leaderboard/leaderboard"); // Read raw html file
            return html;
        } catch (LoginRedirection redirection) {
            return null;
        }
    }

    public static Object getGuildLevelLeaderboard(Request req, Response res) {
        try {
            final String guildId = req.params("guildId"); // Get guild id
            // Bot isn't in this guild
            if (Db.getDatabase().getCollection("guilds").find(eq("guildId", guildId)).first() == null) {
                Spark.halt(404); // Throw 404 exception
            }

            final Guild.DiscordGuild guild = Guild.getGuild(guildId); // Get guild

            List<MongoMember> members = new ArrayList<>();
            Db.getDatabase().getCollection("users").find(exists(guildId)).forEach(userDocument -> {
                try {
                    final MongoMember member = new MongoMember(userDocument, guildId);
                    members.add(member); // Add member to list
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(userDocument.getString("userId"));
                }
            });
            members.sort(Comparator.comparing(MongoMember::getXp).reversed()); // Sort list after xp

            final StringBuilder htmlMembers = new StringBuilder(); // Stores the html for the members
            final String htmlMemberTemplate = new Reader(req, res).read("leaderboard/level-leaderboard-user"); // Get template
            // Go through each member
            for (int i = 0; i < members.size(); i++) {
                final MongoMember member = members.get(i); // Get current member
                System.out.println(member.getId());
                final String htmlMember = htmlMemberTemplate
                        .replace("{$member.rank}", String.valueOf(i + 1))
                        .replace("{$member.avatar}", member.getAvatar())
                        .replace("{$member.name}", member.getName())
                        .replace("{$member_messages}", String.valueOf(member.getMessages()))
                        .replace("{$member_xp}", String.valueOf(member.getXp()))
                        .replace("{$member_level}", String.valueOf(member.getLevel()));
                htmlMembers.append(htmlMember); // Add member
            }

            final String html = new Reader(req, res).read("leaderboard/leaderboard"); // Read raw html file
            return html
                    .replace("{$guild.icon}", guild.icon)
                    .replace("{$guild.name}", guild.name)
                    .replace("{$members}", htmlMembers.toString()); // Leaderboard members
        } catch (LoginRedirection redirection) {
            return null;
        }
    }

    public static Object getGuildBalanceLeaderboard(Request req, Response res) {
        try {
            // User has an active session
            if (req.cookie("session") != null) {
                final DiscordUser.CookieUser discordUser = DiscordUser.getDiscordUser(req, res); // Get discord user information

                final String guildId = req.params("guildId"); // Get guild id
                // Bot isn't in this guild
                if (!Guild.hasMyra(guildId)) {
                    Spark.halt(404); // Throw 404 exception
                }

                final Guild.DiscordGuild guild = Guild.getGuild(guildId); // Get guild

                List<MongoMember> members = new ArrayList<>();
                Db.getDatabase().getCollection("users").find(exists(guildId)).forEach(userDocument -> {
                    try {
                        final MongoMember member = new MongoMember(userDocument, guildId);
                        members.add(member); // Add member to list
                    } catch (Exception e) {
                        System.out.println(userDocument.getString("userId"));
                    }
                });
                members.sort(Comparator.comparing(MongoMember::getBalance).reversed()); // Sort list after xp

                final StringBuilder htmlMembers = new StringBuilder(); // Stores the html for the members
                final String htmlMemberTemplate = new Reader(req, res).read("leaderboard/balance-leaderboard-user"); // Get template
                // Go through each member
                for (int i = 0; i < members.size(); i++) {
                    final MongoMember member = members.get(i); // Get current member
                    final String htmlMember = htmlMemberTemplate
                            .replace("{$member.rank}", String.valueOf(i + 1))
                            .replace("{$member.avatar}", member.getAvatar())
                            .replace("{$member.name}", member.getName())
                            .replace("{$member.balance}", Utils.formatNumber(member.getBalance()));
                    htmlMembers.append(htmlMember); // Add member
                }

                res.type("text/html"); // Set response type to html
                final String html = new Reader(req, res).read("leaderboard/leaderboard"); // Read raw html file
                return html
                        .replace("{$guild.icon}", guild.icon)
                        .replace("{$guild.name}", guild.name)
                        .replace("{$members}", htmlMembers.toString()); // Leaderboard members
            }

            // User hasn't an active session
            res.cookie("/", "fromPage", req.url(), Integer.MAX_VALUE, false, false); // Save current url
            res.redirect("/login"); // Login
            return null;
        } catch (LoginRedirection redirection) {
            return null;
        }
    }

    public static Object getGuildVoiceCallLeaderboard(Request req, Response res) {
        try {
            // User has no active session
            if (req.cookie("session") == null) {
                // User hasn't an active session
                res.cookie("/", "fromPage", req.url(), Integer.MAX_VALUE, false, false); // Save current url
                res.redirect("/login"); // Login
                return null;
            }

            final DiscordUser.CookieUser discordUser = DiscordUser.getDiscordUser(req, res); // Get discord user information

            final String guildId = req.params("guildId"); // Get guild id
            // Bot isn't in this guild
            if (!Guild.hasMyra(guildId)) {
                Spark.halt(404); // Throw 404 exception
            }

            final Guild.DiscordGuild guild = Guild.getGuild(guildId); // Get guild

            List<MongoMember> members = new ArrayList<>();
            Db.getDatabase().getCollection("users").find(exists(guildId)).forEach(userDocument -> {
                try {
                    final MongoMember member = new MongoMember(userDocument, guildId);
                    members.add(member); // Add member to list
                } catch (Exception e) {
                    System.out.println(userDocument.getString("userId"));
                }
            });
            members.sort(Comparator.comparing(MongoMember::getVoiceCallTime).reversed()); // Sort list after xp

            final StringBuilder htmlMembers = new StringBuilder(); // Stores the html for the members
            final String htmlMemberTemplate = new Reader(req, res).read("leaderboard/voice-call-leaderboard-user"); // Get template
            // Go through each member
            for (int i = 0; i < members.size(); i++) {
                final MongoMember member = members.get(i); // Get current member
                final String htmlMember = htmlMemberTemplate
                        .replace("{$member.rank}", String.valueOf(i + 1))
                        .replace("{$member.avatar}", member.getAvatar())
                        .replace("{$member.name}", member.getName())
                        .replace("{$member.voiceTime}", Utils.toTimeExact(member.getVoiceCallTime()));
                htmlMembers.append(htmlMember); // Add member
            }

            res.type("text/html"); // Set response type to html
            final String html = new Reader(req, res).read("leaderboard/leaderboard"); // Read raw html file
            return html
                    .replace("{$guild.icon}", guild.icon)
                    .replace("{$guild.name}", guild.name)
                    .replace("{$members}", htmlMembers.toString()); // Leaderboard members

        } catch (LoginRedirection redirection) {
            return null;
        }
    }

}
