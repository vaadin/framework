package com.vaadin.demo.sampler.features.selects;

import java.util.Arrays;
import java.util.List;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class ListSelectSingleExample extends VerticalLayout implements
        Property.ValueChangeListener {

    private static final List cities = Arrays.asList(new String[] { "Berlin",
            "Brussels", "Helsinki", "Madrid", "Oslo", "Paris", "Stockholm" });

    public ListSelectSingleExample() {
        setSpacing(true);

        // 'Shorthand' constructor - also supports data binding using Containers
        ListSelect citySelect = new ListSelect("Please select a city", cities);

        citySelect.setRows(7); // perfect length in out case
        citySelect.setNullSelectionAllowed(false); // user can not 'unselect'
        citySelect.select("Berlin"); // select this by default
        citySelect.setImmediate(true); // send the change to the server at once
        citySelect.addListener(this); // react when the user selects something

        addComponent(citySelect);
    }

    /*
     * Shows a notification when a selection is made. The listener will be
     * called whenever the value of the component changes, i.e when the user
     * makes a new selection.
     */
    public void valueChange(ValueChangeEvent event) {
        getWindow().showNotification("Selected city: " + event.getProperty());

    }
}
