package com.vaadin.tests.components;

import com.vaadin.annotations.Viewport;
import com.vaadin.data.util.converter.StringToIntegerConverter;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Viewport(value = "width=device-width,height=device-height")
public class TouchDevicesTooltip extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final Label errorLabel = new Label("No error");
        addComponent(errorLabel);

        for (int i = 0; i < 50; i++) {
            createTextField(i);
        }
    }

    private void createTextField(int n) {
        TextField textField = new TextField("Value" + n);
        textField.setConverter(new StringToIntegerConverter());
        textField.addValidator(new IntegerRangeValidator(getErrorMessage(n), 0,
                100));
        textField.setImmediate(true);
        textField.setValue("-5");
        addComponent(textField);
    }

    private String getErrorMessage(int n) {
        if (n % 2 == 0) {
            return "incorrect value" + n;
        } else {
            return "super long long long long long long long long long long long error message "
                    + n;
        }
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
        return 17150;
    }

    @Override
    public String getDescription() {
        return "Unable to dismiss a tooltip on touch devices";
    }
}