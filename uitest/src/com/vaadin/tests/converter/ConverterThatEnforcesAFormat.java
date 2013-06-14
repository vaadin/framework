package com.vaadin.tests.converter;

import java.util.Locale;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.TextField;

public class ConverterThatEnforcesAFormat extends TestBase {

    private Log log = new Log(5);

    @Override
    protected void setup() {
        final TextField tf = new TextField(
                "This field should always be formatted with 3 digits");
        tf.setLocale(Locale.ENGLISH);
        tf.setConverter(new StringToDoubleConverterWithThreeFractionDigits());
        tf.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                log.log("Value changed to "
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
        addComponent(log);
        addComponent(tf);
        tf.setConvertedValue(50.0);
    }

    @Override
    protected String getDescription() {
        return "Entering a valid double in the field should always cause the field contents to be formatted to contain 3 digits after the decimal point";
    }

    @Override
    protected Integer getTicketNumber() {
        return 8191;
    }

}
