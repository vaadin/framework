package com.vaadin.tests.components;

import com.vaadin.data.util.converter.StringToIntegerConverter;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class TouchDevicesTooltip extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final Label errorLabel = new Label("No error");
        addComponent(errorLabel);

        TextField textField = new TextField("Value");
        textField.setConverter(new StringToIntegerConverter());
        textField.addValidator(new IntegerRangeValidator("incorrect value", 0, 100));
        textField.setImmediate(true);
        textField.setValue("-5");
        addComponent(textField);

        TextField textField2 = new TextField("Value2");
        textField2.setConverter(new StringToIntegerConverter());
        textField2.addValidator(new IntegerRangeValidator("incorrect value2", 0, 100));
        textField2.setImmediate(true);
        textField2.setValue("-5");
        addComponent(textField2);
    }

    public static class Bean {
        @NotNull
        @Min(0)
        private Integer value;

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }
    }

    @Override
    protected Integer getTicketNumber() {
        return 15353;
    }

    @Override
    public String getDescription() {
        return "Displaying error message in slot for touch devices";
    }
}
