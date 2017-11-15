package com.vaadin.tests.components.loginform;

import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.LoginForm;

@SuppressWarnings("serial")
public class LoginFormWithMultipleWindows extends LegacyApplication {

    @Override
    public void init() {
        setMainWindow(new LoginFormWindow());
    }

    public static class LoginFormWindow extends LegacyWindow {
        public LoginFormWindow() {
            super();

            LoginForm loginForm = new LoginForm();
            loginForm.setSizeUndefined();

            loginForm.addLoginListener(event -> showNotification(
                    event.getLoginParameter("username") + ":"
                            + event.getLoginParameter("password")));

            addComponent(loginForm);
        }
    }

}
