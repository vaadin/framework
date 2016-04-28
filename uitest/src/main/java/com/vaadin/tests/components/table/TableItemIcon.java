package com.vaadin.tests.components.table;

import com.vaadin.data.Item;
import com.vaadin.server.ClassResource;
import com.vaadin.server.Resource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Table;

public class TableItemIcon extends TestBase {

    @Override
    protected Integer getTicketNumber() {
        return 2457;
    }

    @Override
    protected String getDescription() {
        return "The items in the Table should have icons in the first column (rowheader).";
    }

    @Override
    protected void setup() {
        Table table = new Table();
        table.addContainerProperty("icon", Resource.class, null);
        table.setItemIconPropertyId("icon");
        table.setRowHeaderMode(Table.ROW_HEADER_MODE_ICON_ONLY);
        getLayout().addComponent(table);

        Item item = table.addItem("FI");
        item.getItemProperty("icon").setValue(new ClassResource("fi.gif"));
        item = table.addItem("SE");
        item.getItemProperty("icon").setValue(new ClassResource("se.gif"));

    }

}
