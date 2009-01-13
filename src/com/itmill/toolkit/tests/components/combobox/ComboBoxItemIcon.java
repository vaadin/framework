package com.itmill.toolkit.tests.components.combobox;

import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.terminal.ClassResource;
import com.itmill.toolkit.terminal.Resource;
import com.itmill.toolkit.tests.components.TestBase;
import com.itmill.toolkit.ui.ComboBox;

public class ComboBoxItemIcon extends TestBase {

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
