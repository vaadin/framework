package com.vaadin.tests.components.combobox;

import com.vaadin.data.Item;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.ComboBox;

public class ComboBoxItemIcon extends TestBase {

    @Override
    protected Integer getTicketNumber() {
        return 2455;
    }

    @Override
    protected String getDescription() {
        return "All items in the ComboBoxes should have icons.";
    }

    @Override
    protected void setup() {
        {
            ComboBox cb = new ComboBox();
            cb.addContainerProperty("icon", Resource.class, null);
            cb.setItemIconPropertyId("icon");

            Item item = cb.addItem("FI");
            item.getItemProperty("icon").setValue(
                    new ThemeResource("../tests-tickets/icons/fi.gif"));
            item = cb.addItem("SE");
            item.getItemProperty("icon").setValue(
                    new ThemeResource("../tests-tickets/icons/se.gif"));

            addComponent(cb);
        }
        {
            ComboBox cb = new ComboBox();
            cb.addContainerProperty("icon", Resource.class, null);
            cb.setItemIconPropertyId("icon");

            Item item = cb.addItem("Finland");
            item.getItemProperty("icon").setValue(
                    new ThemeResource("../tests-tickets/icons/fi.gif"));
            item = cb.addItem("Australia");
            item.getItemProperty("icon").setValue(
                    new ThemeResource("../tests-tickets/icons/au.gif"));
            item = cb.addItem("Hungary");
            item.getItemProperty("icon").setValue(
                    new ThemeResource("../tests-tickets/icons/hu.gif"));

            cb.setValue("Hungary");
            addComponent(cb);
        }
    }

}
