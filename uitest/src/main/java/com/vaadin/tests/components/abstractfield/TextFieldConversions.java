package com.vaadin.tests.components.abstractfield;

import java.util.Date;

import com.vaadin.server.ErrorEvent;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.UserError;
import com.vaadin.v7.data.Property.ValueChangeEvent;
import com.vaadin.v7.data.Property.ValueChangeListener;
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

        dataType.addListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                tf.setPropertyDataSource(new ObjectProperty<Object>(o,
                        (Class<Object>) dataType.getValue()));
            }
        });
        addComponent(dataType);

        tf = new TextField("TextField");
        addComponent(tf);
        tf.setErrorHandler(new ErrorHandler() {

            @Override
            public void error(ErrorEvent event) {
                tf.setComponentError(new UserError("Invalid value"));
            }
        });
    }
}
