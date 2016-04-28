package com.vaadin.tests.components.combobox;

import com.vaadin.data.Item;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.ComboBox;

public class ComboBoxUndefinedWidthAndIcon extends TestBase {
    @Override
    protected void setup() {
        ComboBox cb = new ComboBox();
        cb.addContainerProperty("caption", String.class, null);
        cb.addContainerProperty("icon", Resource.class, null);
        for (int i = 1; i < 200 + 1; i++) {
            Item item = cb.addItem(i);
            item.getItemProperty("caption").setValue("Item " + i);
            item.getItemProperty("icon").setValue(
                    new ThemeResource("../runo/icons/16/users.png"));
        }
        cb.setItemIconPropertyId("icon");
        cb.setItemCaptionPropertyId("caption");

        addComponent(cb);
    }

    @Override
    protected String getDescription() {
        return "The width of the ComboBox should be fixed even though it is set to undefined width. The width should not change when changing pages in the dropdown.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 7013;
    }
}
