package com.m5rian.github.myraWebsite.discord;

import com.m5rian.github.myraWebsite.LoginRedirection;
import com.m5rian.github.myraWebsite.utilities.Utils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import spark.Request;
import spark.Response;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class checks if the needed information are saved as cookies.
 * If they are saved as cookies, it returns the cookies.
 * If not it makes a new http request to get the information about the user
 * and saves the info then as cookies.
 */
public class DiscordUser {

    private static final String GUILD_URL = Utils.DISCORD_BASE_URL + "/users/@me/guilds";

    public static CookieUser getDiscordUser(Request req, Response res) throws LoginRedirection {
        final String oAuth2Token = OAuth2.getToken(req, res); // Get access token

        String userId;
        String userAsTag;
        String userAvatar;

        // No user cookies
        if (req.cookie("userId") == null || req.cookie("userAsTag") == null || req.cookie("userAvatar") == null) {
            final JSONObject jsonUser = getUser(oAuth2Token); // Get information about user

            final String id = jsonUser.getString("id"); // Get user id
            final String avatarId = jsonUser.optString("avatar", null); // Get avatar id, make default value null
            final String username = jsonUser.getString("username"); // Get username
            final String tag = jsonUser.getString("discriminator"); // Get user's tag
            final String avatar = Utils.getUserAvatar(id, avatarId); // Get users avatar

            res.cookie("/", "userId", id, Integer.MAX_VALUE, false, false); // Save user id as cookie
            res.cookie("/", "userAvatar", avatar, Integer.MAX_VALUE, false, false); // Save avatar as cookie
            res.cookie("/", "userAsTag", URLEncoder.encode(username + "#" + tag, StandardCharsets.UTF_8), Integer.MAX_VALUE, false, false); // Save tag as cookie

            userId = id;
            userAsTag = username + "#" + tag;
            userAvatar = avatar;
        }
        // User cookies exist
        else {
            userId = req.cookie("userId"); // Get user id from cookie
            userAsTag = req.cookie("userAsTag"); // Get avatar from cookie
            userAvatar = req.cookie("userAvatar"); // Get user tag from cookie
        }

        return new CookieUser(userAsTag, userAvatar); // Return user data
    }

    /**
     * {@see https://discord.com/developers/docs/resources/user#get-current-user}
     *
     * @param oAuth2Token
     * @return
     */
    private static JSONObject getUser(String oAuth2Token) {
        final okhttp3.Request request = new okhttp3.Request.Builder()
                .url(Utils.DISCORD_BASE_URL + "/users/@me")
                .addHeader("Authorization", "Bearer " + oAuth2Token)
                .get()
                .build();

        try (okhttp3.Response response = Utils.HTTP_CLIENT.newCall(request).execute()) {
            JSONObject json = new JSONObject(response.body().string()); // Create JSONObject
            System.out.println(json);
            return json;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<FeaturedGuild> getUserGuilds(Request req, Response res) throws LoginRedirection {
        final String oAuth2Token = OAuth2.getToken(req, res); // Get access token
        System.out.println(oAuth2Token);

        final okhttp3.Request request = new okhttp3.Request.Builder()
                .url(GUILD_URL)
                .addHeader("Authorization", "Bearer " + oAuth2Token)
                .get()
                .build();
        try (okhttp3.Response response = Utils.HTTP_CLIENT.newCall(request).execute()) {
            final String jsonResponse = response.body().string(); // Get response

            // Check for unauthorized
            if (jsonResponse.equals(Utils.ERROR_UNAUTHORIZED) && oAuth2Token != null) {
                System.out.println(req.cookies());
                System.out.println("Something went wrong, trying to reauthorize user");
                OAuth2.refreshToken(req, res); // Refresh token
                return getUserGuilds(req, res); // Rerun method
            }

            final List<FeaturedGuild> featuredGuilds = new ArrayList<>();
            final JSONArray guilds = new JSONArray(jsonResponse); // Parse response to a JSONArray
            for (int i = 0; i < guilds.length(); i++) {
                JSONObject guild = guilds.getJSONObject(i);
                featuredGuilds.add(new FeaturedGuild(guild)); // Add guild
            }
            return featuredGuilds;
        }
        // Exceptions thrown by the http request
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public enum UserBadge {
        // Myra badges
        MYRA_STAFF("Myra Staff"),
        MYRA_PARTNER("Myra Partner"),
        // HypeSquad
        HYPESQUAD("HypeSquad Events"),
        HYPESQUAD_BRAVERY("HypeSquad Bravery"),
        HYPESQUAD_BRILLIANCE("HypeSquad Brilliance"),
        HYPESQUAD_BALANCE("HypeSquad Balance"),
        // Other
        STAFF("Discord Employee"),
        PARTNER("Partnered Server Owner"),
        BUG_HUNTER_LEVEL_1("Bug Hunter Level 1"),
        EARLY_SUPPORTER("Early Supporter"),
        TEAM_USER("Team User"),
        BUG_HUNTER_LEVEL_2("Bug Hunter Level 2"),
        VERIFIED_BOT("Verified Bot"),
        VERIFIED_DEVELOPER("Early Verified Bot Developer"),

        UNKNOWN("Unknown");

        private final String name;

        UserBadge(String name) {
            this.name = name;
        }

        /**
         * Finds the matching {@link UserBadge}. This method is not case sensitive.
         *
         * @param search The search query to search in the enum.
         * @return Returns a matching {@link UserBadge} to the search.
         */
        public static UserBadge find(String search) {
            return Arrays.stream(UserBadge.values())
                    .filter(badge -> badge.getName().equalsIgnoreCase(search))
                    .findAny()
                    .get();
        }

        /**
         * The readable name as used in the Discord Client.
         *
         * @return The readable name of this UserFlag.
         */
        public String getName() {
            return this.name;
        }
    }

    public static class FeaturedGuild {
        public final String id;
        public final String name;
        public final String icon;
        public final Boolean isOwner;
        public final Long permissions;
        public final List<String> features;

        public FeaturedGuild(@NotNull JSONObject json) {
            this.id = json.getString("id");
            this.name = json.getString("name");
            this.icon = json.optString("icon", null);
            this.isOwner = json.optBoolean("isOwner", false);
            this.permissions = json.getLong("permissions");

            final List<String> features = new ArrayList<>();
            for (int i = 0; i < json.getJSONArray("features").length(); i++) {
                features.add(json.getJSONArray("features").getString(i));
            }
            this.features = features;
        }

    }

    /**
     * Stores information about the user.
     */
    public static class CookieUser {
        private String tag;
        public String avatarUrl;

        public CookieUser(String tag, String avatarUrl) {
            this.tag = URLDecoder.decode(tag, StandardCharsets.UTF_8); // Decode tag
            this.avatarUrl = avatarUrl;
        }

        public String getTag() {
            return this.tag;
        }
    }
}
