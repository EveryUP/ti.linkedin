var linkedin = require('ti.linkedin');

// Setting up linkedin permissions and module itself
linkedin.permissions = [linkedin.PERMISSION_BASIC_PROFILE, linkedin.PERMISSION_EMAIL_ADDRESSES];
linkedin.initialize();

var window = Ti.UI.createWindow({
    backgroundColor: "white"
});

window.add(Ti.UI.createView({
    layout: "vertical",
    height: Ti.UI.SIZE
}));

// Adding login and logout buttons
var login = Ti.UI.createButton({
    title: 'Login',
    enabled: true
});

var logout = Ti.UI.createButton({
    top: 20,
    title: 'Logout',
    enabled: false
});

window.children[0].add(login);
window.children[0].add(logout);

// Adding listeners
linkedin.addEventListener('login', onLogin);
linkedin.addEventListener('logout', onLogout);

login.addEventListener("click", function() { linkedin.authorize() });
logout.addEventListener("click", function() { linkedin.logout() });

// Opening window
window.open();

// Handlers
function onLogin(event) {
    Ti.API.info('User is logged in?', linkedin.loggedIn);
    Ti.API.info(JSON.stringify(event));

    if (!event.success) {
        return;
    }

    login.enabled = false;
    logout.enabled = true;
}

function onLogout(event) {
    Ti.API.info('User is logged in?', linkedin.loggedIn);

    login.enabled = true;
    logout.enabled = false;
}
