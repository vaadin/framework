package com.vaadin.demo.sampler.features.dates;

import java.util.Locale;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.demo.sampler.ExampleUtil;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.VerticalLayout;

public class DateLocaleExample extends VerticalLayout implements
        Property.ValueChangeListener {

    private InlineDateField datetime;
    private ComboBox localeSelection;

    public DateLocaleExample() {
        setSpacing(true);

        datetime = new InlineDateField("Please select the starting time:");

        // Set the value of the PopupDateField to current date
        datetime.setValue(new java.util.Date());

        // Set the correct resolution
        datetime.setResolution(InlineDateField.RESOLUTION_MIN);
        datetime.setImmediate(true);

        // Create selection and fill it with locales
        localeSelection = new ComboBox("Select date format:");
        localeSelection.addListener(this);
        localeSelection.setImmediate(true);
        localeSelection
                .setContainerDataSource(ExampleUtil.getLocaleContainer());

        addComponent(datetime);
        addComponent(localeSelection);
    }

    public void valueChange(ValueChangeEvent event) {
        Item selected = localeSelection.getItem(event.getProperty().getValue());
        datetime.setLocale((Locale) selected.getItemProperty(
                ExampleUtil.locale_PROPERTY_LOCALE).getValue());
        datetime.requestRepaint();
    }
}
