package com.itmill.toolkit.demo.sampler.features.selects;

import com.itmill.toolkit.data.Property;
import com.itmill.toolkit.data.Property.ValueChangeEvent;
import com.itmill.toolkit.ui.ComboBox;
import com.itmill.toolkit.ui.VerticalLayout;
import com.itmill.toolkit.ui.AbstractSelect.Filtering;

public class ComboBoxNewItemsExample extends VerticalLayout implements
        Property.ValueChangeListener {

    private static final String[] cities = new String[] { "Berlin", "Brussels",
            "Helsinki", "Madrid", "Oslo", "Paris", "Stockholm" };
    private ComboBox l;

    public ComboBoxNewItemsExample() {
        setSpacing(true);

        l = new ComboBox("Please select a city");
        for (int i = 0; i < cities.length; i++) {
            l.addItem(cities[i]);
        }

        l.setFilteringMode(Filtering.FILTERINGMODE_OFF);
        l.setNewItemsAllowed(true);
        l.setImmediate(true);
        l.addListener(this);

        addComponent(l);
    }

    /*
     * Shows a notification when a selection is made.
     */
    public void valueChange(ValueChangeEvent event) {
        Boolean newItem = true;
        String s = event.getProperty().toString();

        for (int i = 0; i < cities.length; i++) {
            if (s == null || s.equals(cities[i])) {
                newItem = false;
            }
        }
        if (newItem) {
            getWindow().showNotification("Selected an added item: " + s);
        } else {
            getWindow().showNotification("Selected city: " + s);
        }
    }
}
