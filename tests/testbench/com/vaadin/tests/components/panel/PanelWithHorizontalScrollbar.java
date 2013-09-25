package com.vaadin.tests.components.panel;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Panel;

public class PanelWithHorizontalScrollbar extends TestBase {

    @Override
    protected void setup() {
        Panel p = new Panel();
        addComponent(p);
        p.setWidth("100px");
//        p.setHeight("500px");
        p.getContent().setSizeUndefined();
        Button b = new Button("aaaaaaaaa");
        b.setSizeUndefined();
        p.addComponent(b);
    }

    @Override
    protected String getDescription() {
        return "Horizontal scrollbar in panel rendered outside visible content on Chrome";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10255;
    }

}
