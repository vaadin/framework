package com.vaadin.tests.smoke;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.v7.ui.InlineDateField;
import com.vaadin.v7.ui.PopupDateField;

/**
 * @author Vaadin Ltd
 *
 */
public class DateFieldSmoke extends AbstractTestUIWithLog {

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat(
            "yyyy.MM.dd", Locale.ENGLISH);

    @Override
    protected void setup(VaadinRequest request) {
        setLocale(Locale.ENGLISH);

        InlineDateField inline = new InlineDateField();
        PopupDateField popup = new PopupDateField();

        int year = 2016 - 1900;
        popup.setValue(new Date(year, 11, 28));
        inline.setValue(new Date(year, 11, 29));

        popup.setDateFormat("MM/dd/yy");
        inline.setDateFormat("MM/dd/yy");

        popup.addValueChangeListener(event -> log(
                "Popup value is : " + FORMAT.format(popup.getValue())));
        inline.addValueChangeListener(event -> log(
                "Inline value is : " + FORMAT.format(inline.getValue())));

        addComponents(inline, popup);
    }

}
