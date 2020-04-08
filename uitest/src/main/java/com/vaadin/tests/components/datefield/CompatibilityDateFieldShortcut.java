package com.vaadin.tests.components.datefield;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Notification;
import com.vaadin.v7.ui.DateField;

@SuppressWarnings("deprecation")
public class CompatibilityDateFieldShortcut extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        String dateFormat = "dd/MM/yyyy";

        DateField dateField = new DateField();
        dateField.setValue(new Date(2018 - 1900, 0, 11));
        dateField.setDateFormat(dateFormat);

        dateField.addShortcutListener(
                new ShortcutListener("Enter", KeyCode.ENTER, null) {
                    @Override
                    public void handleAction(Object sender, Object target) {
                        SimpleDateFormat df = new SimpleDateFormat(dateFormat);
                        Notification.show(df.format(dateField.getValue()));
                    }
                });

        addComponent(dateField);
    }

    @Override
    protected String getTestDescription() {
        return "Modify the date manually (without using the popup element) and"
                + " then press Enter. The notification should show the modified"
                + " value instead of the old value.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10854;
    }
}
