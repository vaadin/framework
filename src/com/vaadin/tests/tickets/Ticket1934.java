package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.Button;
import com.vaadin.ui.ExpandLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

public class Ticket1934 extends Application {

    @Override
    public void init() {
        Window w = new Window(
                "#1934 : Horizontal ExpandLayout completely broken");
        setMainWindow(w);
        w.addComponent(new Label(
                "Horizontal 500x200 ExpandLayout with two components:"));

        ExpandLayout testedLayout = new ExpandLayout(
                ExpandLayout.ORIENTATION_HORIZONTAL);
        testedLayout.setWidth("500px");
        testedLayout.setHeight("200px");

        Button b1 = new Button("b1");
        testedLayout.addComponent(b1);
        testedLayout.expand(b1);
        testedLayout.addComponent(new Button("b2"));

        w.addComponent(testedLayout);
    }

}
