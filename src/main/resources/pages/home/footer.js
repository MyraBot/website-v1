var topGg = "https://top.gg/bot/718444709445632122";
var discordBoats = "https://discord.boats/bot/718444709445632122";
var invite = "invite";
var support = "support";

function toTopGg() {
    window.location.href = topGg;
}

function toDiscordBoats() {
    window.location.href = discordBoats;
}

function toSupport() {
    const loginUrl = window.location.origin + "/" + support;
    window.location.href = loginUrl;
}

function toInvite() {
    const loginUrl = window.location.origin + "/" + invite;
    window.location.href = loginUrl;
}