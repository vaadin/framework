package com.vaadin.tests.components.abstractfield;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.TextField;

public class IntegerFieldWithoutDataSource extends TestBase {

    private Log log = new Log(5);

    @Override
    protected void setup() {
        addComponent(log);

        TextField tf = createIntegerTextField();
        tf.setCaption(tf.getCaption() + "(invalid allowed)");
        addComponent(tf);
        tf = createIntegerTextField();
        tf.setInvalidAllowed(false);
        tf.setCaption(tf.getCaption() + "(invalid not allowed)");
        addComponent(tf);
    }

    private TextField createIntegerTextField() {
        final TextField tf = new TextField("Enter an integer");
        tf.setConverter(Integer.class);
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
