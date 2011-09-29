package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Window;

/**
 * #5053: Last ComboBox item may not be shown if null selection enabled
 */
public class Ticket5053 extends Application {

    @Override
    public void init() {
        Window main = new Window();
        setMainWindow(main);

        ComboBox combobox = new ComboBox("My ComboBox");

        // Enable null selection
        combobox.setNullSelectionAllowed(true);
        // Add the item that marks 'null' value
        String nullitem = "-- none --";
        combobox.addItem(nullitem);
        // Designate it was the 'null' value marker
        combobox.setNullSelectionItemId(nullitem);

        // Add some other items
        for (int i = 0; i < 10; i++) {
            combobox.addItem("Item " + i);
        }

        main.addComponent(combobox);
    }

}
