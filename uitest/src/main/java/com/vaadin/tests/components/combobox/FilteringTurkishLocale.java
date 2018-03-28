package com.vaadin.tests.components.combobox;

import java.util.Arrays;
import java.util.Locale;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.ComboBox;
import com.vaadin.v7.ui.NativeSelect;

public class FilteringTurkishLocale extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {

        final ComboBox<String> comboBox = new ComboBox<>("Box",
                Arrays.asList("I dotless", "İ dotted"));
        comboBox.setEmptySelectionAllowed(false);

        NativeSelect localeSelect = new NativeSelect("Locale",
                Arrays.asList(Locale.ENGLISH, new Locale("tr")));
        localeSelect.addValueChangeListener(event -> comboBox
                .setLocale((Locale) event.getProperty().getValue()));
        localeSelect.setValue(Locale.ENGLISH);

        addComponents(localeSelect, comboBox);
    }

    @Override
    protected String getTestDescription() {
        return "When the Turkish locale is used,"
                + " filtering for 'i' should show the option with a dot"
                + " while filtering for 'ı' should show the option witout a dot";
    }
}
