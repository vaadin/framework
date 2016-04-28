package com.vaadin.tests.components.checkbox;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.server.AbstractErrorMessage;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;

public class CheckBoxNullValue extends TestBase {

    @Override
    protected void setup() {
        // workaround for #6919
        getLayout().setWidth("100%");

        final CheckBox checkbox = new CheckBox("A checkbox");
        checkbox.setValue(null);
        addComponent(checkbox);

        final CheckBox requiredCheckbox = new CheckBox("A required checkbox");
        requiredCheckbox.setRequired(true);
        requiredCheckbox.setValue(null);
        addComponent(requiredCheckbox);

        final Label valueLabel = new Label("");

        final Button button = new Button("Validate");
        addComponent(button);
        button.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                checkbox.setComponentError(null);
                requiredCheckbox.setComponentError(null);
                try {
                    checkbox.validate();
                } catch (InvalidValueException e) {
                    checkbox.setComponentError(AbstractErrorMessage
                            .getErrorMessageForException(e));
                }
                try {
                    requiredCheckbox.validate();
                } catch (InvalidValueException e) {
                    requiredCheckbox.setComponentError(AbstractErrorMessage
                            .getErrorMessageForException(e));
                }
                valueLabel.setValue("Checkbox: " + checkbox.getValue()
                        + "; Required checkbox: " + requiredCheckbox.getValue());
            }
        });
        addComponent(valueLabel);
    }

    @Override
    protected String getDescription() {
        return "CheckBox should support null values.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6918;
    }

}
