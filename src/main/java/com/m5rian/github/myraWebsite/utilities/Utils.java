package com.m5rian.github.myraWebsite.utilities;

import okhttp3.OkHttpClient;
import org.bson.Document;
import org.json.JSONObject;
import spark.Response;

import java.io.IOException;
import java.math.BigInteger;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.Random;

public class Utils {
    public static final String LOCAL_ADDRESS = "http://myra.dyndns1.de";
    public static final String SERVER_ADDRESS = "http://www.myra.bot";
    public static String CURRENT_ADDRESS;
    public static String BOT_ADDRESS;
    public static final Integer WEBSITE_PORT = 82;
    public static final Integer BOT_PORT = 1027;

    public static final OkHttpClient HTTP_CLIENT = new OkHttpClient();
    public static final String DISCORD_BASE_URL = "https://discord.com/api";
    /**
     * {@see https://discord.com/developers/docs/reference#image-data}
     */
    public static final String DISCORD_IMAGE_BASE_URL = "https://cdn.discordapp.com/"; // Discord's base image url
    // IDs and Tokens
    public static final String APPLICATION_ID = "718444709445632122";
    public static final String BOT_TOKEN = "NzE4NDQ0NzA5NDQ1NjMyMTIy.Xto9xg.ScXvpTLGPkMBp0EP-mlLUCErI8Y";

    public static final String blue = "#7AC8F2";
    public static final String red = "#C16B65";
    public static final String MYRA_ICON = "https://github.com/MyraBot/resources/blob/main/logo.png?raw=true";
    public static final Long EMBED_EXPIRE = 172800000L;
    // Errors
    public static final String ERROR_UNAUTHORIZED = "{\"message\": \"401: Unauthorized\", \"code\": 0}";
    public static final String ERROR_INVALID_GRANT = "{\"error\": \"invalid_grant\"}"; //
    private static final String DEFAULT_AVATAR_RED = "https://cdn.discordapp.com/embed/avatars/4.png";

    public static void setup() {
        final String os = System.getProperty("os.name");
        if (os.startsWith("Windows")) {
            CURRENT_ADDRESS = LOCAL_ADDRESS;
        } else {
            CURRENT_ADDRESS = SERVER_ADDRESS;
        }

        final okhttp3.Request request = new okhttp3.Request.Builder()
                .url(SERVER_ADDRESS + ":" + Utils.BOT_PORT + "/api/retrieve/guild")
                .addHeader("guildId", "642809436515074053")
                .get()
                .build();
        try (okhttp3.Response response = Utils.HTTP_CLIENT.newCall(request).execute()) {
            BOT_ADDRESS = SERVER_ADDRESS;
        } catch (IOException e) {
            BOT_ADDRESS = LOCAL_ADDRESS;
        }

        System.out.println("Bot address " + BOT_ADDRESS);
    }

    /**
     * Check if a permission number contains the "Manage server" permission.
     * {@see https://discord.com/developers/docs/topics/permissions}
     *
     * @param permissions The permission number to check.
     */
    public static boolean isAdministrator(long permissions) {
        final BigInteger manageGuildPermission = BigInteger.valueOf(0x00000020); // Get manager server permission

        BigInteger and = BigInteger.valueOf(permissions).and(manageGuildPermission);
        return and.equals(manageGuildPermission);
    }

    public static Object redirect(Response res, String url) {
        res.redirect(url);
        return null;
    }

    public static String getUserAvatar(String userId, String avatarId) {
        final String avatarUrl = DISCORD_IMAGE_BASE_URL + "avatars/%s/%s.%s";
        return avatarId == null ? DEFAULT_AVATAR_RED : String.format(avatarUrl, userId, avatarId, avatarId.startsWith("a_") ? "gif" : "png");
    }

    public static String getGuildIcon(String guildId, String iconId) {
        final String iconUrl = DISCORD_IMAGE_BASE_URL + "icons/%s/%s.%s";
        return iconId == null ? DEFAULT_AVATAR_RED : String.format(iconUrl, guildId, iconId, iconId.startsWith("a_") ? "gif" : "png");
    }

    /**
     * Add '.' separators to show the number more nicely.
     *
     * @param number The number to format.
     * @return Returns the formatted number as a String.
     */
    public static String formatNumber(int number) {
        return NumberFormat.getInstance().format(number);
    }

    public static String toTimeExact(long millis) {
        final long seconds = (millis / 1000) % 60;
        final long minutes = (millis / (1000 * 60)) % 60;
        final long hours = (millis / (1000 * 60 * 60)) % 24;
        final long days = (millis / (1000 * 60 * 60 * 24));

        return String.format("%02dd %02dh %02dmin %02ds", days, hours, minutes, seconds);
    }

    /**
     * @param document The document to get the long from.
     * @param key      The key of the long value.
     * @return Returns a long even if BSON reads an Integer.
     */
    public static Long getBsonLong(Document document, String key) {
        try {
            return document.getLong(key);
        }
        // If voice call time is an integer
        catch (ClassCastException e) {
            return Long.valueOf(document.getInteger(key)); // Parse to long
        }
    }

    /**
     * @param xp The experience, which should get converted to a level.
     * @return Returns the level calculated by the experience.
     */
    public static int getLevelFromXp(long xp) {
        // Parabola
        long dividedNumber = xp / 5;
        double exactLevel = Math.sqrt(dividedNumber);
        return (int) exactLevel;
    }

    /**
     * @param queryParameters The parameters as a whole String to parse.
     * @return Returns a {@link JSONObject} with the queryParameters.
     */
    public static JSONObject queryParamsToJson(String queryParameters) {
        JSONObject json = new JSONObject();
        for (String pair : queryParameters.split("&")) {
            final String[] keyValue = pair.split("="); // Get key and value
            json.put(keyValue[0], keyValue.length < 2 ? "" : URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8));
        }
        return json;
    }

    /**
     * @param length The length of the code.
     * @param chars  The characters to use.
     * @return Returns a code with random characters of the provided ones.
     */
    public static String generateCode(int length, String chars) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) {
            result.append(chars.toCharArray()[new Random().nextInt(chars.length())]); // Append random character
        }

        return result.toString();
    }
}
