package com.vaadin.tests.components.nativeselect;

import com.vaadin.tests.components.abstractlisting.AbstractListingTestUI;
import com.vaadin.ui.NativeSelect;

public class NativeSelects extends
        AbstractListingTestUI<NativeSelect<Object>> {

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected Class<NativeSelect<Object>> getTestClass() {
        return (Class) NativeSelect.class;
    }

    @Override
    protected void createActions() {
        super.createActions();
        createListenerMenu();
    }

    protected void createListenerMenu() {
        createListenerAction("Selection listener", "Listeners",
                c -> c.addSelectionListener(
                        e -> log("Selected: " + e.getValue())));
    }
}
