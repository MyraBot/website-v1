/**
 * Removes space in prefix input.
 */
document.addEventListener('DOMContentLoaded', function () {
    // Run function after a key is pressed in the prefix input
    const prefixInput = document.querySelector("#prefix-input"); // Get reference to input
    prefixInput.addEventListener("keyup", event => {
        // Pressed key is a space
        if (event.key === " ") {
            prefixInput.value = prefixInput.value.replaceAll(" ", ""); // Replace all spaces
        }
    });
}, false);