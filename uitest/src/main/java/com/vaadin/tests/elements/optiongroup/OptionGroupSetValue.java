package com.vaadin.tests.elements.optiongroup;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.v7.ui.OptionGroup;

public class OptionGroupSetValue extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        OptionGroup group = new OptionGroup();
        group.addItem("item1");
        group.addItem("item2");
        group.addItem("item3");
        addComponent(group);
    }

    @Override
    protected String getTestDescription() {
        return "Test OptionGroup element setValue() and SelectByText()";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14918;
    }

}
