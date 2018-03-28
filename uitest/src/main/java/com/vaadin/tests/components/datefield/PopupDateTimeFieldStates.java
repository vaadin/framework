package com.vaadin.tests.components.datefield;

import java.time.LocalDateTime;
import java.util.Locale;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.DateTimeField;
import com.vaadin.ui.GridLayout;

/**
 * @author Vaadin Ltd
 *
 */
public class PopupDateTimeFieldStates extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        setLocale(Locale.ENGLISH);
        final GridLayout gridLayout = new GridLayout(2, 2);
        gridLayout.setSpacing(true);

        gridLayout.addComponent(createPopupDateTimeField(true, true));
        gridLayout.addComponent(createPopupDateTimeField(true, false));
        gridLayout.addComponent(createPopupDateTimeField(false, true));
        gridLayout.addComponent(createPopupDateTimeField(false, false));

        getLayout().addComponent(gridLayout);
    }

    @Override
    protected String getTestDescription() {
        return "Test that PopupDateTimeField is rendered consistently across browsers";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14565;
    }

    private static DateTimeField createPopupDateTimeField(final boolean enabled,
            final boolean textFieldEnabled) {
        final DateTimeField popupDatefield = new DateTimeField();

        popupDatefield.setValue(LocalDateTime.of(2014, 9, 3, 10, 34));
        popupDatefield.setCaption("Enabled: " + enabled
                + ", Text field enabled: " + textFieldEnabled);
        popupDatefield.setEnabled(enabled);
        popupDatefield.setTextFieldEnabled(textFieldEnabled);
        return popupDatefield;
    }

}
