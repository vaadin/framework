package com.vaadin.tests.components.datefield;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

/**
 * Test UI for date field Popup calendar.
 *
 * @author Vaadin Ltd
 */
public abstract class DateFieldPopupPosition extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        HorizontalLayout layout = new HorizontalLayout();
        addComponent(layout);
        Label gap = new Label();
        gap.setWidth(250, Unit.PIXELS);
        layout.addComponent(gap);
        DateField field = new DateField();
        layout.addComponent(field);
    }

    @Override
    protected Integer getTicketNumber() {
        return 14757;
    }

    @Override
    protected String getTestDescription() {
        return "Calendar popup should not placed on the top of text field when "
                + "there is no space on bottom.";
    }
}
