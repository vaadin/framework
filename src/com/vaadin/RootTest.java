package com.vaadin;

import com.vaadin.terminal.WrappedRequest;
import com.vaadin.ui.Label;
import com.vaadin.ui.Root;
import com.vaadin.ui.VerticalLayout;

public class RootTest extends Root {
    @Override
    public void init(WrappedRequest request) {
        VerticalLayout layout = new VerticalLayout();

        layout.addComponent(new Label("Hello root"));

        setContent(layout);
    }
}
