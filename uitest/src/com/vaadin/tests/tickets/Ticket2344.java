package com.vaadin.tests.tickets;

import java.util.Random;

import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.Button;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;

public class Ticket2344 extends LegacyApplication {

    @Override
    public void init() {
        LegacyWindow main = new LegacyWindow("Quick test");

        setMainWindow(main);

        // setTheme("quicktest");

        VerticalLayout hl = new VerticalLayout();
        hl.setWidth("400px");
        main.setContent(hl);

        Table t = new Table();
        t.setWidth("100%");

        t.addContainerProperty("Prop 1", VerticalLayout.class, "");
        t.addContainerProperty("Prop 2", String.class, "");
        t.addContainerProperty("Prop 3", String.class, "");
        t.addContainerProperty("Prop 4", String.class, "");
        t.addContainerProperty("Prop 5", Button.class, "");

        t.setPageLength(3);

        for (int i = 0; i < 10; i++) {

            VerticalLayout vl = new VerticalLayout();
            // vl.setWidth(null);
            Button b = new Button("String 1 2 3");
            b.setStyleName(BaseTheme.BUTTON_LINK);
            vl.addComponent(b);
            t.addItem(new Object[] { vl, "String 2", "String 3", "String 4",

            new Button("String 5") }, new Integer(new Random().nextInt()));

        }

        hl.addComponent(t);

    }

}
