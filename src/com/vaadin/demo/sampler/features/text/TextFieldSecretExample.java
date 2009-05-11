package com.vaadin.demo.sampler.features.text;

import com.vaadin.ui.Button;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

public class TextFieldSecretExample extends VerticalLayout {

    private final TextField username;
    private final TextField password;

    public TextFieldSecretExample() {
        setSizeUndefined(); // let content 'push' size
        setSpacing(true);

        // Username
        username = new TextField("Username");
        addComponent(username);

        // Password
        password = new TextField("Password");
        password.setSecret(true);
        addComponent(password);

        // Login button
        Button loginButton = new Button("Login", new Button.ClickListener() {
            // inline click listener
            public void buttonClick(ClickEvent event) {
                getWindow().showNotification(
                        "User: " + username.getValue() + " Password: "
                                + password.getValue());

            }
        });
        addComponent(loginButton);
        setComponentAlignment(loginButton, "right");

    }
}
