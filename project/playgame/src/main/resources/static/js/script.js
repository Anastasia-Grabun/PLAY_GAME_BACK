document.addEventListener("DOMContentLoaded", function () {
    // Обработка формы логина
    const loginForm = document.getElementById("loginForm");
    if (loginForm) {
        loginForm.addEventListener("submit", function (event) {
            event.preventDefault();

            const login = document.getElementById("login").value;
            const password = document.getElementById("password").value;

            fetch("/api/v1/authorization/login", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({ login, password }),
            })
                .then((response) => response.json())
                .then((data) => {
                    if (data.token) {
                        alert("Login successful! Token: " + data.token);
                    } else {
                        alert("Login failed!");
                    }
                })
                .catch((error) => alert("Error: " + error));
        });
    }

    // Обработка формы регистрации
    const registerForm = document.getElementById("registerForm");
    if (registerForm) {
        registerForm.addEventListener("submit", function (event) {
            event.preventDefault();

            const username = document.getElementById("username").value;
            const login = document.getElementById("login").value;
            const password = document.getElementById("password").value;
            const email = document.getElementById("email").value;

            fetch("/api/v1/authorization/register", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({ username, login, password, email }),
            })
                .then((response) => response.json())
                .then((data) => {
                    if (data.token) {
                        alert("Registration successful! Token: " + data.token);
                    } else {
                        alert("Registration failed!");
                    }
                })
                .catch((error) => alert("Error: " + error));
        });
    }
});
