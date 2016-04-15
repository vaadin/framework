package com.vaadin.tests.components.table;

import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.TestUtils;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.CellStyleGenerator;

@SuppressWarnings("serial")
public class CellStyleGeneratorTest extends TestBase {

    @Override
    protected void setup() {
        TestUtils
                .injectCSS(getMainWindow(),
                        ".v-table-cell-content-red{background:red;}.v-table-row-blue{background:blue;}");

        CellStyleGenerator g = new CellStyleGenerator() {

            @Override
            public String getStyle(Table source, Object itemId,
                    Object propertyId) {
                if (propertyId != null && propertyId.equals("red")) {
                    return "red";
                } else if (itemId.equals("blue") && propertyId == null) {
                    // row style
                    return "blue";
                }
                return null;
            }

        };

        Table table = new Table();
        table.addContainerProperty("foo", String.class, "foo");
        table.addContainerProperty("red", String.class, "red");
        table.addItem();
        table.addItem("blue");
        table.setCellStyleGenerator(g);

        addComponent(table);

        table = new Table();
        table.addContainerProperty("foo", String.class, "foo");
        table.addContainerProperty("red", String.class, "red");
        table.addItem();
        table.addItem("blue");
        table.setCellStyleGenerator(g);
        table.setRowHeaderMode(Table.ROW_HEADER_MODE_ID);

        addComponent(table);

    }

    @Override
    protected String getDescription() {
        return "Cell style generators should work";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

}
