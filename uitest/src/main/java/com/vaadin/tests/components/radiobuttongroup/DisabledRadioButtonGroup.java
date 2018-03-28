package com.vaadin.tests.components.radiobuttongroup;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.RadioButtonGroup;

/**
 * @author Vaadin Ltd
 *
 */
public class DisabledRadioButtonGroup extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        RadioButtonGroup<String> group = new RadioButtonGroup<>();
        group.setEnabled(false);
        group.setItems("a", "b", "c");
        addComponent(group);
    }

}
