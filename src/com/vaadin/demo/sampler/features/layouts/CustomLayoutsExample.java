package com.vaadin.demo.sampler.features.layouts;

import com.vaadin.ui.Button;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class CustomLayoutsExample extends VerticalLayout {

    public CustomLayoutsExample() {
        setMargin(true);

        // Create the custom layout and set it as a component in
        // the current layout
        CustomLayout custom = new CustomLayout("examplecustomlayout");
        addComponent(custom);

        // Create components and bind them to the location tags
        // in the custom layout.
        TextField username = new TextField();
        custom.addComponent(username, "username");

        TextField password = new TextField();
        password.setSecret(true);
        custom.addComponent(password, "password");

        Button ok = new Button("Login");
        custom.addComponent(ok, "okbutton");
    }
}