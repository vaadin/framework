package com.vaadin.tests.components.datefield;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.PopupDateField;

@SuppressWarnings("serial")
public class PopupDateFieldStates extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final GridLayout gridLayout = new GridLayout(2, 2);
        gridLayout.setSpacing(true);

        gridLayout.addComponent(createPopupDateField(true, true));
        gridLayout.addComponent(createPopupDateField(true, false));
        gridLayout.addComponent(createPopupDateField(false, true));
        gridLayout.addComponent(createPopupDateField(false, false));

        getLayout().addComponent(gridLayout);
    }

    @Override
    protected String getTestDescription() {
        return "Test that PopupDateField is rendered consistently across browsers";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14565;
    }

    private static PopupDateField createPopupDateField(final boolean enabled,
            final boolean textFieldEnabled) {
        final PopupDateField popupDatefield = new PopupDateField();

        Calendar cal = GregorianCalendar.getInstance();
        cal.set(Calendar.DATE, 3);
        cal.set(Calendar.MONTH, Calendar.SEPTEMBER);
        cal.set(Calendar.YEAR, 2014);
        cal.set(Calendar.HOUR_OF_DAY, 8);
        final Date currentDate = cal.getTime();

        popupDatefield.setValue(currentDate);
        popupDatefield.setCaption("Enabled: " + enabled
                + ", Text field enabled: " + textFieldEnabled);
        popupDatefield.setEnabled(enabled);
        popupDatefield.setTextFieldEnabled(textFieldEnabled);
        return popupDatefield;
    }

}
