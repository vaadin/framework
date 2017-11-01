package com.vaadin.tests.integration;

import com.vaadin.server.ClassResource;
import com.vaadin.server.LegacyApplication;
import com.vaadin.server.Resource;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.ui.Table;

public class IntegrationTestApplication extends LegacyApplication {

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
        item.getItemProperty("icon").setValue(new ClassResource("fi.gif"));
        item.getItemProperty("country").setValue("Finland");
        item = table.addItem("SE");
        item.getItemProperty("icon").setValue(new FlagSeResource());
        item.getItemProperty("country").setValue("Sweden");

        final Label selectedLabel = new Label();
        table.addValueChangeListener(event -> selectedLabel
                .setValue(String.valueOf(table.getValue())));
        window.addComponent(selectedLabel);
    }
}
