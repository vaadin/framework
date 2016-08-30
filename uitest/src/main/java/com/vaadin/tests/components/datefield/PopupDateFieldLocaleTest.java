package com.vaadin.tests.components.datefield;

import java.util.Calendar;
import java.util.Locale;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.PopupDateField;
import com.vaadin.shared.ui.datefield.Resolution;

public class PopupDateFieldLocaleTest extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        // Set a specific time for the PopupDateField
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2000);
        cal.set(Calendar.DAY_OF_YEAR, 1);
        cal.set(Calendar.HOUR_OF_DAY, 1);
        cal.set(Calendar.MINUTE, 1);
        cal.set(Calendar.SECOND, 1);
        cal.set(Calendar.MILLISECOND, 1);

        final PopupDateField pdf = new PopupDateField();
        pdf.setLocale(Locale.ENGLISH);
        pdf.setValue(cal.getTime());
        pdf.setImmediate(true);
        pdf.setResolution(Resolution.SECOND);
        addComponent(pdf);

        pdf.addValueChangeListener(event -> pdf.setLocale(Locale.FRENCH));
    }

    @Override
    protected String getTestDescription() {
        return "Changing the locale while the popupdatefield is visible can "
                + "result in the locale remaining at the previous value; the locale "
                + "is only changed once the current month is changed.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12135;
    }

}
