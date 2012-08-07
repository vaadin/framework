package com.vaadin.tests.components.table;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.GeneratedRow;
import com.vaadin.ui.Table.RowGenerator;

public class RowGenerators extends TestBase implements RowGenerator {

    @Override
    protected void setup() {
        Table table = new Table();
        table.setContainerDataSource(filledContainer());
        table.setRowGenerator(this);
        addComponent(table);
    }

    private Container filledContainer() {
        IndexedContainer c = new IndexedContainer();
        c.addContainerProperty("Property 1", String.class, "");
        c.addContainerProperty("Property 2", String.class, "");
        c.addContainerProperty("Property 3", String.class, "");
        c.addContainerProperty("Property 4", String.class, "");
        for (int ix = 0; ix < 500; ix++) {
            Item i = c.addItem(ix);
            i.getItemProperty("Property 1").setValue("Item " + ix + ",1");
            i.getItemProperty("Property 2").setValue("Item " + ix + ",2");
            i.getItemProperty("Property 3").setValue("Item " + ix + ",3");
            i.getItemProperty("Property 4").setValue("Item " + ix + ",4");
        }
        return c;
    }

    @Override
    public GeneratedRow generateRow(Table table, Object itemId) {
        if ((Integer) itemId % 5 == 0) {
            if ((Integer) itemId % 10 == 0) {
                return new GeneratedRow(
                        "foobarbazoof very extremely long, most definitely will span.");
            } else {
                return new GeneratedRow("foo", "bar", "baz", "oof");
            }
        }
        return null;
    }

    @Override
    protected String getDescription() {
        return "Row generators should replace every fifth row in the table";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6720;
    }

}
