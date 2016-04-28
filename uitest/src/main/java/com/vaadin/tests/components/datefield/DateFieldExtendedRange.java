package com.vaadin.tests.components.datefield;

import java.util.Calendar;
import java.util.Locale;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.DateField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.PopupDateField;

@SuppressWarnings("serial")
public class DateFieldExtendedRange extends TestBase {

    private Calendar date = Calendar.getInstance();

    @Override
    protected void setup() {
        date.set(2011, 0, 1);

        GridLayout layout = new GridLayout(2, 3);
        layout.setWidth("600px");
        layout.setSpacing(true);

        final DateField[] fields = new DateField[6];

        Locale fi = new Locale("fi", "FI");
        Locale us = new Locale("en", "US");

        fields[0] = makeDateField(true, fi, "Finnish locale");
        fields[1] = makeDateField(false, fi, "Finnish locale");

        fields[2] = makeDateField(true, us, "US English locale");
        fields[3] = makeDateField(false, us, "US English locale");

        fields[4] = makeDateField(true, fi, "Finnish locale with week numbers");
        fields[4].setShowISOWeekNumbers(true);
        fields[5] = makeDateField(false, fi, "Finnish locale with week numbers");
        fields[5].setShowISOWeekNumbers(true);

        for (DateField f : fields) {
            layout.addComponent(f);
        }

        addComponent(layout);

        addComponent(new Button("Change date", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                date.set(2010, 1, 16);
                for (DateField f : fields) {
                    f.setValue(date.getTime());
                }
            }
        }));
    }

    @Override
    protected String getDescription() {
        return "Show a few days of the preceding and following months in the datefield popup";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6718;
    }

    private DateField makeDateField(boolean isPopup, Locale locale,
            String caption) {
        DateField df = isPopup ? new PopupDateField() : new InlineDateField();
        df.setResolution(DateField.RESOLUTION_DAY);
        df.setValue(date.getTime());
        df.setLocale(locale);
        df.setCaption(caption);
        return df;
    }
}
