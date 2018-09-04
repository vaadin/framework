package com.vaadin.tests.components.datefield;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;

import java.time.LocalDate;
import java.time.ZoneId;

public class DateFieldResetValueWithinListener extends AbstractTestUI {

    public static LocalDate initialValue = LocalDate.of(2018, 8, 4);
    public static LocalDate beforeInitialValue = LocalDate.of(2018, 7, 13);

    @Override
    protected void setup(VaadinRequest request) {
        LocalDate defaultDate = LocalDate.of(2018, 9, 4);
        DateField df = new DateField("Date: ", defaultDate);
        df.setDateFormat("d.M.yyyy");
        df.setId("dateField1");
        df.setValue(initialValue);
        df.setZoneId(ZoneId.of("Europe/Helsinki"));
        df.addValueChangeListener(evt -> {
            if (evt.getValue().isAfter(initialValue)) {
                df.setValue(beforeInitialValue);
            }
        });

        addComponent(df);
        Button setV = new Button("Set date after the current", e -> {
            LocalDate afterButtonPress = LocalDate.of(2018, 9, 12);
            df.setValue(afterButtonPress);
        });
        setV.setId("setValueButton");
        addComponent(setV);
    }
}
