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