package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.ui.ComboBox;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Window;

public class Ticket2234 extends Application {

    @Override
    public void init() {
        Window w = new Window(getClass().getSimpleName());
        setMainWindow(w);
        // setTheme("tests-tickets");
        createUI((OrderedLayout) w.getLayout());
    }

    private void createUI(OrderedLayout layout) {
        ComboBox combo = new ComboBox("Combobox caption");
        combo.addContainerProperty("blah", String.class, "");
        combo.setItemCaptionPropertyId("blah");

        Item item;
        for (int i = 0; i < 100; i++) {
            item = combo.addItem(new Object());
            item.getItemProperty("blah").setValue("Item " + i);
        }

        layout.addComponent(combo);

        combo = new ComboBox("Combobox caption");
        combo.addContainerProperty("blah", String.class, "");
        combo.setItemCaptionPropertyId("blah");

        for (int i = 0; i < 5; i++) {
            item = combo.addItem(new Object());
            item.getItemProperty("blah").setValue("Item " + i);
        }

        layout.addComponent(combo);
    }
}
