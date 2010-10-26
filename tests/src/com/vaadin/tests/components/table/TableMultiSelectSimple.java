package com.vaadin.tests.components.table;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.AbstractSelect.MultiSelectMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Table;

public class TableMultiSelectSimple extends TestBase {
    Log log = new Log(3);

    @Override
    protected void setup() {
        log.setDebugId("eventlog");

        Table t = new Table();

        t.setSelectable(true);
        t.setNullSelectionAllowed(true);
        t.setMultiSelect(true);
        t.setMultiSelectMode(MultiSelectMode.SIMPLE);
        t.setImmediate(true);
        t.addListener(new ValueChangeListener() {

            public void valueChange(ValueChangeEvent event) {
                log.log("Selected value: " + event.getProperty().getValue());
            }
        });

        t.addContainerProperty("string", String.class, null);
        t.addContainerProperty("button", Component.class, null);

        for (int i = 0; i < 10; i++) {
            t.addItem(i);
            t.getContainerProperty(i, "string").setValue(i);
            t.getContainerProperty(i, "button")
                    .setValue(new Button("Click me"));
        }

        addComponent(log);
        addComponent(t);
    }

    @Override
    protected String getDescription() {
        return "Tests that MultiSelectMode.SIMPLE is working properly";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5744;
    }

}
