package com.vaadin.tests.components.checkboxgroup;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.CheckBoxGroup;

/**
 * @author Vaadin Ltd
 *
 */
public class DisabledCheckBoxGroup extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        CheckBoxGroup<String> group = new CheckBoxGroup<>();
        group.setEnabled(false);
        group.setItems("a", "b", "c");
        addComponent(group);
    }

}
