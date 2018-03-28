package com.vaadin.tests.components.datefield;

import java.util.Timer;
import java.util.TimerTask;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.tests.components.TestDateField;
import com.vaadin.ui.AbstractLocalDateField;

public class DateFieldPopupClosingOnDetach extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        // Use polling to notice the removal of DateField.
        getUI().setPollInterval(500);

        final AbstractLocalDateField df = new TestDateField();
        getLayout().addLayoutClickListener(event -> {
            // Use a background Thread to remove the DateField 1 second
            // after being clicked.
            TimerTask removeTask = new TimerTask() {

                @Override
                public void run() {
                    getUI().access(() -> removeComponent(df));
                }
            };
            new Timer(true).schedule(removeTask, 1000);
        });

        addComponent(df);
    }

    @Override
    protected String getTestDescription() {
        return "DateField popup should be removed if it's open while the DateField is removed. "
                + "Click the popup open and a background Thread will remove the DateField after 1 second.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 18985;
    }

}
