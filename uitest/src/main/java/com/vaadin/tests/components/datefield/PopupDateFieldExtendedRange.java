package com.vaadin.tests.components.datefield;

import java.util.Calendar;
import java.util.Locale;

import com.vaadin.legacy.ui.LegacyPopupDateField;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

@SuppressWarnings("serial")
public class PopupDateFieldExtendedRange extends AbstractTestUI {

    private Calendar date = Calendar.getInstance();

    @Override
    protected void setup(VaadinRequest request) {
        date.set(2011, 0, 1);

        getLayout().setSpacing(true);

        final LegacyPopupDateField[] fields = new LegacyPopupDateField[3];

        fields[0] = makeDateField();
        fields[0].setLocale(new Locale("fi", "FI"));
        fields[0].setCaption("Finnish locale");

        fields[1] = makeDateField();
        fields[1].setLocale(new Locale("en", "US"));
        fields[1].setCaption("US English locale");

        fields[2] = makeDateField();
        fields[2].setLocale(new Locale("fi", "FI"));
        fields[2].setShowISOWeekNumbers(true);
        fields[2].setCaption("Finnish locale with week numbers");

        for (LegacyPopupDateField f : fields) {
            addComponent(f);
        }

        addComponent(new Button("Change date", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                date.set(2010, 1, 16);
                for (LegacyPopupDateField f : fields) {
                    f.setValue(date.getTime());
                }
            }
        }));
    }

    @Override
    protected String getTestDescription() {
        return "Show a few days of the preceding and following months in the datefield popup";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6718;
    }

    private LegacyPopupDateField makeDateField() {
        LegacyPopupDateField pdf = new LegacyPopupDateField();
        pdf.setResolution(Resolution.DAY);
        pdf.setValue(date.getTime());
        return pdf;
    }
}
