package com.vaadin.tests.tickets;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.LegacyWindow;

public class Ticket1767 extends com.vaadin.server.LegacyApplication {

    @Override
    public void init() {

        final LegacyWindow main = new LegacyWindow(getClass().getName()
                .substring(getClass().getName().lastIndexOf(".") + 1));
        setMainWindow(main);

        ComboBox cb = new ComboBox(" '<' item is not seen in populist?");
        cb.addItem("Te<strong>st</strong> < jep >");
        cb.addItem("<");
        cb.addItem(">");

        cb.addItem("< dsf");
        cb.addItem("> sdf");

        cb.addItem("dsfs <");
        cb.addItem("sdfsd >");

        main.addComponent(cb);

    }

}
