package com.vaadin.tests.layouts;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;

public class CssLayoutAbsoluteUrl extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Label label = new Label("Hello");
        label.setId("myLabel");
        addComponent(new CssLayout(label) {
            @Override
            protected String getCss(Component c) {
                return "color: blue; background-image: url(\"about:blank\");";
            }
        });
    }

}
