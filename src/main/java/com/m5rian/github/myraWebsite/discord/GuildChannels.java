package com.m5rian.github.myraWebsite.discord;

import com.m5rian.github.myraWebsite.utilities.Utils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

/**
 * For channel types {@see https://discord.com/developers/docs/resources/channel#channel-object-channel-types}.
 */
public class GuildChannels {
    // Channel types
    public static int GUILD_TEXT = 0;
    public static int GUILD_VOICE = 2;
    public static int GUILD_CATEGORY = 4;
    public static int GUILD_NEWS = 5;
    public static int GUILD_STORE = 6;

    public static JSONArray getChannels(String guildId) {
        final okhttp3.Request request = new okhttp3.Request.Builder().url(Utils.DISCORD_BASE_URL + "/guilds/" + guildId + "/channels").addHeader("Authorization", "Bot " + Utils.BOT_TOKEN).get().build();
        try (okhttp3.Response response = Utils.HTTP_CLIENT.newCall(request).execute()) {
            return new JSONArray(response.body().string());
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    public static TextChannel getChannelById(String channelId) {
        final okhttp3.Request request = new okhttp3.Request.Builder().url(Utils.DISCORD_BASE_URL + "/channels/" + channelId).addHeader("Authorization", "Bot " + Utils.BOT_TOKEN).get().build();
        try (okhttp3.Response response = Utils.HTTP_CLIENT.newCall(request).execute()) {
            final JSONObject jsonChannel = new JSONObject(response.body().string());
            return new TextChannel(jsonChannel);
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    public static class TextChannel {
        public String id;
        public String latestMessageId;
        public int type;
        public String name;
        public int position;
        public String parentId;
        public String topic;
        public String guildId;
        public boolean nsfw;

        /**
         * When no topic is set Discord returns null. That's why I'm using {@link JSONObject#optString(String, String)}, instead of {@link JSONObject#getString(String)}.
         *
         * @param jsonChannel A {@link JSONObject} which contains all important data.
         */
        public TextChannel(JSONObject jsonChannel) {
            this.id = jsonChannel.getString("id");
            this.latestMessageId = jsonChannel.optString("last_message_id");
            this.type = jsonChannel.getInt("type");
            this.name = jsonChannel.getString("name");
            this.position = jsonChannel.getInt("position");
            this.parentId = jsonChannel.optString("parent_id");
            this.topic = jsonChannel.optString("topic", "");
            this.nsfw = jsonChannel.getBoolean("nsfw");
        }
    }

}
