package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.data.Item;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.OrderedLayout;
import com.vaadin.ui.Window;

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
