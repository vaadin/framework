package com.vaadin.tests.components.datefield;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;

import java.time.LocalDate;

public class DateFieldResetValueWithinListener extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        LocalDate sourceDate = LocalDate.now();
        DateField df = new DateField("Date: ");
        df.setDateFormat("d.M.yyyy");
        df.setId("dateField1");
        df.setValue(sourceDate);
        df.addValueChangeListener(evt -> {
            if (evt.getValue().isAfter(sourceDate)) {
                df.setValue(LocalDate.now());
            }
        });

        addComponent(df);
        Button setV = new Button("Set date after the current", e -> {
            df.setValue(LocalDate.now().plusDays(5));
        });
        setV.setId("setValueButton");
        addComponent(setV);
    }
}
