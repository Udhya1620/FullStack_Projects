function validateForm(event) {
    // Get the button that triggered the submission
    const submitButton = event.submitter; // [[7]]

    // Bypass validation if "show" button is clicked
    if (submitButton.value === "show") {
        return true; // Allow submission without validation
    }

    // Get form values
    const fname = document.forms["employeeForm"]["fname"].value.trim();
    const lname = document.forms["employeeForm"]["lname"].value.trim();
    const uname = document.forms["employeeForm"]["uname"].value.trim();
    const password = document.forms["employeeForm"]["password"].value.trim();
    const address = document.forms["employeeForm"]["address"].value.trim();
    const contact = document.forms["employeeForm"]["contact"].value.trim();

    // Validate First Name
    if (fname === "") {
        alert("First Name is required!");
        return false;
    }
    if (!/^[A-Za-z]{3,}$/.test(fname)) {
        alert("First Name must contain only letters and be at least 3 characters long!");
        return false;
    }

    // Validate Last Name
    if (lname === "") {
        alert("Last Name is required!");
        return false;
    }
    if (!/^[A-Za-z]{3,}$/.test(lname)) {
        alert("Last Name must contain only letters and be at least 3 characters long!");
        return false;
    }

    // Validate User Name
    if (uname === "") {
        alert("User Name is required!");
        return false;
    }
    if (!/^[A-Za-z][A-Za-z0-9_]{2,}$/.test(uname)) {
        alert("User Name must start with a letter and contain only letters, numbers, or underscores!");
        return false;
    }

    // Validate Password
    if (password === "") {
        alert("Password is required!");
        return false;
    }
    if (password.length < 6) {
        alert("Password must be at least 6 characters long!");
        return false;
    }

    // Validate Address
    if (address === "") {
        alert("Address is required!");
        return false;
    }

    // Validate Contact Number
    if (contact === "") {
        alert("Contact number is required!");
        return false;
    }
    if (!/^\d{10}$/.test(contact)) {
        alert("Contact number must contain exactly 10 digits!");
        return false;
    }

    // If all validations pass
    return true;
}