package com.vaadin.tests.components.checkbox;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.CheckBox;

public class CheckboxInputValueElement extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        final CheckBox cb = new CheckBox("Test custom style names for inner elements", true);
        cb.getCheckBoxInput().addStyleName("my-input-class");
        cb.getCheckBoxLabel().addStyleName("my-label-class");

        addComponent(cb);

        addButton("add-style", e -> {
            cb.getCheckBoxInput().addStyleName("later-applied-input-class");
            cb.getCheckBoxLabel().addStyleName("later-applied-label-class");
        });

        addButton("remove-style", e -> {
            cb.getCheckBoxInput().removeStyleName("my-input-class");
            cb.getCheckBoxLabel().removeStyleName("my-label-class");
        });
    }

}
