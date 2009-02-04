package com.itmill.toolkit.demo.sampler.features.selects;

import com.itmill.toolkit.data.Property;
import com.itmill.toolkit.data.Property.ValueChangeEvent;
import com.itmill.toolkit.ui.AbstractSelect;
import com.itmill.toolkit.ui.ComboBox;
import com.itmill.toolkit.ui.VerticalLayout;

public class ComboBoxNewItemsExample extends VerticalLayout implements
        Property.ValueChangeListener, AbstractSelect.NewItemHandler {
    private static final String[] cities = new String[] { "Berlin", "Brussels",
            "Helsinki", "Madrid", "Oslo", "Paris", "Stockholm" };
    private ComboBox l;
    private Boolean lastAdded = false;

    public ComboBoxNewItemsExample() {
        setSpacing(true);

        l = new ComboBox("Please select a city");
        for (int i = 0; i < cities.length; i++) {
            l.addItem(cities[i]);
        }

        l.setNewItemsAllowed(true);
        l.setNewItemHandler(this);
        l.setImmediate(true);
        l.addListener(this);

        addComponent(l);
    }

    /*
     * Shows a notification when a selection is made.
     */
    public void valueChange(ValueChangeEvent event) {
        if (!lastAdded) {
            getWindow().showNotification(
                    "Selected city: " + event.getProperty());
        }
        lastAdded = false;
    }

    public void addNewItem(String newItemCaption) {
        if (!l.containsId(newItemCaption)) {
            getWindow().showNotification("Added city: " + newItemCaption);
            lastAdded = true;
            l.addItem(newItemCaption);
            l.setValue(newItemCaption);
        }
    }
}
