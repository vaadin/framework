package com.vaadin.tests.tickets;

import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.VerticalLayout;

public class Ticket2347 extends LegacyApplication {

    private Button b1;

    @Override
    public void init() {
        LegacyWindow w = new LegacyWindow(getClass().getSimpleName());
        setMainWindow(w);
        setTheme("tests-tickets");
        createUI((VerticalLayout) w.getContent());
    }

    private void createUI(VerticalLayout layout) {
        CustomLayout cl = new CustomLayout("Ticket2347");
        b1 = new Button("200px button");
        b1.addListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                if (b1.getWidth() == 200.0) {
                    b1.setWidth("300px");
                } else {
                    b1.setWidth("200px");

                }
                b1.setCaption(b1.getWidth() + "px button");

            }

        });
        b1.setWidth("200px");
        Button b2 = new Button("100% button");
        b2.setWidth("100%");

        cl.addComponent(b1, "button1");
        cl.addComponent(b2, "button2");

        layout.addComponent(cl);
    }
}
