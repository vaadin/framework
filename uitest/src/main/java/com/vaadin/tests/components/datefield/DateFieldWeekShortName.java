package com.vaadin.tests.components.datefield;

import java.time.LocalDate;
import java.util.Locale;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.DateField;

public class DateFieldWeekShortName extends AbstractTestUI {

    @Override
    protected String getTestDescription() {
        return "DateField to correctly show week name for locales with different first week day";
    }

    @Override
    protected Integer getTicketNumber() {
        return 9200;
    }

    @Override
    protected void setup(VaadinRequest request) {
        LocalDate localDate = LocalDate.of(2017, 10, 20);

        DateField ar = new DateField();
        ar.setValue(localDate);
        ar.setLocale(new Locale("ar"));
        addComponent(ar);

        DateField de = new DateField();
        de.setLocale(Locale.GERMAN);
        de.setValue(localDate);
        addComponent(de);

        DateField en = new DateField();
        en.setLocale(Locale.ENGLISH);
        en.setValue(localDate);
        addComponent(en);
    }

}
