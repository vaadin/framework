package com.vaadin.tests.components.datefield;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.AbstractLocalDateField;
import com.vaadin.ui.DateField;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class DateFieldNavigationKeyBoard extends AbstractTestUI {

    static final String DATEFIELD_ID = "datefield";

    @Override
    protected void setup(VaadinRequest request) {
        final AbstractLocalDateField df = new DateField();
        df.setId(DATEFIELD_ID);
        addComponent(df);
    }

    @Override
    protected String getTestDescription() {
        return "Navigation in popup should be possible via arrows in Firefox 65 and later";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11465;
    }

}
