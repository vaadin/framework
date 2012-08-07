package com.vaadin.tests.components.loginform;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.LoginForm;
import com.vaadin.ui.LoginForm.LoginEvent;
import com.vaadin.ui.LoginForm.LoginListener;
import com.vaadin.ui.VerticalLayout;

public class LoginFormTest extends TestBase {

    private HorizontalLayout loginFormLayout;
    protected LoginForm loginForm;

    @Override
    protected void setup() {
        loginFormLayout = new HorizontalLayout();

        loginForm = new LoginForm();
        loginForm.setSizeUndefined();

        updateCaption();
        loginForm.addListener(new LoginListener() {

            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void onLogin(LoginEvent event) {
                login((LoginForm) event.getSource(),
                        event.getLoginParameter("username"),
                        event.getLoginParameter("password"));

            }
        });

        loginFormLayout.addComponent(loginForm);

        Button changeWidth = new Button("Change width", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                if (loginForm.getWidth() < 0) {
                    loginForm.setWidth("300px");
                } else {
                    loginForm.setWidth(null);
                }
                updateCaption();
            }
        });

        Button changeHeight = new Button("Change height", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                if (loginForm.getHeight() < 0) {
                    loginForm.setHeight("200px");
                } else {
                    loginForm.setHeight(null);
                }
                updateCaption();
            }
        });

        addComponent(loginFormLayout);
        addComponent(changeWidth);
        addComponent(changeHeight);

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

        Label info = new Label("User '" + user + "', password='" + password
                + "' logged in");
        Button logoutButton = new Button("Log out", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                Button b = event.getButton();
                loginFormLayout.replaceComponent(b.getParent(),
                        (LoginForm) b.getData());
            }

        });
        logoutButton.setData(loginForm);

        infoLayout.addComponent(info);
        infoLayout.addComponent(logoutButton);

        loginFormLayout.replaceComponent(loginForm, infoLayout);

    }

    @Override
    protected String getDescription() {
        return "Basic test for the LoginForm component. Three login forms should be visible (undefined height, undefined width, defined height and width). Entering a username+password in a login form and clicking 'login' should replace the login form with a label telling the user name as password. Also a logout button should then be shown and pressing that takes the user back to the original screen with the LoginForm";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3597;
    }

}
