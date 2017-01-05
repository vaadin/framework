package com.vaadin.tests.components.datefield;

import java.time.LocalDate;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.DateField;
import com.vaadin.ui.GridLayout;

@SuppressWarnings("serial")
public class PopupDateFieldStates extends AbstractReindeerTestUI {

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

    private static DateField createPopupDateField(final boolean enabled,
            final boolean textFieldEnabled) {
        final DateField popupDatefield = new DateField();

        popupDatefield.setValue(LocalDate.of(2014, 9, 3));
        popupDatefield.setCaption("Enabled: " + enabled
                + ", Text field enabled: " + textFieldEnabled);
        popupDatefield.setEnabled(enabled);
        popupDatefield.setTextFieldEnabled(textFieldEnabled);
        return popupDatefield;
    }

}
