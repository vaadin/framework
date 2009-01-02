package com.itmill.toolkit.tests.tickets;

import java.util.LinkedList;

import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.ExpandLayout;
import com.itmill.toolkit.ui.GridLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.Window;

public class Ticket1869 extends com.itmill.toolkit.Application {

    LinkedList listOfAllFields = new LinkedList();

    @Override
    public void init() {

        GridLayout lo = new GridLayout(2, 1);
        setMainWindow(new Window("#1869", lo));
        lo.setMargin(true);
        lo.setSpacing(true);

        ExpandLayout el = new ExpandLayout();
        Panel elp = new Panel(
                "Vertical ExpandLayout /w first component expanded", el);
        el.setHeight(1000);
        for (int i = 0; i < 3; i++) {
            Button b = new Button("x");
            el.addComponent(b);
            if (i == 0) {
                b.setSizeFull();
                el.expand(b);
            }
        }
        lo.addComponent(elp);
        elp.setWidth(300);
        elp.setHeight(300);
        elp.setScrollable(true);

        ExpandLayout elh = new ExpandLayout(ExpandLayout.ORIENTATION_HORIZONTAL);
        Panel elph = new Panel(
                "Horizontal ExpandLayout /w first component expanded", elh);
        elh.setWidth(1000);
        for (int i = 0; i < 3; i++) {
            Button b = new Button("x");
            elh.addComponent(b);
            if (i == 0) {
                b.setSizeFull();
                elh.expand(b);
            }
        }
        lo.addComponent(elph);
        elph.setWidth(300);
        elph.setHeight(300);
        elph.setScrollable(true);

    }
}
