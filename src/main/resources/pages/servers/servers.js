function toDashboard(element) {
    const guildId = element.parentElement.getAttribute("guildId"); // Get guild id
    const dashboard = window.location.origin + "/dashboard/" + guildId;

    window.location.href = dashboard;
}

function toInvite() {
    const loginUrl = window.location.origin + "/invite";
    window.location.href = loginUrl;
}