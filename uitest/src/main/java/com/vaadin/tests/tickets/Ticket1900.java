package com.vaadin.tests.tickets;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Validator;
import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.TextField;

public class Ticket1900 extends LegacyApplication {

    TextField f[] = new TextField[5];
    LegacyWindow main = new LegacyWindow("#1900 test");

    @Override
    public void init() {

        setMainWindow(main);

        for (int i = 0; i < 5; i++) {
            final int j = i;
            f[i] = new TextField("Testcase " + i);
            f[i].setImmediate(true);
            f[i].setRequired(true);
            main.addComponent(f[i]);
            f[i].addListener(new Property.ValueChangeListener() {
                @Override
                public void valueChange(ValueChangeEvent event) {
                    main.showNotification("Validity test", "Testcase " + j
                            + " is " + (f[j].isValid() ? "valid" : "invalid"));
                }
            });
            f[i].addValidator(new ContainsValidator("1"));
            f[i].addValidator(new ContainsValidator("2"));

        }

        f[0].setDescription("Field is empty, requiredError(null): *");

        f[1].setDescription("Field is empty, requiredError(\"foo\"): * (popup shows the validation error)");
        f[1].setRequiredError("The field must not be empty");

        f[2].setDescription("Field is non-empty, validators do not give validation error: *");
        f[2].setValue("valid 12");

        f[3].setDescription("Field is non-empty, requiredError(null), validators "
                + "give validation error: * ! (popup shows the validation error)");
        f[3].setValue("invalid");

        f[4].setDescription("Field is non-empty, requiredError(\"foo\"), validators "
                + "give validation error: * ! (popup shows the validation error)");
        f[4].setValue("invalid");
        f[4].setRequiredError("The field must not be empty");

    }

    static class ContainsValidator implements Validator {
        private final String c;

        public ContainsValidator(String c) {
            this.c = c;
        }

        @Override
        public void validate(Object value) throws InvalidValueException {
            if (value == null || !value.toString().contains(c)) {
                throw new InvalidValueException("Value does not contain " + c);
            }

        }

    }

}
