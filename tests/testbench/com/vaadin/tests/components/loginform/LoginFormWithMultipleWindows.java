package com.vaadin.tests.components.loginform;

import com.vaadin.Application;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.ui.LoginForm;
import com.vaadin.ui.LoginForm.LoginEvent;
import com.vaadin.ui.LoginForm.LoginListener;
import com.vaadin.ui.Root;

@SuppressWarnings("serial")
public class LoginFormWithMultipleWindows extends Application {

    @Override
    protected Root getRoot(WrappedRequest request) {
        return new LoginFormWindow();
    }

    public class LoginFormWindow extends Root {
        public LoginFormWindow() {
            super();

            LoginForm loginForm = new LoginForm();
            loginForm.setSizeUndefined();

            loginForm.addListener(new LoginListener() {

                private static final long serialVersionUID = 1L;

                public void onLogin(LoginEvent event) {
                    showNotification(event.getLoginParameter("username") + ":"
                            + event.getLoginParameter("password"));

                }
            });

            addComponent(loginForm);
        }
    }

}
