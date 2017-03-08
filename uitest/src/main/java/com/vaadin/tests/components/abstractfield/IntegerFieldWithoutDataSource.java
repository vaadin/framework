/*
 * Copyright 2000-2016 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tests.components.abstractfield;

import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.Log;
import com.vaadin.v7.data.Property.ValueChangeEvent;
import com.vaadin.v7.data.Property.ValueChangeListener;
import com.vaadin.v7.ui.TextField;

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
