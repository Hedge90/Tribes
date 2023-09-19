const registrationForm = document.getElementById("registration-form");
let responseMessage = document.getElementById('response-message');
const savedToken = window.localStorage.getItem("token");
if (savedToken != null) {
    window.location.href = "/your-kingdom";
}
registrationForm.addEventListener('submit', (event) => {
    event.preventDefault();
    const username = event.target.elements.username.value;
    const email = event.target.elements.email.value;
    const password = event.target.elements.password.value;
    const passwordRepeat = event.target.elements['password-repeat'].value;
    const kingdomName = event.target.elements['kingdom-name'].value;

    if (password != passwordRepeat) {
        responseMessage.classList.remove("hide");
        responseMessage.innerHTML = `<p style="color: red">Passwords are not same.</p>`
    }

    if (!validateEmail(email)) {
        responseMessage.classList.remove("hide");
        responseMessage.innerHTML = `<p style="color: red">Only valid emails accepted.</p>`
    }

    const userDTO = {
        "username": username,
        "email": email,
        "password": password,
        "kingdomname": kingdomName
    }

    signUpTheNewUser(userDTO);
});

async function signUpTheNewUser(userDTO) {
    try {
        const response = await fetch("/register", {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify(userDTO),
        });
        const data = await response.json();
        responseMessage.classList.remove("hide");
        if (!response.ok) {
            throw new Error(data.message);
        } else {
            responseMessage.innerHTML = `<p style="color: green">Successfully signed up!</p>`
            window.location.href = "/";
        }
    } catch (error) {
        responseMessage.innerHTML = `<p style="color: red">${error.message}</p>`;
    }
}

function validateEmail(mail) {
    let regex = new RegExp("([!#-'*+/-9=?A-Z^-~-]+(\.[!#-'*+/-9=?A-Z^-~-]+)*|\"\(\[\]!#-[^-~ \t]|(\\[\t -~]))+\")@([!#-'*+/-9=?A-Z^-~-]+(\.[!#-'*+/-9=?A-Z^-~-]+)*|\[[\t -Z^-~]*])");
    if (regex.test(mail)) {
        return true;
    } else {
        return false;
    }
}