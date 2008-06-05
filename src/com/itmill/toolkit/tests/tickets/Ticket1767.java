package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.ui.ComboBox;
import com.itmill.toolkit.ui.Window;

public class Ticket1767 extends com.itmill.toolkit.Application {

    public void init() {

        final Window main = new Window(getClass().getName().substring(
                getClass().getName().lastIndexOf(".") + 1));
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
