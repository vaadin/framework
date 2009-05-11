package com.vaadin.tests.components.combobox;

import com.vaadin.data.Item;
import com.vaadin.terminal.ClassResource;
import com.vaadin.terminal.Resource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.ComboBox;

public class ComboBoxItemIcon extends TestBase {

    @Override
    protected Integer getTicketNumber() {
        return 2455;
    }

    @Override
    protected String getDescription() {
        return "The items in the ComboBox should have icons - also when selected.";
    }

    @Override
    protected void setup() {
        ComboBox cb = new ComboBox();
        cb.addContainerProperty("icon", Resource.class, null);
        cb.setItemIconPropertyId("icon");
        getLayout().addComponent(cb);

        Item item = cb.addItem("FI");
        item.getItemProperty("icon").setValue(
                new ClassResource("fi.gif", ComboBoxItemIcon.this));
        item = cb.addItem("SE");
        item.getItemProperty("icon").setValue(
                new ClassResource("se.gif", ComboBoxItemIcon.this));

    }

}
