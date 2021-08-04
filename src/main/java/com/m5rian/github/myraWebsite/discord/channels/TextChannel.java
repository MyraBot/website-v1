package com.m5rian.github.myraWebsite.discord.channels;

import com.mongodb.lang.Nullable;
import org.json.JSONObject;

public class TextChannel {

    private final String guildId;

    private final String id;
    private final String name;
    private final String topic;
    private final Boolean nsfw;
    private final Integer type;
    private final Integer rateLimitPerUser;

    private final Integer position;
    private final String parentId;

    private final String lastMessageId;

    /**
     * @param json A {@link JSONObject} which contains all important data.
     */
    public TextChannel(JSONObject json) {
        this.guildId = json.getString("guild_id");

        this.id = json.getString("id");
        this.name = json.getString("name");
        this.topic = json.optString("topic", "");
        this.nsfw = json.getBoolean("nsfw");
        this.type = json.getInt("type");
        this.rateLimitPerUser = json.optInt("rate_limit_per_user", 0);

        this.position = json.getInt("position");
        this.parentId = json.optString("parent_id", null);

        this.lastMessageId = json.optString("last_message_id", null);
    }

    public String getGuildId() {
        return this.guildId;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getTopic() {
        return this.topic;
    }

    public Boolean isNsfw() {
        return this.nsfw;
    }

    public Integer getType() {
        return this.type;
    }

    public Integer getRateLimitPerUser() {
        return this.rateLimitPerUser;
    }

    public Integer getPosition() {
        return this.position;
    }

    @Nullable
    public String getParentId() {
        return this.parentId;
    }

    @Nullable
    public String getLastMessageId() {
        return this.lastMessageId;
    }
}
