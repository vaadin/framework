package com.vaadin.tests.components.datefield;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.datefield.DateResolution;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.DateField;

import java.time.LocalDate;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class DateFieldMonthResolutionClick extends AbstractTestUIWithLog {
    @Override
    protected void setup(VaadinRequest request) {
        DateField dyf = new DateField();
        dyf.setDateFormat("yyyy");
        dyf.setRangeStart(LocalDate.of(2012, 01, 31));
        dyf.setResolution(DateResolution.YEAR);
        dyf.setCaption("Resolution : year");
        dyf.setId("yearResolutionDF");
        dyf.addValueChangeListener(event -> {
            log("Current value for the 1.st DF: " + event.getValue()
                    + " isUserOriginated: " + event.isUserOriginated());
        });
        addComponent(dyf);
        DateField dmf = new DateField();
        dmf.setDateFormat("M/yyyy");
        dmf.setCaption("Resolution : month");
        dmf.setResolution(DateResolution.MONTH);
        dmf.setId("monthResolutionDF");
        dmf.setRangeStart(LocalDate.now());
        dmf.addValueChangeListener(event -> {
            log("Current value for the 2.st DF: " + event.getValue()
                    + " isUserOriginated: " + event.isUserOriginated());
        });
        addComponent(dmf);
    }
}
