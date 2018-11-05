package com.vaadin.tests.components.combobox;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.ComboBox;

public class ComboBoxAutoresetValue extends AbstractTestUIWithLog {

    public static final String RESET = "Reset";
    public static final String CHANGE = "Change to something else";
    public static final String SOMETHING = "Something else";

    @Override
    protected void setup(VaadinRequest request) {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setItems(RESET, CHANGE, SOMETHING);
        comboBox.addValueChangeListener(event -> {
            String value = event.getValue();
            log("Value changed to " + value);

            if (event.isUserOriginated()) {
                if (RESET.equals(value)) {
                    event.getSource().setValue(null);
                } else if (CHANGE.equals(value)) {
                    event.getSource().setValue(SOMETHING);
                }
            }
        });
        addComponent(comboBox);
    }

    @Override
    public String getDescription() {
        return "Changing the ComboBox value in its own value change listener should work";
    }

}
