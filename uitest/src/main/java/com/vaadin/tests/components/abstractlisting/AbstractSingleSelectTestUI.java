package com.vaadin.tests.components.abstractlisting;

import java.util.LinkedHashMap;

import com.vaadin.ui.AbstractSingleSelect;

public abstract class AbstractSingleSelectTestUI<T extends AbstractSingleSelect<Object>>
        extends AbstractListingTestUI<T> {

    @Override
    protected void createActions() {
        super.createActions();

        createSelectionMenu();
        createListenerMenu();
    }

    protected void createListenerMenu() {
        createListenerAction("Selection listener", "Listeners", c -> c
                .addSelectionListener(
                        event -> log("Selected: " + event.getValue())));
    }

    protected void createSelectionMenu() {
        LinkedHashMap<String, String> options = new LinkedHashMap<>();
        options.put("None", null);
        options.put("Item 0", "Item 0");
        options.put("Item 1", "Item 1");
        options.put("Item 2", "Item 2");
        options.put("Item 10", "Item 10");
        options.put("Item 100", "Item 100");

        createSelectAction("Select", "Selection", options, "None",
                (component, selected, data) -> component.setValue(selected));
    }

}
