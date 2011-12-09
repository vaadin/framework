package com.vaadin.tests.components.button;

import com.vaadin.tests.components.AbstractTestCase;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Root;
import com.vaadin.ui.VerticalLayout;

public class ButtonsInHorizontalLayout extends AbstractTestCase {

    @Override
    public void init() {
        VerticalLayout content = new VerticalLayout();
        content.setMargin(true);
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSpacing(true);
        layout.addComponent(new Button(
                "Look at me in IE7 or IE8 in compatibility mode"));
        layout.addComponent(new Button(
                "Look at me in IE7 or IE8 in compatibility mode"));
        layout.addComponent(new Button(
                "Look at me in IE7 or IE8 in compatibility mode"));
        content.addComponent(layout);
        setMainWindow(new Root("", content));
    }

    @Override
    protected String getDescription() {
        return "Tests for rendering of buttons in a HorizontalLayout";
    }

    @Override
    protected Integer getTicketNumber() {
        return 7978;
    }

}
