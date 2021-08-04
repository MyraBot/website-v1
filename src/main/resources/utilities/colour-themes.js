const animationDuration = 2000; // Duration of theme switching animation in millis

// Wait for document to load
document.addEventListener("DOMContentLoaded", function (event) {
    updateTheme(); // Update theme
});

function switchTheme() {
    console.log("THEME SWITCHING GO BRRRRRRRR");
    var currentTheme = getCookie("theme"); // Current theme
    var newTheme = currentTheme === "dark" ? "light" : "dark" // Get new theme
    document.cookie = "theme=" + newTheme + "; expires=Thu, 18 Dec 5000 12:00:00 UTC; path=/"; // Update theme in cookies

    updateTheme();
}

function updateTheme() {
    var currentTheme = getCookie("theme"); // Get theme
    document.documentElement.setAttribute("data-theme", currentTheme); // Update theme

    var themeChanger = document.querySelector(".theme-changer"); // Get switch theme button
    //var themeChangerButton = themeChanger.querySelector(".switch-theme-button"); // Get image element
    //var themeChangeAnimation = themeChanger.querySelector(".switch-theme-animation"); // Get video element

    const videos = document.querySelectorAll("video"); // Get all videos

    // Light theme
    if (getCookie("theme") === "light") {
        // Change video styles
        videos.forEach(video => {
            video.src = video.src.replace("-dark.webm", ".webm");
        });


        //themeChangerButton.setAttribute("src", "/videos/theme-changer/sun.png"); // Change theme icon

        // Animation
        //themeChangerButton.classList.remove("active"); // Remove active class
        //themeChangeAnimation.classList.add("active"); // Add active class

        //themeChangeAnimation.currentTime = 0; // Start video at the start
        setTimeout(function () {
            //themeChangerButton.classList.add("active"); // Add active class
            //themeChangeAnimation.classList.remove("active"); // Remove active class
        }, animationDuration); // Delay
    }
    // Dark theme
    else {
        // Change video styles
        videos.forEach(video => {

            video.src = video.src.replace(".webm", "-dark.webm");
            console.log(video.src);
        });


        //themeChangerButton.setAttribute("src", "/videos/theme-changer/moon.png"); // Change theme icon

        // Animation
        //themeChangerButton.classList.remove("active"); // Remove active class
        //themeChangeAnimation.classList.add("active"); // Add active class

        //themeChangeAnimation.currentTime = 0; // Start video at the start
        setTimeout(function () {
            //themeChangerButton.classList.add("active"); // Add active class
            //themeChangeAnimation.classList.remove("active"); // Remove active class
        }, animationDuration); // Delay
    }

}

function getCookie(name) {
    const match = document.cookie.match(new RegExp('(^| )' + name + '=([^;]+)'));
    if (match) return match[2];
}