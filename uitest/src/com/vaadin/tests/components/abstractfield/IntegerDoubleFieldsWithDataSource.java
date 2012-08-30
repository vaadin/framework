package com.vaadin.tests.components.abstractfield;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.validator.DoubleValidator;
import com.vaadin.data.validator.IntegerValidator;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.TextField;

public class IntegerDoubleFieldsWithDataSource extends TestBase {

    private Log log = new Log(5);

    @Override
    protected void setup() {
        addComponent(log);

        TextField tf = createIntegerTextField();
        tf.addValidator(new IntegerValidator("Must be an Integer"));
        addComponent(tf);

        tf = createIntegerTextField();
        tf.setCaption("Enter a double");
        tf.setPropertyDataSource(new ObjectProperty<Double>(2.1));
        tf.addValidator(new DoubleValidator("Must be a Double"));
        addComponent(tf);
    }

    private TextField createIntegerTextField() {
        final TextField tf = new TextField("Enter an integer");
        tf.setPropertyDataSource(new ObjectProperty<Integer>(new Integer(2)));
        tf.setImmediate(true);
        tf.addListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                try {
                    log.log("Value for " + tf.getCaption() + " changed to "
                            + tf.getValue());
                    log.log("Converted value is " + tf.getConvertedValue());
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
            }
        });

        return tf;
    }

    @Override
    protected String getDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }

}
