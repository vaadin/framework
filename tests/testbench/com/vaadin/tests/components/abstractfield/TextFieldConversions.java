package com.vaadin.tests.components.abstractfield;

import java.util.Date;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.terminal.UserError;
import com.vaadin.ui.AbstractComponent.ComponentErrorEvent;
import com.vaadin.ui.AbstractComponent.ComponentErrorHandler;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextField;

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

            public void valueChange(ValueChangeEvent event) {
                tf.setPropertyDataSource(new ObjectProperty<Object>(o,
                        (Class<Object>) dataType.getValue()));
            }
        });
        addComponent(dataType);

        tf = new TextField("TextField");
        addComponent(tf);
        tf.setErrorHandler(new ComponentErrorHandler() {

            public boolean handleComponentError(ComponentErrorEvent event) {
                tf.setComponentError(new UserError("Invalid value"));
                return true;
            }
        });
    }
}
