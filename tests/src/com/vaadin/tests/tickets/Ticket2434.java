package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.tests.TestForTablesInitialColumnWidthLogicRendering;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;

public class Ticket2434 extends Application {

    @Override
    public void init() {

        Window w = new Window();

        setMainWindow(w);

        Table t = TestForTablesInitialColumnWidthLogicRendering.getTestTable(3,
                50);

        t.setPageLength(0);

        t.addStyleName("bordered");

        w.addComponent(t);

        setTheme("tests-tickets");

    }

}
