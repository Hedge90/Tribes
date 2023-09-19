'use strict';
const token = window.localStorage.getItem("token");
const body = document.getElementsByTagName("body")[0];

if (token == null) {
    body.innerHTML = "<h1>Please log in!</h1>" +
        "<a href='/'>Login page</a>";
} else {
    body.innerHTML = "<h1>Oops...</h1>" +
        "<p>Something went wrong!</p>" +
        "<a href='/your-kingdom'>Go back to your kingdom</a>";
}