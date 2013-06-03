package com.vaadin.tests.minitutorials.v7b9;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class LoginView extends Panel implements View {

    public static final String NAME = "login";

    public LoginView(final Navigator navigator,
            final String fragmentAndParameters) {
        Layout layout = new VerticalLayout();

        final TextField email = new TextField("Email");
        layout.addComponent(email);

        final PasswordField password = new PasswordField("Password");
        layout.addComponent(password);

        final Button login = new Button("Login", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                Notification.show("Ok, let's pretend you're " + email);

                // indicate the user is logged in
                ((NavigationtestUI) UI.getCurrent()).setLoggedInUser(email
                        .getValue());

                // navigate back to the intended place
                navigator.navigateTo(fragmentAndParameters);
            }
        });
        layout.addComponent(login);
        setContent(layout);

    }

    @Override
    public void enter(ViewChangeEvent event) {
        // TODO Auto-generated method stub

    }
}
