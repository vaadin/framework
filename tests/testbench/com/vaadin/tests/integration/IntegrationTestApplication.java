package com.vaadin.tests.integration;

import com.vaadin.Application;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.ClassResource;
import com.vaadin.terminal.Resource;
import com.vaadin.ui.Label;
import com.vaadin.ui.Root.LegacyWindow;
import com.vaadin.ui.Table;

public class IntegrationTestApplication extends Application.LegacyApplication {

    @Override
    public void init() {
        LegacyWindow window = new LegacyWindow("Vaadin Application");
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
        item.getItemProperty("icon").setValue(new FlagSeResource(this));
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
