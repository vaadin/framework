package com.vaadin.tests.components.combobox;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.CheckBox;

public class ComboBoxNoTextInput extends ComboBoxSelecting {

    @Override
    protected void setup(VaadinRequest request) {
        super.setup(request);
        comboBox.setTextInputAllowed(true);

        final CheckBox textInputCheckBox = new CheckBox("Text Input", true);
        textInputCheckBox.setId("textInput");
        textInputCheckBox.addValueChangeListener(event -> comboBox
                .setTextInputAllowed(textInputCheckBox.getValue()));
        addComponent(textInputCheckBox);
    }

    @Override
    protected String getTestDescription() {
        return "ComboBox should open popup on click when text input is not allowed.";
    }

}
