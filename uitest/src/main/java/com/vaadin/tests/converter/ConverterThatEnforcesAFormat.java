package com.vaadin.tests.converter;

import java.util.Locale;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.TextField;

public class ConverterThatEnforcesAFormat extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        final TextField tf = new TextField(
                "This field should always be formatted with 3 digits");
        tf.setLocale(Locale.ENGLISH);
        // this is needed so that IE tests pass
        tf.setNullRepresentation("");
        tf.setConverter(new StringToDoubleConverterWithThreeFractionDigits());
        tf.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                log("Value changed to "
                        + event.getProperty().getValue()
                        + "(converted value is "
                        + tf.getConvertedValue()
                        + "). Two-way conversion gives: "
                        + tf.getConverter().convertToPresentation(
                                tf.getConverter().convertToModel(tf.getValue(),
                                        Double.class, tf.getLocale()),
                                String.class, tf.getLocale()) + ")");
            }
        });
        tf.setImmediate(true);
        addComponent(tf);
        tf.setConvertedValue(50.0);
    }

    @Override
    protected String getTestDescription() {
        return "Entering a valid double in the field should always cause the field contents to be formatted to contain 3 digits after the decimal point";
    }

    @Override
    protected Integer getTicketNumber() {
        return 8191;
    }

}
