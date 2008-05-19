package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.data.Validator;
import com.itmill.toolkit.data.util.MethodProperty;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.Window;

public class Ticket846 extends Application {

    public void init() {

        final Window mainWin = new Window("Test app for #846");
        setMainWindow(mainWin);

        final TextField tx = new TextField("Integer");
        mainWin.addComponent(tx);
        tx.setImmediate(true);
        tx.addValidator(new Validator() {

            public boolean isValid(Object value) {
                try {
                    Integer.parseInt("" + value);
                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            }

            public void validate(Object value) throws InvalidValueException {
                if (!isValid(value)) {
                    throw new InvalidValueException(value + " is not a number");
                }
            }
        });

        final String[] visibleProps = { "required", "invalidAllowed",
                "readOnly", "readThrough", "invalidCommitted",
                "validationVisible" };
        for (int i = 0; i < visibleProps.length; i++) {
            Button b = new Button(visibleProps[i], new MethodProperty(tx,
                    visibleProps[i]));
            b.setImmediate(true);
            mainWin.addComponent(b);
        }

        mainWin.addComponent(new Button("Validate integer",
                new Button.ClickListener() {
                    public void buttonClick(
                            com.itmill.toolkit.ui.Button.ClickEvent event) {
                        mainWin.showNotification("The field is "
                                + (tx.isValid() ? "" : "not ") + "valid");
                    };
                }));
    }

}
