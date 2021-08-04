function checkForInputBox() {
    const editMessage = document.querySelector("#edit");
    const messageIdInput = document.querySelector("#edit-message-id");

    // Button got clicked
    if(editMessage.checked) {
        messageIdInput.classList.add("active");
    }
    // Button was deselected
    else {
        messageIdInput.classList.remove("active");
    }
}