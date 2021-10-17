/*let states = document.getElementById("statedd");
var stateList = ["AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "DC", "FL", "GA", "HI", "ID", "IL", "IN", "IA", "KS", "KY", "LA", "ME", "MD", "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ", "NM", "NY", "NC", "ND", "OH", "OK", "OR", "PA", "RI", "SC", "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY"];
stateList.forEach(state => {
    states.innerHTML += '\n<form:option value="' + state + '">'+ state + '</form:option>';
});
states.selectedIndex = 38;*/      // PA

function validateForm() {
    let name = document.getElementById("name").value;
    let email = document.getElementById("email").value;
    let password = document.getElementById("password").value;
    let passwordConfirm = document.getElementById("passwordConfirm").value;
    let phone = document.getElementById("phone").value;
    let validForm = true;
    const re = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    if (re.test(String(email).toLowerCase())==false) {
        alert("Email must be in form \"johndoe@site.com\"! Please correct.");
        validForm = false;
    }
    if (pass != passchk) {
        alert("Passwords do not match! Please correct.");
        validForm = false;
    }
    if (Number.isNaN(parseInt(phone)) || phone.length != 10) {
        alert(`Invalid phone number entered (${phone})! Please correct.`);
        validForm = false;
    }
    return validForm;
}