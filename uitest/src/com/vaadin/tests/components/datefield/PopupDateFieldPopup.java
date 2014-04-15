package com.vaadin.tests.components.datefield;

import java.util.Calendar;
import java.util.Locale;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.DateField;
import com.vaadin.ui.PopupDateField;

public class PopupDateFieldPopup extends TestBase {

    @Override
    protected void setup() {
        // Set a specific time for the PopupDateField
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2000);
        cal.set(Calendar.DAY_OF_YEAR, 1);
        cal.set(Calendar.HOUR_OF_DAY, 1);
        cal.set(Calendar.MINUTE, 1);
        cal.set(Calendar.SECOND, 1);
        cal.set(Calendar.MILLISECOND, 1);

        PopupDateField pdf = new PopupDateField();
        pdf.setLocale(Locale.US);
        pdf.setValue(cal.getTime());
        pdf.setImmediate(true);
        pdf.setResolution(DateField.RESOLUTION_SEC);
        addComponent(pdf);
    }

    @Override
    protected String getDescription() {
        return "Changing the minute, second and millisecond parts should also result in an update of the PopupDateField popup contents.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 8391;
    }

}
