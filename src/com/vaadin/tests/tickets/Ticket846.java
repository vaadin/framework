package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.data.Validator;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.ui.Button;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

public class Ticket846 extends Application {

    @Override
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

        // tx.setIcon(new ThemeResource("icons/16/folder.png"));

        mainWin.addComponent(new Button("Validate integer",
                new Button.ClickListener() {
                    public void buttonClick(
                            com.vaadin.ui.Button.ClickEvent event) {
                        mainWin.showNotification("The field is "
                                + (tx.isValid() ? "" : "not ") + "valid");
                    };
                }));
        TextField caption = new TextField("Caption", new MethodProperty(tx,
                "caption"));
        caption.setImmediate(true);
        mainWin.addComponent(caption);
    }

}
