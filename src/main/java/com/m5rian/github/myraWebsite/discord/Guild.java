package com.m5rian.github.myraWebsite.discord;

import com.m5rian.github.myraWebsite.database.Db;
import com.m5rian.github.myraWebsite.utilities.Utils;
import org.json.JSONObject;

import java.io.IOException;

import static com.mongodb.client.model.Filters.eq;

public class Guild {

    public static DiscordGuild getGuild(String guildId) {
        final okhttp3.Request request = new okhttp3.Request.Builder()
                .url(Utils.DISCORD_BASE_URL + "/guilds/" + guildId)
                .addHeader("Authorization", "Bot " + Utils.BOT_TOKEN)
                .get()
                .build();
        try (okhttp3.Response response = Utils.HTTP_CLIENT.newCall(request).execute()) {
            final JSONObject json = new JSONObject(response.body().string()); // Parse response to JSONObject
            return new DiscordGuild(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean hasMyra(String guildId) {
        return Db.getDatabase().getCollection("guilds").find(eq("guildId", guildId)).first() != null;
    }

    public static class DiscordGuild {
        public String id;
        public String name;
        public String icon;

        public DiscordGuild(JSONObject json) {
            id = json.getString("id");
            name = json.getString("name");
            icon = Utils.getGuildIcon(this.id, json.optString("icon", null));
        }
    }

}
