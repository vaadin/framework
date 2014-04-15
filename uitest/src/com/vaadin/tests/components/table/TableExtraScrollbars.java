package com.vaadin.tests.components.table;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.tests.components.AbstractTestCase;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

public class TableExtraScrollbars extends AbstractTestCase {

    private static int PROPS = 15;
    private static int ROWS = 1000;

    @Override
    public void init() {
        setTheme("runo");
        LegacyWindow w = new LegacyWindow("Table scrollbars bug example");
        setMainWindow(w);

        VerticalLayout vl = new VerticalLayout();
        vl.setSizeFull();
        vl.addComponent(createTable());
        w.setContent(vl);
    }

    protected Table createTable() {
        Table table = new Table(null, createContainer());
        table.setSizeFull();
        table.setPageLength(50);
        table.setColumnReorderingAllowed(true);
        table.setSelectable(true);
        return table;
    }

    protected Container createContainer() {
        Container container = new IndexedContainer();
        for (int i = 0; i < PROPS; ++i) {
            container.addContainerProperty("prop" + i, String.class, null);
        }
        for (int i = 0; i < ROWS; ++i) {
            Item item = container.addItem(i);
            for (int p = 0; p < PROPS; ++p) {
                item.getItemProperty("prop" + p).setValue(
                        "property value 1234567890");
            }
        }
        return container;
    }

    @Override
    protected String getDescription() {
        return "Scrolling down in the table should not add extra scrollbars";
    }

    @Override
    protected Integer getTicketNumber() {
        return 4489;
    }
}
