package com.vaadin.tests.tickets;

import com.vaadin.data.util.MethodProperty;
import com.vaadin.data.validator.IntegerValidator;
import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.TextField;

public class Ticket846 extends LegacyApplication {

    @Override
    public void init() {

        final LegacyWindow mainWin = new LegacyWindow("Test app for #846");
        setMainWindow(mainWin);

        final TextField tx = new TextField("Integer");
        mainWin.addComponent(tx);
        tx.setImmediate(true);
        tx.addValidator(new IntegerValidator("{0} is not a number"));

        final String[] visibleProps = { "required", "invalidAllowed",
                "readOnly", "readThrough", "invalidCommitted",
                "validationVisible" };
        for (int i = 0; i < visibleProps.length; i++) {
            CheckBox b = new CheckBox(visibleProps[i],
                    new MethodProperty<Boolean>(tx, visibleProps[i]));
            b.setImmediate(true);
            mainWin.addComponent(b);
        }

        // tx.setIcon(new ThemeResource("icons/16/folder.png"));

        mainWin.addComponent(new Button("Validate integer",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(
                            com.vaadin.ui.Button.ClickEvent event) {
                        mainWin.showNotification("The field is "
                                + (tx.isValid() ? "" : "not ") + "valid");
                    }
                }));
        TextField caption = new TextField("Caption",
                new MethodProperty<String>(tx, "caption"));
        caption.setImmediate(true);
        mainWin.addComponent(caption);
    }

}
