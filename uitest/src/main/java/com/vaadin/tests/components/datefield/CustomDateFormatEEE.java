package com.vaadin.tests.components.datefield;

import java.time.LocalDate;
import java.util.Locale;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.datefield.DateResolution;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.tests.components.TestDateField;
import com.vaadin.ui.AbstractLocalDateField;
import com.vaadin.ui.VerticalLayout;

public class CustomDateFormatEEE extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        AbstractLocalDateField df = new TestDateField(
                "Should display 14/03/2014 Fri");
        df.setResolution(DateResolution.DAY);
        df.setLocale(new Locale("en", "US"));

        String pattern = "dd/MM/yyyy EEE";
        df.setDateFormat(pattern);
        df.setValue(LocalDate.of(2014, 3, 14)); // Friday
        df.setWidth("200px");

        VerticalLayout layout = new VerticalLayout();
        layout.addComponent(df);
        layout.setMargin(true);
        setContent(layout);
    }

    @Override
    protected String getTestDescription() {
        return "Verifies that \"EEE\" works as a part of custom date pattern";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13443;
    }

}
