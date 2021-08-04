package com.m5rian.github.myraWebsite.database;

import com.m5rian.github.myraWebsite.discord.DiscordUser;
import com.m5rian.github.myraWebsite.utilities.Utils;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class MongoUser {
    public final String id;
    public final String name;
    public final String discriminator;
    public final String avatar;
    public final List<DiscordUser.UserBadge> badges;
    public final Long xp;
    public final Long messages;

    public MongoUser(Document document) {
        this.id = document.getString("userId");
        this.name = document.getString("name");
        this.discriminator = document.getString("discriminator");
        this.avatar = document.getString("avatar");

        List<DiscordUser.UserBadge> badges = new ArrayList<>();
        for (String badge : document.getList("badges", String.class)) {
            badges.add(DiscordUser.UserBadge.find(badge));
        }
        this.badges = badges;

        this.xp = Utils.getBsonLong(document, "xp");
        this.messages = Utils.getBsonLong(document, "messages");
    }

}
