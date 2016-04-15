package com.vaadin.tests.components.caption;

import com.vaadin.server.ThemeResource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;

public class LargeCaptionIcon extends TestBase {

    @Override
    protected String getDescription() {
        return "The icon should be completely visible on both initial load and after subsequent refreshes.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2902;
    }

    @Override
    protected void setup() {
        GridLayout gl = new GridLayout();
        gl.setWidth("100%");

        Label l = new Label("This is a label");
        l.setCaption("This is its caption, it also has a large icon");
        l.setIcon(new ThemeResource("../runo/icons/64/ok.png"));
        gl.addComponent(l);
        addComponent(gl);
    }
}
