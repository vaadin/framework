package com.vaadin.tests.components.loginform;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.LoginForm;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.LoginForm.LoginEvent;
import com.vaadin.ui.LoginForm.LoginListener;

public class LoginFormTest extends TestBase {

    private LoginForm loginForm;

    @Override
    protected void setup() {
        loginForm = new LoginForm();
        getLayout().setSizeFull();
        loginForm.addListener(new LoginListener() {

            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            public void onLogin(LoginEvent event) {
                login(event.getLoginParameter("user"), event
                        .getLoginParameter("password"));

            }
        });
        addComponent(loginForm);

    }

    protected void login(String user, String password) {
        Label info = new Label("User '" + user + "', password='" + password
                + "' logged in");
        getLayout().removeAllComponents();
        getLayout().addComponent(info);
        getLayout().addComponent(new Button("Log out", new ClickListener() {

            public void buttonClick(ClickEvent event) {
                getLayout().removeAllComponents();
                getLayout().addComponent(loginForm);
            }

        }));

    }

    @Override
    protected String getDescription() {
        return "Basic test for the LoginForm component. The login form should be visible. Entering a username+password and clicking 'login' should replace the login form with a label telling the user name as password. Also a logout button should then be shown and pressing that takes the user back to the original screen with the LoginForm";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3597;
    }

}
