package com.vaadin.tests.tickets;

import com.vaadin.data.util.MethodProperty;
import com.vaadin.server.LegacyApplication;
import com.vaadin.tests.util.CheckBoxWithPropertyDataSource;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.v7.data.validator.LegacyIntegerValidator;
import com.vaadin.v7.ui.LegacyTextField;

public class Ticket846 extends LegacyApplication {

    @Override
    public void init() {

        final LegacyWindow mainWin = new LegacyWindow("Test app for #846");
        setMainWindow(mainWin);

        final LegacyTextField tx = new LegacyTextField("Integer");
        mainWin.addComponent(tx);
        tx.setImmediate(true);
        tx.addValidator(new LegacyIntegerValidator("{0} is not a number"));

        final String[] visibleProps = { "required", "invalidAllowed",
                "readOnly", "readThrough", "invalidCommitted",
                "validationVisible" };
        for (int i = 0; i < visibleProps.length; i++) {
            CheckBox b = new CheckBoxWithPropertyDataSource(visibleProps[i],
                    new MethodProperty<Boolean>(tx, visibleProps[i]));
            b.setImmediate(true);
            mainWin.addComponent(b);
        }

        // tx.setIcon(new ThemeResource("icons/16/folder.png"));

        mainWin.addComponent(
                new Button("Validate integer", new Button.ClickListener() {
                    @Override
                    public void buttonClick(
                            com.vaadin.ui.Button.ClickEvent event) {
                        mainWin.showNotification("The field is "
                                + (tx.isValid() ? "" : "not ") + "valid");
                    }
                }));
        LegacyTextField caption = new LegacyTextField("Caption",
                new MethodProperty<String>(tx, "caption"));
        caption.setImmediate(true);
        mainWin.addComponent(caption);
    }

}
