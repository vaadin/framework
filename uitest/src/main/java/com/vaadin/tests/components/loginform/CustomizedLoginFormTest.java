package com.vaadin.tests.components.loginform;

public class CustomizedLoginFormTest extends LoginFormTest {

    @Override
    protected void setup() {
        super.setup();

        loginForm.setUsernameCaption("Identifiant");
        loginForm.setPasswordCaption("Mot de passe");
        loginForm.setLoginButtonCaption("Se connecter");
    }

    @Override
    protected String getDescription() {
        return "Customization of the captions on the LoginForm component. Three login forms should be visible (undefined height, undefined width, defined height and width). Entering a username+password in a login form and clicking 'login' should replace the login form with a label telling the user name as password. Also a logout button should then be shown and pressing that takes the user back to the original screen with the LoginForm";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5226;
    }

}
