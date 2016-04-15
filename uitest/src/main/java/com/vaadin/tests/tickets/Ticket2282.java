package com.vaadin.tests.tickets;

import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;

public class Ticket2282 extends LegacyApplication {

    private FormLayout layout1;
    private FormLayout layout2;

    @Override
    public void init() {
        LegacyWindow w = new LegacyWindow(getClass().getSimpleName());
        setMainWindow(w);
        setTheme("tests-tickets");
        w.getContent().setSizeUndefined();

        layout1 = new FormLayout();
        layout1.setSizeUndefined();
        layout1.setStyleName("borders");
        Label label = new Label(
                "This should not be wider than this label + reserved error space");
        label.setCaption("A caption");
        layout1.addComponent(label);
        w.addComponent(layout1);

        layout2 = new FormLayout();
        layout2.setWidth("500px");
        layout2.setStyleName("borders");
        label = new Label("This should be 500px wide");
        label.setCaption("A caption");
        layout2.addComponent(label);
        w.addComponent(layout2);

        Button b = new Button("Swap", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                if (layout1.getWidth() < 0.0) {
                    layout1.setWidth("500px");
                    layout2.setWidth(null);
                } else {
                    layout1.setWidth(null);
                    layout2.setWidth("500px");
                }
            }

        });
        w.addComponent(b);
    }

}
