package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.tests.TestForTablesInitialColumnWidthLogicRendering;
import com.vaadin.ui.Root;
import com.vaadin.ui.Table;

public class Ticket2434 extends Application.LegacyApplication {

    @Override
    public void init() {

        Root w = new Root();

        setMainWindow(w);

        Table t = TestForTablesInitialColumnWidthLogicRendering.getTestTable(3,
                50);

        t.setPageLength(0);

        t.addStyleName("bordered");

        w.addComponent(t);

        setTheme("tests-tickets");

    }

}
