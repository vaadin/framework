package com.vaadin.tests.minitutorials;

import com.vaadin.terminal.WrappedRequest;
import com.vaadin.tests.components.AbstractTestRoot;
import com.vaadin.ui.TextField;

public class CustomConverterFactoryRoot extends AbstractTestRoot {
    @Override
    public void setup(WrappedRequest request) {
        getApplication().setConverterFactory(new MyConverterFactory());

        TextField tf = new TextField("This is my double field");
        tf.setImmediate(true);
        tf.setConverter(Double.class);
        addComponent(tf);

        // As we do not set the locale explicitly for the field we set the value
        // after the field has been attached so it uses the application locale
        // for conversion
        tf.setConvertedValue(50.1);

    }

    @Override
    protected String getTestDescription() {
        return "Mini tutorial for https://vaadin.com/wiki/-/wiki/Main/Changing%20the%20default%20converters%20for%20an%20application";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }
}
