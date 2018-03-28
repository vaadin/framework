package com.vaadin.tests.components.datefield;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.datefield.DateResolution;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.tests.components.TestDateField;
import com.vaadin.ui.AbstractLocalDateField;
import com.vaadin.ui.VerticalLayout;

public class PopupClosingWithEsc extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        AbstractLocalDateField df1 = new TestDateField("Day");
        df1.setId("day");
        df1.setResolution(DateResolution.DAY);

        AbstractLocalDateField df2 = new TestDateField("Month");
        df2.setId("month");
        df2.setResolution(DateResolution.MONTH);

        AbstractLocalDateField df3 = new TestDateField("Year");
        df3.setId("year");
        df3.setResolution(DateResolution.YEAR);

        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSpacing(true);
        layout.addComponents(df1, df2, df3);
        setContent(layout);
    }

    @Override
    protected String getTestDescription() {
        return "Testing that the DateField popup can be closed with ESC key.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12317;
    }

}
