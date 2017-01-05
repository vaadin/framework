package com.vaadin.tests.components.abstractfield;

import com.vaadin.v7.ui.DateField;

public class DateFieldBackedByString extends AbstractComponentDataBindingTest {

    private String s = null;

    @Override
    protected void createFields() {
        DateField df = new DateField("Date field");
        addComponent(df);
        df.setPropertyDataSource(
                new com.vaadin.v7.data.util.ObjectProperty<>(s, String.class));

    }
}
