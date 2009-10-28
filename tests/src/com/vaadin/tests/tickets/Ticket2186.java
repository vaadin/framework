package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.Label;
import com.vaadin.ui.OrderedLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;

public class Ticket2186 extends Application {

    @Override
    public void init() {
        Window main = new Window("Quick test");
        setMainWindow(main);

        OrderedLayout base = new OrderedLayout(
                OrderedLayout.ORIENTATION_HORIZONTAL);
        main.setLayout(base);

        OrderedLayout content = new OrderedLayout();

        content.addComponent(new Label("Content."));
        content.setWidth("500px");

        Table table = new Table();

        table.setPageLength(10);

        table.setWidth("100%");

        table.addContainerProperty("Lähettäjä", String.class, "");
        table.addContainerProperty("Viestin tyyppi", String.class, "");

        for (int i = 0; i < 15; i++) {

            table.addItem(new Object[] { i + " Joku Ihminen", "Testiviesti" },

            new Object());

        }

        content.addComponent(table);

        Panel right = new Panel("Panel");

        right.addComponent(new Label("Some basic text might show up here."));

        base.addComponent(content);

        base.addComponent(right);

    }
}
