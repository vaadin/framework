package com.itmill.toolkit.demo.sampler.features.selects;

import com.itmill.toolkit.data.Property;
import com.itmill.toolkit.data.Property.ValueChangeEvent;
import com.itmill.toolkit.ui.TwinColSelect;
import com.itmill.toolkit.ui.VerticalLayout;

public class TwinColumnSelectExample extends VerticalLayout implements
        Property.ValueChangeListener {

    private static final String[] cities = new String[] { "Berlin", "Brussels",
            "Helsinki", "Madrid", "Oslo", "Paris", "Stockholm" };

    public TwinColumnSelectExample() {
        setSpacing(true);

        TwinColSelect l = new TwinColSelect("Please select some cities");
        for (int i = 0; i < cities.length; i++) {
            l.addItem(cities[i]);
        }
        l.setRows(7);
        l.setNullSelectionAllowed(true);
        l.setMultiSelect(true);
        l.setImmediate(true);
        l.addListener(this);

        addComponent(l);
    }

    /*
     * Shows a notification when a selection is made.
     */
    public void valueChange(ValueChangeEvent event) {
        if (!event.getProperty().toString().equals("[]")) {
            getWindow().showNotification(
                    "Selected cities: " + event.getProperty());
        }
    }
}
