package com.vaadin.tests.components.radiobuttongroup;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.RadioButtonGroup;

public class RadioButtonGroupDisablingAndReadOnly extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        RadioButtonGroup<String> group = new RadioButtonGroup<>();
        group.setEnabled(false);
        group.setReadOnly(true);
        group.setItems("a", "b", "c");
        addComponent(group);

        addComponent(new Button("Toggle enabled", e -> {
            group.setEnabled(!group.isEnabled());
        }));

        addComponent(new Button("Toggle readOnly", e -> {
            group.setReadOnly(!group.isReadOnly());
        }));

        addComponent(new Button("Clear selection", e -> {
            group.setValue(null);
        }));
    }

    @Override
    protected String getTestDescription() {
        return "Options should only be selectable when the group is "
                + "neither disabled nor readOnly";
    }
}
