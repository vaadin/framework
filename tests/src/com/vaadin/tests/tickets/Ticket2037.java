package com.vaadin.tests.tickets;

import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.OrderedLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

public class Ticket2037 extends com.vaadin.Application {

    @Override
    public void init() {
        Window main = new Window();
        setMainWindow(main);

        main
                .addComponent(new Label(
                        "Use debug dialog and trac number of registered paintables. It should not grow on subsequant b clicks."));

        final Layout lo = new OrderedLayout();

        Button b = new Button("b");

        main.addComponent(b);
        main.addComponent(lo);
        b.addListener(new Button.ClickListener() {

            public void buttonClick(ClickEvent event) {

                repopupate(lo);

            }
        });

    }

    int counter = 0;

    protected void repopupate(Layout lo) {
        lo.removeAllComponents();

        for (int i = 0; i < 20; i++) {
            lo.addComponent(new Label("tc" + (counter++)));
        }

    }

}