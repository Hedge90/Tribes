const loginForm = document.getElementById("login-form");
let responseMessage = document.getElementById("response-message");
const savedToken = window.localStorage.getItem("token");
if (savedToken != null) {
    window.location.href = "/your-kingdom";
}
document.addEventListener("submit", (event) => {
    event.preventDefault();

    const username = event.target.elements.username.value;
    const password = event.target.elements.password.value;

    const userLogin = {
        "username": username,
        "password": password
    }

    loginUser(userLogin);
});

async function loginUser(userLogin) {
    try {
        const response = await fetch("/login", {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify(userLogin),
        });
        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.message);
        } else {
            console.log(data.token)
            let token = data.token;
            window.localStorage.setItem("token", token);
            window.location.href = "/your-kingdom";
        }
    } catch (error) {
        responseMessage.classList.remove("hide");
        responseMessage.innerHTML = `<p style="color: red">${error.message}</p>`;
    }
}