package com.vaadin.tests.components.combobox;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;

public class ComboBoxFilterClear extends ComboBoxSelecting {

    @Override
    protected void setup(VaadinRequest request) {
        super.setup(request);

        Button toggleVisibility = new Button("Toggle visibility",
                e -> comboBox.setVisible(!comboBox.isVisible()));
        toggleVisibility.setId("toggleVisibility");

        Button setNull = new Button("Set null", e -> comboBox.setValue(null));
        setNull.setId("setNull");

        addComponents(toggleVisibility, setNull);
    }

    @Override
    protected String getTestDescription() {
        return "Clearing selection while ComboBox is not visible should not "
                + "leave the suggestion items stuck on the previous filter";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10624;
    }
}
