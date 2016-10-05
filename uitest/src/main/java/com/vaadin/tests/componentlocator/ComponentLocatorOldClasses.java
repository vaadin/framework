package com.vaadin.tests.componentlocator;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;

public class ComponentLocatorOldClasses extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        com.vaadin.ui.TextField newTF = new com.vaadin.ui.TextField();
        newTF.setId("new");
        addComponent(newTF);
        com.vaadin.v7.ui.TextField oldTF = new com.vaadin.v7.ui.TextField();
        oldTF.setId("old");
        addComponent(oldTF);
    }

    @Override
    protected String getTestDescription() {
        return "Component locator should find both text fields";
    }
}
