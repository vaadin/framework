package com.vaadin.tests.tickets;

import com.vaadin.data.Item;
import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.LegacyWindow;

public class Ticket2234 extends LegacyApplication {

    @Override
    public void init() {
        LegacyWindow w = new LegacyWindow(getClass().getSimpleName());
        setMainWindow(w);
        // setTheme("tests-tickets");
        createUI((AbstractOrderedLayout) w.getContent());
    }

    private void createUI(AbstractOrderedLayout layout) {
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
