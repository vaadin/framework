package com.vaadin.tests.elements.checkboxgroup;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.CheckBoxGroup;

public class CheckBoxGroupSetSelection extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        CheckBoxGroup<String> group = new CheckBoxGroup<>();
        List<String> options = new ArrayList<>();
        options.add("item1");
        options.add("item2");
        options.add("item3");
        group.setItems(options);
        addComponent(group);
    }

    @Override
    protected String getTestDescription() {
        return "Test CheckBoxGroup element setValue() and selectByText()";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

}
