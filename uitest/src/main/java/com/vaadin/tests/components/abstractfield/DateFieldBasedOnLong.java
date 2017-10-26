package com.vaadin.tests.components.abstractfield;

import java.util.Date;

import com.vaadin.ui.Button;
import com.vaadin.v7.data.util.ObjectProperty;
import com.vaadin.v7.ui.PopupDateField;

public class DateFieldBasedOnLong extends AbstractComponentDataBindingTest {

    private Long l = null;
    private ObjectProperty<Long> property;

    @Override
    protected void createFields() {
        PopupDateField pdf = new PopupDateField("DateField");
        addComponent(pdf);
        property = new ObjectProperty<>(l, Long.class);
        pdf.setPropertyDataSource(property);

        property.setValue(new Date(2011 - 1900, 4, 6).getTime());

        addComponent(new Button("Set property value to 10000L",
                event -> property.setValue(10000L)));
    }

}
