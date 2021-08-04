/**
 * This funtion is called to authorize the users discord account.
 * @returns {boolean}
 */
function login() {
    // Add new cookie
    const currentUrl = window.location.href; // Get current url
    document.cookie = "fromPage=" + currentUrl; // Add page url before redirecting

    const loginUrl = window.location.origin + "/login";
    window.location.href = loginUrl;
}

function error() {
    const idiot = user.name;
    for (i in idiots) {
        if(i.name === idiot) {
            const mailAction = idiot.locate.sendMail("Found you!");
            if(error.code == 404) mailAction.append("You were on the wrong page my friend");
            else if(error.code == 401) mailAction.append("the police will chase you!");
        }
    }
}