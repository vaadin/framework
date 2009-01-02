package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.ExpandLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Window;

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
