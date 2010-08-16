package com.vaadin.tests.components.loginform;

import com.vaadin.Application;
import com.vaadin.ui.LoginForm;
import com.vaadin.ui.LoginForm.LoginEvent;
import com.vaadin.ui.LoginForm.LoginListener;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class LoginFormWithMultipleWindows extends Application {

    /**
     * =======================================================================
     * Comment out this to make the LoginForm work as expected
     * =======================================================================
     */
    @Override
    public Window getWindow(String name) {
        Window w = super.getWindow(name);
        if (w == null) {
            w = new LoginFormWindow();
            w.setName(name);
            addWindow(w);
        }
        return w;

    }

    @Override
    public void init() {
        setMainWindow(new LoginFormWindow());
    }

    public class LoginFormWindow extends Window {
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
