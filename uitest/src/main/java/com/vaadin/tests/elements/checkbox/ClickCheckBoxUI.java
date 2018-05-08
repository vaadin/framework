package com.vaadin.tests.elements.checkbox;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.CheckBox;

public class ClickCheckBoxUI extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        CheckBox checkBox = new CheckBox("Checkbox Caption");
        addComponent(checkBox);
        addComponent(new CheckBox());
    }

    @Override
    protected String getTestDescription() {
        return "Ensure that CheckBoxElement.click() actually toggles checkmark";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13763;
    }

}
