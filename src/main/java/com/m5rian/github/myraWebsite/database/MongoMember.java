package com.m5rian.github.myraWebsite.database;

import com.m5rian.github.myraWebsite.utilities.Utils;
import org.bson.Document;

public class MongoMember {
    private final String id;
    private final String name;
    private final String discriminator;
    private final String avatar;
    private final int level;
    private final long xp;
    private final long messages;
    private final long voiceCallTime;
    private final int balance;
    private final long lastClaim;
    private final String rankBackground;

    public MongoMember(Document userDocument, String guildId) {
        this.id = userDocument.getString("userId");
        this.name = userDocument.getString("name");
        this.discriminator = userDocument.getString("discriminator");
        this.avatar = userDocument.getString("avatar");

        final Document guildDocument = userDocument.get(guildId, Document.class);
        this.level = guildDocument.getInteger("level");
        this.xp = Utils.getBsonLong(guildDocument, "xp");
        this.messages = Utils.getBsonLong(guildDocument, "messages");
        this.voiceCallTime = Utils.getBsonLong(guildDocument, "voiceCallTime");
        this.balance = guildDocument.getInteger("balance");
        this.lastClaim = guildDocument.getLong("lastClaim");
        this.rankBackground = guildDocument.getString("rankBackground");
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDiscriminator() {
        return discriminator;
    }

    public String getAvatar() {
       return avatar == null ? "https://cdn.discordapp.com/embed/avatars/4.png" : this.avatar;
    }

    public int getLevel() {
        return level;
    }

    public long getXp() {
        return xp;
    }

    public long getMessages() {
        return messages;
    }

    public long getVoiceCallTime() {
        return voiceCallTime;
    }

    public int getBalance() {
        return balance;
    }

    public long getLastClaim() {
        return lastClaim;
    }

    public String getRankBackground() {
        return rankBackground;
    }
}
