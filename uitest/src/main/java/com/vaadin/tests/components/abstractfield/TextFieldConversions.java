package com.vaadin.tests.components.abstractfield;

import java.util.Date;

import com.vaadin.server.UserError;
import com.vaadin.v7.data.util.ObjectProperty;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextField;

public class TextFieldConversions extends AbstractComponentDataBindingTest {

    private TextField tf;

    private Object o;

    private ComboBox dataType;

    @Override
    protected void createFields() {
        dataType = new ComboBox("Data type");
        dataType.setImmediate(true);
        dataType.addItem(Long.class);
        dataType.addItem(Integer.class);
        dataType.addItem(Double.class);
        dataType.addItem(Date.class);
        dataType.addItem(String.class);

        dataType.addValueChangeListener(event ->
                tf.setPropertyDataSource(new ObjectProperty<>(o,
                (Class<Object>) dataType.getValue())));
        addComponent(dataType);

        tf = new TextField("TextField");
        addComponent(tf);
        tf.setErrorHandler(
                event -> tf.setComponentError(new UserError("Invalid value")));
    }
}
