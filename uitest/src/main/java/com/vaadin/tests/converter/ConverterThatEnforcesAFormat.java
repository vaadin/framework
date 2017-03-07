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
package com.vaadin.tests.converter;

import java.util.Locale;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.v7.data.Property.ValueChangeEvent;
import com.vaadin.v7.data.Property.ValueChangeListener;
import com.vaadin.v7.ui.TextField;

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
                log("Value changed to " + event.getProperty().getValue()
                        + "(converted value is " + tf.getConvertedValue()
                        + "). Two-way conversion gives: "
                        + tf.getConverter().convertToPresentation(
                                tf.getConverter().convertToModel(tf.getValue(),
                                        Double.class, tf.getLocale()),
                                String.class, tf.getLocale())
                        + ")");
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
