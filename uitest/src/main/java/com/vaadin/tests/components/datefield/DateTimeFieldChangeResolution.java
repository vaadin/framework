package com.vaadin.tests.components.datefield;

import java.util.Locale;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.datefield.DateTimeResolution;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateTimeField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

public class DateTimeFieldChangeResolution extends AbstractReindeerTestUI {

    public static final String DATEFIELD_ID = "datefield";
    // The ID of a button is BUTTON_BASE_ID + resolution, e.g. button-month
    public static final String BUTTON_BASE_ID = "button-";

    @Override
    protected void setup(VaadinRequest request) {
        final DateTimeField dateField = new DateTimeField("Enter date");
        dateField.setResolution(DateTimeResolution.YEAR);
        dateField.setId(DATEFIELD_ID);
        addComponent(dateField);

        Label label = new Label("Select resolution");
        addComponent(label);
        HorizontalLayout hlayout = new HorizontalLayout();
        addComponent(hlayout);
        for (final DateTimeResolution value : DateTimeResolution.values()) {
            String resolutionString = value.toString().toLowerCase(Locale.ROOT);
            Button button = new Button(resolutionString);
            button.addClickListener(event -> dateField.setResolution(value));
            button.setId(BUTTON_BASE_ID + resolutionString);
            hlayout.addComponent(button);
        }
    }

    @Override
    protected String getTestDescription() {
        return "The calendar should always have the correct resolution and the text field should be empty before selecting a date.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14174;
    }

}
