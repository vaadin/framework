package com.vaadin.tests.components.table;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;

@SuppressWarnings("serial")
public class MultiSelectWithNotIdentityEqualIds extends TestBase {

    @Override
    protected void setup() {
        final Table t = new Table();
        t.setContainerDataSource(getDS());
        t.setRowHeaderMode(Table.ROW_HEADER_MODE_ID);
        t.setMultiSelect(true);
        t.setSelectable(true);
        t.setImmediate(true);
        t.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                Notification.show("Selected: " + event.getProperty());

            }
        });
        getLayout().addComponent(t);
    }

    private Container getDS() {

        IndexedContainer idx = new IndexedContainer() {

            @Override
            public Object nextItemId(Object itemId) {
                Integer id = (Integer) super.nextItemId(itemId);
                return id == null ? null : new Integer(id);
            }

        };
        for (int i = 0; i < 10; i++) {
            idx.addItem();
        }

        idx.addContainerProperty("Property", String.class, "foo");

        return idx;
    }

    @Override
    protected String getDescription() {
        return "Multiselection should work with container that uses ids that are equal, but not necessary identical. With bug an infinit loop is caused.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5342;
    }

}
