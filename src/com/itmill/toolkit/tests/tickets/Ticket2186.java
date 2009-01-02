package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.Table;
import com.itmill.toolkit.ui.Window;

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
