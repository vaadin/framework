package com.vaadin.tests.components.checkbox;

import com.vaadin.event.FieldEvents;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.CheckBox;

public class CheckboxFocusClick extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        final CheckBox cb = new CheckBox("Click me", true);
        cb.addFocusListener((FieldEvents.FocusListener) event -> log("checkbox focused"));
        addComponent(cb);
    }

}
