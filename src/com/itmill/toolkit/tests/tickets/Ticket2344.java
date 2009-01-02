package com.itmill.toolkit.tests.tickets;

import java.util.Random;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.Table;
import com.itmill.toolkit.ui.VerticalLayout;
import com.itmill.toolkit.ui.Window;

public class Ticket2344 extends Application {

    private Panel p;

    @Override
    public void init() {
        Window main = new Window("Quick test");

        setMainWindow(main);

        // setTheme("quicktest");

        VerticalLayout hl = new VerticalLayout();
        hl.setWidth("400px");
        main.setLayout(hl);

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
            b.setStyleName(Button.STYLE_LINK);
            vl.addComponent(b);
            t.addItem(new Object[] { vl, "String 2", "String 3", "String 4",

            new Button("String 5") }, new Integer(new Random().nextInt()));

        }

        hl.addComponent(t);

    }

}
