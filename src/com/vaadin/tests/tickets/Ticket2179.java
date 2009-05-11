package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

public class Ticket2179 extends Application {

    TextField f = new TextField("Test fiel ( must contain 1 & 2 )");
    Window main = new Window("Dual validator test");

    @Override
    public void init() {

        f.setImmediate(true);
        f.setRequired(true);
        f.addValidator(new ContainsValidator("1"));
        f.addValidator(new ContainsValidator("2"));

        setMainWindow(main);
        main.addComponent(f);

        f.addListener(new Property.ValueChangeListener() {

            public void valueChange(ValueChangeEvent event) {
                main.showNotification("Test field is "
                        + (f.isValid() ? "valid" : "invalid"));
            }
        });

    }

    class ContainsValidator implements Validator {
        private final String c;

        public ContainsValidator(String c) {
            this.c = c;
        }

        public boolean isValid(Object value) {
            return value != null && value.toString().contains(c);
        }

        public void validate(Object value) throws InvalidValueException {
            if (!isValid(value)) {
                throw new InvalidValueException("Value does not contain " + c);
            }

        }

    }

}
