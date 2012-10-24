package com.vaadin.tests.application;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.UI;

/**
 * A simple application to simplify doing quick testing of features when a full
 * test case is not needed
 */
public class HelloWorld extends UI {

    @Override
    protected void init(VaadinRequest request) {

        HorizontalSplitPanel hsp = new HorizontalSplitPanel();
        hsp.setWidth("100%");
        hsp.setHeight("70px");

        NativeButton btn = new NativeButton("Button 1");
        btn.setSizeFull();
        hsp.addComponent(btn);

        NativeButton btn2 = new NativeButton("Button 2");
        btn2.setSizeFull();
        hsp.addComponent(btn2);

        addComponent(hsp);

    }
}
