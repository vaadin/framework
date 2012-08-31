package com.vaadin.tests.components.table;

import com.vaadin.data.Item;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;

@SuppressWarnings("serial")
public class MultiClickingItemThatDetachesTable extends TestBase {
    @Override
    public void setup() {
        final Table table = new Table();
        table.setImmediate(true);
        table.addContainerProperty("p1", String.class, "p1");
        table.addContainerProperty("p2", String.class, "p2");
        for (int i = 0; i < 200; ++i) {
            final Item item = table.getItem(table.addItem());
            item.getItemProperty("p2").setValue(i + "");
            item.getItemProperty("p1").setValue(i + "");
        }
        table.addListener(new ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent event) {
                if (event.isDoubleClick()) {
                    try {
                        // Wait a bit so there's time to click multiple times
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    replaceComponent(table, new Label("Completed!"));
                }
            }
        });
        addComponent(table);
    }

    @Override
    protected String getDescription() {
        return "Clicking multiple times on an item whose listener detaches the table causes Out of Sync";
    }

    @Override
    protected Integer getTicketNumber() {
        return 8580;
    }

}
