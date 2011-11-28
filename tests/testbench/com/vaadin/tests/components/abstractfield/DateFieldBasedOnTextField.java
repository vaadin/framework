package com.vaadin.tests.components.abstractfield;

import java.util.Date;

import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.TextField;

public class DateFieldBasedOnTextField extends AbstractComponentDataBindingTest {

    private TextField tf;

    @Override
    protected void createFields() {
        tf = new TextField("Enter a date");
        tf.setWidth("200px");
        addComponent(tf);
        tf.setImmediate(true);
        ObjectProperty<Date> property = new ObjectProperty<Date>(new Date());
        tf.setPropertyDataSource(property);

    }

}
