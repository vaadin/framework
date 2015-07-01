/*
 * Copyright 2000-2014 Vaadin Ltd.
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

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.CheckBox;

public class ComboBoxNoTextInput extends ComboBoxSelecting {

    @Override
    protected void setup(VaadinRequest request) {
        super.setup(request);
        comboBox.setTextInputAllowed(true);

        final CheckBox textInputCheckBox = new CheckBox("Text Input", true);
        textInputCheckBox.setId("textInput");
        textInputCheckBox.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                comboBox.setTextInputAllowed(textInputCheckBox.getValue());
            }
        });
        addComponent(textInputCheckBox);
    }

    @Override
    protected String getTestDescription() {
        return "ComboBox should open popup on click when text input is not allowed.";
    }

}
