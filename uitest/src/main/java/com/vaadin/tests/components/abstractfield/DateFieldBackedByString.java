package com.vaadin.tests.components.abstractfield;

import com.vaadin.legacy.ui.LegacyDateField;

public class DateFieldBackedByString extends AbstractComponentDataBindingTest {

    private String s = null;

    @Override
    protected void createFields() {
        LegacyDateField df = new LegacyDateField("Date field");
        addComponent(df);
        df.setPropertyDataSource(new com.vaadin.data.util.ObjectProperty<String>(
                s, String.class));

    }
}
