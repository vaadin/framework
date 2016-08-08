package com.vaadin.tests.tickets;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.legacy.data.Validator;
import com.vaadin.legacy.ui.LegacyTextField;
import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.LegacyWindow;

public class Ticket2179 extends LegacyApplication {

    LegacyTextField f = new LegacyTextField("Test fiel ( must contain 1 & 2 )");
    LegacyWindow main = new LegacyWindow("Dual validator test");

    @Override
    public void init() {

        f.setImmediate(true);
        f.setRequired(true);
        f.addValidator(new ContainsValidator("1"));
        f.addValidator(new ContainsValidator("2"));

        setMainWindow(main);
        main.addComponent(f);

        f.addListener(new Property.ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                main.showNotification(
                        "Test field is " + (f.isValid() ? "valid" : "invalid"));
            }
        });

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
