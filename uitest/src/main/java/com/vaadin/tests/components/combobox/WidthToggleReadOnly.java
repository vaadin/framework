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

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;

public class WidthToggleReadOnly extends TestBase {

    @Override
    protected void setup() {
        ComboBox combo = createNewComboBoxA("Untouched combobox");
        addComponent(combo);

        combo = createNewComboBoxA("Toggled combobox");
        addComponent(combo);
        addComponent(createReadOnlyForComboBox(combo));
    }

    private ComboBox<String> createNewComboBoxA(String caption) {
        ComboBox<String> combo = new ComboBox<>(caption);
        combo.setItems("first");
        combo.setValue("first");

        addComponent(combo);

        return combo;
    }

    private CheckBox createReadOnlyForComboBox(ComboBox combo) {
        CheckBox readonly = new CheckBox("Second combobox is read only");
        readonly.setValue(combo.isReadOnly());
        readonly.addValueChangeListener(
                event -> combo.setReadOnly(event.getValue()));
        addComponent(readonly);
        return readonly;
    }

    @Override
    protected String getDescription() {
        return "Check that toggling read only mode of second combobox does not change it's width.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5833;
    }

}
