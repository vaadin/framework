package com.itmill.toolkit.demo.sampler.features.layouts;

import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.CustomLayout;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.VerticalLayout;

public class CustomLayoutsExample extends VerticalLayout {

    public CustomLayoutsExample() {
        // Create the custom layout and set it as a component in
        // the current layout
        CustomLayout custom = new CustomLayout("examplecustomlayout");
        addComponent(custom);

        // Create components and bind them to the location tags
        // in the custom layout.
        TextField username = new TextField();
        custom.addComponent(username, "username");

        TextField password = new TextField();
        custom.addComponent(password, "password");

        Button ok = new Button("Login");
        custom.addComponent(ok, "okbutton");
    }
}