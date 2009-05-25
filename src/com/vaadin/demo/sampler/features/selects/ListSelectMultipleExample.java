package com.vaadin.demo.sampler.features.selects;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class ListSelectMultipleExample extends VerticalLayout implements
        Property.ValueChangeListener {

    private static final String[] cities = new String[] { "Berlin", "Brussels",
            "Helsinki", "Madrid", "Oslo", "Paris", "Stockholm" };

    public ListSelectMultipleExample() {
        setSpacing(true);

        ListSelect l = new ListSelect("Please select some cities");
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
        getWindow().showNotification("Selected cities: " + event.getProperty());

    }
}
