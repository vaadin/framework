package com.vaadin.tests.tickets;

import com.vaadin.data.util.MethodProperty;
import com.vaadin.legacy.data.Validator;
import com.vaadin.legacy.data.validator.LegacyCompositeValidator;
import com.vaadin.legacy.data.validator.LegacyIntegerValidator;
import com.vaadin.legacy.data.validator.LegacyCompositeValidator.CombinationMode;
import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.TextField;

public class Ticket20 extends LegacyApplication {

    @Override
    public void init() {

        final LegacyWindow mainWin = new LegacyWindow("Test app for #20");
        setMainWindow(mainWin);

        final TextField tx = new TextField("Integer");
        mainWin.addComponent(tx);
        tx.setImmediate(true);
        LegacyCompositeValidator v = new LegacyCompositeValidator();
        v.addValidator(new LegacyIntegerValidator("{0} is not a number"));
        v.addValidator(new Validator() {

            private boolean isValid(Object value) {
                try {
                    int i = Integer.parseInt("" + value);
                    if (i < 0) {
                        return false;
                    }
                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            }

            @Override
            public void validate(Object value) throws InvalidValueException {
                if (!isValid(value)) {
                    throw new InvalidValueException(value
                            + " is not a non-negative number");
                }
            }
        });
        LegacyCompositeValidator v2 = new LegacyCompositeValidator(CombinationMode.OR, null);
        v2.addValidator(v);
        v2.addValidator(new Validator() {

            @Override
            public void validate(Object value) throws InvalidValueException {
                if (!"".equals("" + value)) {
                    throw new InvalidValueException("Value is not empty string");
                }
            }
        });
        tx.addValidator(v2);

        final String[] visibleProps = { "required", "invalidAllowed",
                "readOnly", "readThrough", "invalidCommitted",
                "validationVisible" };
        for (int i = 0; i < visibleProps.length; i++) {
            CheckBox b = new CheckBox(visibleProps[i],
                    new MethodProperty<Boolean>(tx, visibleProps[i]));
            b.setImmediate(true);
            mainWin.addComponent(b);
        }

        mainWin.addComponent(new Button("Validate integer",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(
                            com.vaadin.ui.Button.ClickEvent event) {
                        mainWin.showNotification("The field is "
                                + (tx.isValid() ? "" : "not ") + "valid");
                    }
                }));
    }

}
