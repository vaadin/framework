package com.vaadin.tests.extensions;

import com.vaadin.annotations.StyleSheet;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;

@StyleSheet("http://fonts.googleapis.com/css?family=Cabin+Sketch")
public class ResponsiveWithCrossDomainStyles extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(new Button("Make responsive",
                event -> event.getButton().setResponsive(true)));
    }

}
