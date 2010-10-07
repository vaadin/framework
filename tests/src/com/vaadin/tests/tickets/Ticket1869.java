package com.vaadin.tests.tickets;

import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class Ticket1869 extends com.vaadin.Application {

    @Override
    public void init() {

        GridLayout lo = new GridLayout(2, 1);
        setMainWindow(new Window("#1869", lo));
        lo.setMargin(true);
        lo.setSpacing(true);

        VerticalLayout el = new VerticalLayout();
        Panel elp = new Panel(
                "Vertical ExpandLayout /w first component expanded", el);
        el.setHeight(1000);
        for (int i = 0; i < 3; i++) {
            Button b = new Button("x");
            el.addComponent(b);
            if (i == 0) {
                b.setSizeFull();
                el.setExpandRatio(b, 1);
            }
        }
        lo.addComponent(elp);
        elp.setWidth(300);
        elp.setHeight(300);
        elp.setScrollable(true);

        HorizontalLayout elh = new HorizontalLayout();
        Panel elph = new Panel(
                "Horizontal ExpandLayout /w first component expanded", elh);
        elh.setWidth(1000);
        for (int i = 0; i < 3; i++) {
            Button b = new Button("x");
            elh.addComponent(b);
            if (i == 0) {
                b.setSizeFull();
                elh.setExpandRatio(b, 1);
            }
        }
        lo.addComponent(elph);
        elph.setWidth(300);
        elph.setHeight(300);
        elph.setScrollable(true);

    }
}
