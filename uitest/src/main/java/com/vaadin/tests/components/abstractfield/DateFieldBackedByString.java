package com.vaadin.tests.components.abstractfield;

import com.vaadin.v7.ui.LegacyDateField;

public class DateFieldBackedByString extends AbstractComponentDataBindingTest {

    private String s = null;

    @Override
    protected void createFields() {
        LegacyDateField df = new LegacyDateField("Date field");
        addComponent(df);
        df.setPropertyDataSource(
                new com.vaadin.data.util.ObjectProperty<String>(s,
                        String.class));

    }
}
