package com.vaadin.tests.elements.datefield;

import java.time.LocalDate;
import java.util.Date;

import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Label;

public class DateFieldPopupSetValue extends AbstractTestUI {

    public static LocalDate initialDate = LocalDate.of(2015, 4, 12);
    public static Date changedDate = new Date(2015, 5, 11);

    Label counterLbl = new Label();

    @Override
    protected void setup(VaadinRequest request) {
        counterLbl.setId("counter");
        DateField df = new DateField();
        df.setDateFormat("MM/dd/yy");
        df.setValue(initialDate);
        df.addValueChangeListener(new EventCounter());
        addComponent(df);
        addComponent(counterLbl);
    }

    @Override
    protected String getTestDescription() {
        return "Test popupDateFieldElement getValue/setValue";
    }

    @Override
    protected Integer getTicketNumber() {
        return 15092;
    }

    private class EventCounter implements ValueChangeListener<LocalDate> {
        private int counter = 0;

        @Override
        public void valueChange(ValueChangeEvent<LocalDate> event) {
            counter++;
            counterLbl.setValue("" + counter);
        }

    }
}
