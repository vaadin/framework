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
package com.vaadin.tests.components.formlayout;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;
import com.vaadin.v7.ui.NativeSelect;

public class CaptionEnableDisable extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        setContent(createFormLayout());

    }

    public FormLayout createFormLayout() {
        FormLayout layout = new FormLayout();
        final TextField textField = new TextField("TextField");
        textField.setEnabled(false);
        layout.addComponent(textField);

        final ComboBox<String> combobox = new ComboBox<>("Combobox");
        combobox.setEnabled(false);
        layout.addComponent(combobox);

        final NativeSelect nativeSelect = new NativeSelect("NativeSelect");
        nativeSelect.setEnabled(false);
        layout.addComponent(nativeSelect);

        final CheckBox checkBox = new CheckBox("Checkbox");
        checkBox.setEnabled(false);
        layout.addComponent(checkBox);

        layout.addComponent(new Button("Toggle components enabled", event -> {
            combobox.setEnabled(!combobox.isEnabled());
            textField.setEnabled(!textField.isEnabled());
            checkBox.setEnabled(!checkBox.isEnabled());
            nativeSelect.setEnabled(!nativeSelect.isEnabled());
        }));
        return layout;
    }

    @Override
    protected String getTestDescription() {
        return "";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12062;
    }

}
