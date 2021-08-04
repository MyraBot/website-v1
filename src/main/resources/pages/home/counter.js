const API_URL = window.location.origin + "/api";
const duration = 1000; // Duration in milliseconds to update a stat

/**
 * Fetch required data to update stats.
 */
document.addEventListener('DOMContentLoaded', function () {
    // Members
    fetch(API_URL + "/users")
        .then(data => data.json())
        .then(result => listenToScroll(result));
    // Guilds
    fetch(API_URL + "/guilds")
        .then(data => data.json())
        .then(result => listenToScroll(result));

}, false);

/**
 * Contains an event listener, which fires once the user scrolls.
 */
function listenToScroll(response) {
    let inView = false;
    let element;
    if (response.type === "users") element = document.querySelector(".stat.users p");
    else if (response.type === "guilds") element = document.querySelector(".stat.guilds p");

    const start = 0;
    const end = response.value;
    let timer;

    console.log(element);

    window.addEventListener('scroll', () => {

        // Stats are new in viewport
        if (inView === false && isInViewport(element)) {
            inView = true; // Stats are in view

            /*let current = 0;
            timer = setInterval(() => {
                current += 1;
                element.innerHTML = current;
                // Stop interval
                if (current >= total) clearInterval(timer);
            }, total / duration * 1000  / current);*/


            var range = end - start; // assumes integer values for start and end
            var minTimer = 50; // no timer shorter than 50ms (not really visible any way)
            var stepTime = Math.abs(Math.floor(duration / range)); // calc step time to show all interediate values
            stepTime = Math.max(stepTime, minTimer); // never go below minTimer

            // get current time and calculate desired end time
            var startTime = new Date().getTime();
            var endTime = startTime + duration;

            function run() {
                var now = new Date().getTime();
                var remaining = Math.max((endTime - now) / duration, 0);
                var value = Math.round(end - (remaining * range));
                element.innerHTML = value;
                if (value === end) {
                    clearInterval(timer);
                }
            }

            timer = setInterval(run, stepTime);
            run();

        }

        //  Stats aren't longer in viewport
        if (inView === true && !isInViewport(element)) {
            inView = false; // Stats are no longer in view

            element.innerHTML = 0; // Reset value
            clearInterval(timer); // Stop timers
        }
    }, false);
}

/**
 * This function checks if an element is in the view of the user.
 *
 * @param element The element you want to check the viewport.
 * @returns {boolean} Is the element in the view of the user?
 */
function isInViewportOld(element) {
    let top = element.offsetTop;
    let left = element.offsetLeft;
    let width = element.offsetWidth;
    let height = element.offsetHeight;

    while (element.offsetParent) {
        element = element.offsetParent;
        top += element.offsetTop;
        left += element.offsetLeft;
    }

    return (
        top < (window.pageYOffset + window.innerHeight) &&
        left < (window.pageXOffset + window.innerWidth) &&
        (top + height) > window.pageYOffset &&
        (left + width) > window.pageXOffset
    );
}

/**
 * This function checks if an element is in the view of the user.
 *
 * @param element The element you want to check the viewport.
 * @returns {boolean} Is the element in the view of the user?
 */
function isInViewport(element) {
    const rect = element.getBoundingClientRect();
    const html = document.documentElement;
    return (
        rect.top >= 0 &&
        rect.left >= 0 &&
        rect.bottom <= (window.innerHeight || html.clientHeight) &&
        rect.right <= (window.innerWidth || html.clientWidth)
    );
}