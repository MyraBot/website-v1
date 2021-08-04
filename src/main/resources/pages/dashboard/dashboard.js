/**
 * Toggle the class list "active" on the changes-option element
 * which will set the opacity of the element to 1.
 */
 function onChangeMade() {
  document.querySelector(".alert-window").classList.add("active");
}

/**
 * Marks current tab as active.
 */
document.addEventListener(
  "DOMContentLoaded",
  function () {
    const currentUrl = window.location.href;
    const lastTab = currentUrl.substring(
      currentUrl.lastIndexOf("/") + 1,
      currentUrl.length
    ); // Get last part of url

    document.querySelector(".category-item." + lastTab).classList.add("active"); // Add active class
  },
  false
);

/**
 * Send the form-data as a json object
 */
function sendData() {
  let elements = document.getElementById("form-data").elements; // Get all elements of the form data
  let data = {};
  let radioButtons = []; // Radio buttons which got already added to the object
  for (var i = 0; i < elements.length; i++) {
    let element = elements[i]; // Get current element

    // Element is text field
    if (element.type === "text" || element.type === "textarea") {
      data[element.name] = element.value; // Put element in object
    }

    // Element is radio button
    if (element.type === "radio") {
      // Radio button got already added to the object
      if (radioButtons.includes(element.name)) continue;

      let value = document.querySelector('input[name = "' + element.name + '"]:checked').id;
      data[element.name] = value;
    }

    // Element is drop down
    if (element.type === "select-one") {
      data[element.name] = element.value;
    }

    // Element is a checkbox
    if (element.type === "checkbox") {
      data[element.name] = element.checked;
    }
  }

  const guildId = window.location.pathname.split("/")[2]; // Get guild id from url
  // Create JSONObject
  const json = JSON.stringify({
    guildId: guildId,
    authentication: getCookie("code"),
    userId: getCookie("userId"),
    data: data
  });

  // Execute post request
  const tab = window.location.href.split("/")[ window.location.href.split("/").length - 1 ]; // Get current tab
  const url = "http://localhost:1027/api/" + tab;

  fetch(url, {
    method: 'POST',
    body: json,
    Origin: "http://localhost"
  })
    .then((response) => response.json())
    .then((data) => {
            if (document.querySelector(".alert-window") != null) {
              document.querySelector(".alert-window").classList.remove("active");
            }

            let i = 0;
            if (data.status == "200") createMessage(data.messages[i])
            else if (data.status == "422") createError(data.messages[i])  
            i++;
            // Create loop function     
            function loop() {
              setTimeout(function() {
                if (data.status == "200") createMessage(data.messages[i])
                else if (data.status == "422") createError(data.messages[i])  
                
                i++; // Increase counter
                if (i < data.messages.length) { // Run delayed loop as long as there are still errors int the array
                  loop(); // Run function for next item
                }
              }, 500) // Set delay of 0,5 seconds
            }
            
            if (data.messages.length > 1) loop(); // Start loop
    });
  }

function createMessage(message) {
  // Create message
  const p = document.createElement("p");
  p.innerHTML = message;
  const div = document.createElement("div");
  div.classList.add("notification-popup", "success-info");
  div.appendChild(p);
  // Append message to other messages
  document.querySelector(".errors-stacker").appendChild(div);
}

function createError(message) {
  // Create error
  const p = document.createElement("p");
  p.innerHTML = message;
  const div = document.createElement("div");
  div.classList.add("notification-popup", "error-info");
  div.appendChild(p);
  // Append error to other errors
  document.querySelector(".errors-stacker").appendChild(div);
}

  /**
 * 
 * @param   {String} name Key of cookie. 
 * @returns {String}      The found value of a cookie by the given key.
 */
function getCookie(name) {
  const match = document.cookie.match(new RegExp('(^| )' + name + '=([^;]+)'));
  if (match) return match[2];
}