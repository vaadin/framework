package com.vaadin.tests.components.table;

import com.vaadin.data.Item;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Table;

public class CollapseIndicatorOverlapsColumn extends TestBase {

    @Override
    protected void setup() {
        Table tbl = createTable();
        tbl = createTable();
        tbl.setWidth("400px");
        addComponent(tbl);
    }

    private Table createTable() {
        Table tbl = new Table();
        tbl.addContainerProperty("COL1", String.class, "Column 1");
        tbl.addContainerProperty("COL2", String.class, "Column 2");

        // Right align last column
        tbl.setColumnAlignment("COL2", Table.ALIGN_RIGHT);

        // Allow collapsing
        tbl.setColumnCollapsingAllowed(true);

        for (int i = 0; i < 5; i++) {
            Item item = tbl.addItem("Item " + i);
            for (int j = 1; j <= 2; j++) {
                item.getItemProperty("COL" + j).setValue("Item " + i + "/" + j);
            }
        }
        return tbl;
    }

    @Override
    protected String getDescription() {
        return "The rightmost column should not be covered by the collapse indicator";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6934;
    }

}
