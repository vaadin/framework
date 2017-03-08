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
package com.vaadin.tests.components.combobox;

import java.util.Arrays;

import com.vaadin.tests.components.TestBase;
import com.vaadin.v7.data.Property.ValueChangeEvent;
import com.vaadin.v7.data.Property.ValueChangeListener;
import com.vaadin.v7.ui.Select;
import com.vaadin.v7.ui.TextField;

public class ComboBoxTextFieldEventOrder extends TestBase {

    @Override
    protected void setup() {
        TextField textField = new TextField("text field");
        textField.setImmediate(true);
        final Select select = new Select("select",
                Arrays.asList("1", "2", "3", "4"));
        textField.addListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                select.addItem(Long.valueOf(select.size() + 1).toString()); // or
                                                                            // just
                                                                            // select.requestRepaint();
            }
        });
        addComponent(textField);
        addComponent(select);
    }

    @Override
    protected String getDescription() {
        return "Entering a text in a TextField and then clicking on the button in a ComboBox should cause the TextField value change to be sent first and the ComboBox filtering afterwards. Failure to do so will cause errors if the value change listener modifies the ComboBox";
    }

    @Override
    protected Integer getTicketNumber() {
        return 7481;
    }
}
