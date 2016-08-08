package com.vaadin.tests.components.abstractfield;

import java.util.Date;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.legacy.ui.LegacyTextField;
import com.vaadin.server.ErrorEvent;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.UserError;
import com.vaadin.ui.ComboBox;

public class TextFieldConversions extends AbstractComponentDataBindingTest {

    private LegacyTextField tf;

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

        tf = new LegacyTextField("TextField");
        addComponent(tf);
        tf.setErrorHandler(new ErrorHandler() {

            @Override
            public void error(ErrorEvent event) {
                tf.setComponentError(new UserError("Invalid value"));
            }
        });
    }
}
