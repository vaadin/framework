package com.vaadin.tests.components.table;

import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.TableDragMode;
import com.vaadin.ui.TextField;

public class TableShouldNotEatValueChanges extends TestBase {

    @Override
    protected void setup() {
        Table t = new Table("Table with multiselection and item click listener");
        t.focus();
        t.setPageLength(3);
        t.addContainerProperty("foo", String.class, "bar");
        t.addItem();
        t.setSelectable(true);
        t.setMultiSelect(true);
        t.setTabIndex(4);
        // t.setSelectable(true);

        final TextField tf = new TextField();
        tf.setTabIndex(1);
        ItemClickListener l = new ItemClickListener() {

            @Override
            public void itemClick(ItemClickEvent event) {
                Notification.show("TF Value on the server:" + tf.getValue(),
                        Notification.TYPE_WARNING_MESSAGE);
            }
        };
        t.addListener(l);
        addComponent(tf);
        addComponent(t);
        t = new Table("Table with drag and drop and item click listener");
        t.setDragMode(TableDragMode.ROW);
        t.setPageLength(3);
        t.addContainerProperty("foo", String.class, "bar");
        t.addItem();
        t.setSelectable(true);
        t.setMultiSelect(true);

        t.addListener(l);
        addComponent(t);

    }

    @Override
    protected String getDescription() {
        return "When selecting something from table or clicking on item, table should never eat value change from other components.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5429;
    }

}
