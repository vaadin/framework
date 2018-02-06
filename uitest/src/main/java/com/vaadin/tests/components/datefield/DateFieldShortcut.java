package com.vaadin.tests.components.datefield;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.vaadin.annotations.Widgetset;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Notification;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class DateFieldShortcut extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        String dateFormat = "dd/MM/yyyy";

        DateField dateField = new DateField();
        dateField.setValue(LocalDate.of(2018, 1, 11));
        dateField.setDateFormat(dateFormat);

        dateField.addShortcutListener(
                new ShortcutListener("Enter", KeyCode.ENTER, null) {
                    @Override
                    public void handleAction(Object sender, Object target) {
                        Notification.show(dateField.getValue()
                                .format(DateTimeFormatter
                                        .ofPattern(dateFormat)));
                    }
                });

        addComponent(dateField);
    }

    @Override
    protected String getTestDescription() {
        return "Modify the date maually (without using the popup element) and"
                + " then press Enter. The notification should show the modified"
                + " value instead of the old value.";
    }
}
