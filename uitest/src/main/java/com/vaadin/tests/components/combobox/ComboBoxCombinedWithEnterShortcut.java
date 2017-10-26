package com.vaadin.tests.components.combobox;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.Button;
import com.vaadin.v7.shared.ui.combobox.FilteringMode;
import com.vaadin.v7.ui.ComboBox;

public class ComboBoxCombinedWithEnterShortcut extends TestBase {
    final String[] cities = { "Berlin", "Brussels", "Helsinki", "Madrid",
            "Oslo", "Paris", "Stockholm" };

    private Log log = new Log(5);

    @Override
    protected void setup() {
        final ComboBox l = new ComboBox("Please select a city");
        for (String city : cities) {
            l.addItem(city);
        }

        l.setFilteringMode(FilteringMode.OFF);
        l.setImmediate(true);
        l.setNewItemsAllowed(true);

        Button aButton = new Button("Show Value");
        aButton.setClickShortcut(KeyCode.ENTER);
        aButton.addClickListener(event -> log
                .log("Button clicked. ComboBox value: " + l.getValue()));

        addComponent(log);
        addComponent(l);
        addComponent(aButton);
    }

    @Override
    protected String getDescription() {
        return "Button has Enter as click shortcut key. The shortcut should not be triggered when selecting an item in the dropdown";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6686;
    }

}
