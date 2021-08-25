async function login(e) {
    let data = new FormData(document.getElementById('login-form'));
    let username = data.get("username");
    console.log(username);
}

document.getElementById('login-form').onsubmit = login;
