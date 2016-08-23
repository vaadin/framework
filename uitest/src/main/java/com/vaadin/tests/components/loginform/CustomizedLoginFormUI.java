package com.vaadin.tests.components.loginform;

import java.util.Optional;

import com.vaadin.server.VaadinRequest;

public class CustomizedLoginFormUI extends LoginFormUI {

    @Override
    protected void setup(VaadinRequest request) {
        super.setup(request);
    }

    @Override
    protected Optional<String> getUsernameCaption() {
        return Optional.of("Identifiant");
    }

    @Override
    protected Optional<String> getPasswordCaption() {
        return Optional.of("Mot de passe");
    }

    @Override
    protected Optional<String> getLoginCaption() {
        return Optional.of("Se connecter");
    }

    @Override
    protected String getTestDescription() {
        return "Customization of the captions on the LoginForm component. Three login forms should be visible (undefined height, undefined width, defined height and width). Entering a username+password in a login form and clicking 'login' should replace the login form with a label telling the user name as password. Also a logout button should then be shown and pressing that takes the user back to the original screen with the LoginForm";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5226;
    }

}
