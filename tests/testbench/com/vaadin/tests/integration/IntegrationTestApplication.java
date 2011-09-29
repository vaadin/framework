package com.vaadin.tests.integration;

import com.vaadin.Application;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.ClassResource;
import com.vaadin.terminal.Resource;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;

public class IntegrationTestApplication extends Application {

    @Override
    public void init() {
        Window window = new Window("Vaadin Application");
        setMainWindow(window);

        final Table table = new Table();
        table.addContainerProperty("icon", Resource.class, null);
        table.setItemIconPropertyId("icon");
        table.addContainerProperty("country", String.class, null);
        table.setRowHeaderMode(Table.ROW_HEADER_MODE_ICON_ONLY);
        table.setImmediate(true);
        table.setSelectable(true);
        table.setVisibleColumns(new Object[] { "country" });
        window.addComponent(table);

        Item item = table.addItem("FI");
        item.getItemProperty("icon")
                .setValue(new ClassResource("fi.gif", this));
        item.getItemProperty("country").setValue("Finland");
        item = table.addItem("SE");
        item.getItemProperty("icon")
                .setValue(new ClassResource("se.gif", this));
        item.getItemProperty("country").setValue("Sweden");

        final Label selectedLabel = new Label();
        table.addListener(new ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
                selectedLabel.setValue(table.getValue());
            }
        });
        window.addComponent(selectedLabel);
    }
}
