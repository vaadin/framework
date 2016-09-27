package com.vaadin.tests.components.datefield;

import java.time.LocalDate;
import java.util.Locale;
import java.util.stream.Stream;

import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.AbstractDateField;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.InlineDateField;

@SuppressWarnings("serial")
public class DateFieldExtendedRange extends TestBase {

    @Override
    protected void setup() {
        GridLayout layout = new GridLayout(2, 3);
        layout.setWidth("600px");
        layout.setSpacing(true);

        final AbstractDateField[] fields = new AbstractDateField[6];

        Locale fi = new Locale("fi", "FI");
        Locale us = new Locale("en", "US");

        fields[0] = makeDateField(true, fi, "Finnish locale");
        fields[1] = makeDateField(false, fi, "Finnish locale");

        fields[2] = makeDateField(true, us, "US English locale");
        fields[3] = makeDateField(false, us, "US English locale");

        fields[4] = makeDateField(true, fi, "Finnish locale with week numbers");
        fields[4].setShowISOWeekNumbers(true);
        fields[5] = makeDateField(false, fi,
                "Finnish locale with week numbers");
        fields[5].setShowISOWeekNumbers(true);

        for (AbstractDateField f : fields) {
            layout.addComponent(f);
        }

        addComponent(layout);

        addComponent(new Button("Change date", event -> Stream.of(fields)
                .forEach(field -> field.setValue(LocalDate.of(2010, 2, 16)))));
    }

    @Override
    protected String getDescription() {
        return "Show a few days of the preceding and following months in the datefield popup";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6718;
    }

    private AbstractDateField makeDateField(boolean isPopup, Locale locale,
            String caption) {
        AbstractDateField df = isPopup ? new DateField()
                : new InlineDateField();
        df.setResolution(Resolution.DAY);
        df.setValue(LocalDate.of(2011, 1, 1));
        df.setLocale(locale);
        df.setCaption(caption);
        return df;
    }
}
