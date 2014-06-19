package com.vaadin.tests.components.datefield;

import java.util.Calendar;
import java.util.Locale;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.DateField;

public class DateFieldReadOnly extends AbstractTestUI {

    @Override
    protected String getTestDescription() {
        return "A read-only DateField should not show the popup button and not be editable.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3163;
    }

    @Override
    protected void setup(VaadinRequest request) {
        final DateField timeField = new DateField("A read-only datefield");
        timeField.setResolution(DateField.RESOLUTION_SEC);
        timeField.setDateFormat("HH:mm:ss");
        timeField.setCaption(null);
        timeField.setIcon(null);
        timeField.setWidth("8em");
        timeField.addStyleName("timeField");
        timeField.setLocale(new Locale("fi"));

        // Set date so that testing always has same time
        Calendar c = Calendar.getInstance(Locale.ENGLISH);
        c.set(2009, 05, 12, 0, 0, 0);

        timeField.setValue(c.getTime());
        timeField.setReadOnly(true);

        addComponent(timeField);

        Button b = new Button("Switch read-only");
        b.addListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                timeField.setReadOnly(!timeField.isReadOnly());
            }
        });

        addComponent(b);
    }
}
