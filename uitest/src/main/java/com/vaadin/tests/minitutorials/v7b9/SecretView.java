package com.vaadin.tests.minitutorials.v7b9;

import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;

public class SecretView extends MessageView {
    public static final String NAME = "secret";

    public SecretView() {
        setCaption("Private messages");

        ((Layout) getContent()).addComponent(new Label("Some private stuff."));
    }

}
