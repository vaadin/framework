package com.vaadin.tests.components.select;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.TwinColSelect;

public class TwinColSelectCaptionStyles extends TestBase {

    @Override
    protected void setup() {
        setTheme("tests-tickets");
        final TwinColSelect<String> sel = new TwinColSelect<>(
                "Component caption");
        sel.setLeftColumnCaption("Left caption");
        sel.setRightColumnCaption("Right caption");
        sel.setStyleName("styled-twincol-captions");
        sel.setWidth("300px");
        addComponent(sel);

        Button b = new Button("Set height and width to 500px", event -> {
            sel.setHeight("500px");
            sel.setWidth("500px");
        });
        addComponent(b);
    }

    @Override
    protected String getDescription() {
        return "Tests that caption styling for TwinColSelect captions work properly. The left caption should be red and the right caption blue and larger than the left one.";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

}
