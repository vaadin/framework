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
package com.vaadin.tests.minitutorials.v7a1;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.v7.data.util.converter.Converter.ConversionException;
import com.vaadin.v7.data.util.converter.StringToIntegerConverter;
import com.vaadin.v7.ui.TextField;

public class IntegerTextFieldStandalone extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final TextField textField = new TextField("Text field");
        textField.setConverter(new StringToIntegerConverter());

        Button submitButton = new Button("Submit value", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                String uiValue = textField.getValue();
                try {
                    Integer convertedValue = (Integer) textField
                            .getConvertedValue();
                    Notification.show("UI value (String): " + uiValue
                            + "<br />Converted value (Integer): "
                            + convertedValue);
                } catch (ConversionException e) {
                    e.printStackTrace();
                    Notification.show("Could not convert value: " + uiValue);
                }
            }
        });

        addComponent(new Label("Text field type: " + textField.getType()));
        addComponent(new Label("Converterd text field type: "
                + textField.getConverter().getModelType()));
        addComponent(textField);
        addComponent(submitButton);
    }

    @Override
    protected String getTestDescription() {
        return "Mini tutorial for https://vaadin.com/wiki/-/wiki/Main/Creating%20a%20TextField%20for%20Integer%20only%20input%20when%20not%20using%20a%20data%20source";
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }

}
