package com.vaadin.tests.components.checkbox;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.CheckBox;

public class CheckboxContextClick extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        final CheckBox cb = new CheckBox("Right-click me", true);
        cb.addContextClickListener(event -> log("checkbox context clicked"));

        addComponent(cb);
    }

}
