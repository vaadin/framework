package com.vaadin.tests.components.abstractfield;

import com.vaadin.ui.DateField;

public class DateFieldBackedByString extends AbstractComponentDataBindingTest {

    private String s = null;

    @Override
    protected void createFields() {
        DateField df = new DateField("Date field");
        addComponent(df);
        df.setPropertyDataSource(new com.vaadin.data.util.ObjectProperty<String>(
                s, String.class));

    }
}
