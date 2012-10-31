package com.vaadin.tests.components.table;

import com.vaadin.tests.components.AbstractTestCase;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

public class SortLongTable extends AbstractTestCase {

    @Override
    public void init() {
        final int NUMBER_OF_ROWS = 100; // Works with 10

        LegacyWindow mainWindow = new LegacyWindow("Table Sort Test");
        mainWindow.setSizeFull();
        setMainWindow(mainWindow);

        Table ptable = new Table();
        ptable.addContainerProperty("Sort_me_please", String.class, "--");
        for (int i = NUMBER_OF_ROWS - 1; i >= 0; i--) {
            ptable.addItem("" + i).getItemProperty("Sort_me_please")
                    .setValue("Value " + String.format("%02d", i));
        }

        ptable.setWidth("100%");
        ptable.setPageLength(NUMBER_OF_ROWS);

        VerticalLayout vl = new VerticalLayout();
        vl.addComponent(ptable);
        mainWindow.addComponent(vl);
    }

    @Override
    protected String getDescription() {
        return "Clicking on the header should sort the column. It should not cause the headers to be scrolled out of view.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6367;
    }

}
