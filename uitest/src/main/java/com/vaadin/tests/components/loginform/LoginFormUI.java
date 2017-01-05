package com.vaadin.tests.components.loginform;

import java.util.Optional;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.LoginForm;
import com.vaadin.ui.VerticalLayout;

public class LoginFormUI extends AbstractReindeerTestUI {

    private HorizontalLayout loginFormLayout;
    protected LoginForm loginForm;

    @Override
    protected void setup(VaadinRequest request) {
        loginFormLayout = new HorizontalLayout();

        loginForm = new LoginForm();
        loginForm.setSizeUndefined();
        getUsernameCaption().ifPresent(loginForm::setUsernameCaption);
        getPasswordCaption().ifPresent(loginForm::setPasswordCaption);
        getLoginCaption().ifPresent(loginForm::setLoginButtonCaption);
        updateCaption();
        loginForm.addLoginListener(event -> {
            login(event.getSource(), event.getLoginParameter("username"),
                    event.getLoginParameter("password"));
        });

        loginFormLayout.addComponent(loginForm);

        Button changeWidth = new Button("Change width",
                (ClickListener) event -> {
                    if (loginForm.getWidth() < 0) {
                        loginForm.setWidth("300px");
                    } else {
                        loginForm.setWidth(null);
                    }
                    updateCaption();
                });

        Button changeHeight = new Button("Change height",
                (ClickListener) event -> {
                    if (loginForm.getHeight() < 0) {
                        loginForm.setHeight("200px");
                    } else {
                        loginForm.setHeight(null);
                    }
                    updateCaption();
                });

        addComponent(loginFormLayout);
        addComponent(changeWidth);
        addComponent(changeHeight);

    }

    protected Optional<String> getUsernameCaption() {
        return Optional.empty();
    }

    protected Optional<String> getPasswordCaption() {
        return Optional.empty();
    }

    protected Optional<String> getLoginCaption() {
        return Optional.empty();
    }

    protected void updateCaption() {
        float width = loginForm.getWidth();
        float height = loginForm.getHeight();

        String w = width < 0 ? "auto" : (int) width + "px";
        String h = height < 0 ? "auto" : (int) height + "px";

        loginForm.setCaption("LoginForm (" + w + "/" + h + ")");
    }

    protected void login(LoginForm loginForm, String user, String password) {
        VerticalLayout infoLayout = new VerticalLayout();

        Label info = new Label(
                "User '" + user + "', password='" + password + "' logged in");
        info.setId("info");
        Button logoutButton = new Button("Log out", (ClickListener) event -> {
            Button b = event.getButton();
            loginFormLayout.replaceComponent(b.getParent(),
                    (LoginForm) b.getData());
        });
        logoutButton.setData(loginForm);

        infoLayout.addComponent(info);
        infoLayout.addComponent(logoutButton);

        loginFormLayout.replaceComponent(loginForm, infoLayout);

    }

    @Override
    protected String getTestDescription() {
        return "Basic test for the LoginForm component.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3597;
    }

}
