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
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.util.BeanItem;
import com.vaadin.v7.ui.TextField;

public class IntegerTextFieldDataSource extends AbstractReindeerTestUI {

    public class MyBean {
        private int value;

        public int getValue() {
            return value;
        }

        public void setValue(int integer) {
            value = integer;
        }
    }

    @Override
    protected void setup(VaadinRequest request) {
        final MyBean myBean = new MyBean();
        BeanItem<MyBean> beanItem = new BeanItem<>(myBean);

        final Property<Integer> integerProperty = beanItem
                .getItemProperty("value");
        final TextField textField = new TextField("Text field",
                integerProperty);

        Button submitButton = new Button("Submit value", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                String uiValue = textField.getValue();
                Integer propertyValue = integerProperty.getValue();
                int dataModelValue = myBean.getValue();

                Notification.show("UI value (String): " + uiValue
                        + "\nProperty value (Integer): " + propertyValue
                        + "\nData model value (int): " + dataModelValue);
            }
        });

        addComponent(new Label("Text field type: " + textField.getType()));
        addComponent(
                new Label("Text field type: " + integerProperty.getType()));
        addComponent(textField);
        addComponent(submitButton);
    }

    @Override
    protected String getTestDescription() {
        return "Mini tutorial for https://vaadin.com/wiki/-/wiki/Main/Creating%20a%20TextField%20for%20Integer%20only%20input%20using%20a%20data%20source";
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }

}
