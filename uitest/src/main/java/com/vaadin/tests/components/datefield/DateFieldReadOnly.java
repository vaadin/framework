package com.vaadin.tests.components.datefield;

import java.time.LocalDate;
import java.util.Locale;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.components.TestDateField;
import com.vaadin.ui.AbstractDateField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

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
        final AbstractDateField timeField = new TestDateField(
                "A read-only datefield");
        timeField.setCaption(null);
        timeField.setIcon(null);
        timeField.setWidth("8em");
        timeField.addStyleName("timeField");
        timeField.setLocale(new Locale("fi"));

        // Set date so that testing always has same time
        timeField.setValue(LocalDate.of(2009, 6, 12));
        timeField.setReadOnly(true);

        addComponent(timeField);

        Button b = new Button("Switch read-only");
        b.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                timeField.setReadOnly(!timeField.isReadOnly());
            }
        });

        addComponent(b);
    }
}
