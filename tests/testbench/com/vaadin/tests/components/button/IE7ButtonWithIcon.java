package com.vaadin.tests.components.button;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;

public class IE7ButtonWithIcon extends TestBase {

    @Override
    protected String getDescription() {
        return "The button should contain the text \"Normal\" and an 16x16 icon and have padding to the left and the right";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3031;
    }

    @Override
    protected void setup() {
        Button b = new Button("Normal");
        b.setIcon(new ThemeResource("../runo/icons/16/ok.png"));

        addComponent(b);
    }

}
