package com.itmill.toolkit.tests.components.table;

import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.terminal.ClassResource;
import com.itmill.toolkit.terminal.Resource;
import com.itmill.toolkit.tests.components.TestBase;
import com.itmill.toolkit.ui.Table;

public class TableItemIcon extends TestBase {

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
        item.getItemProperty("icon").setValue(
                new ClassResource("fi.gif", TableItemIcon.this));
        item = table.addItem("SE");
        item.getItemProperty("icon").setValue(
                new ClassResource("se.gif", TableItemIcon.this));

    }

}
