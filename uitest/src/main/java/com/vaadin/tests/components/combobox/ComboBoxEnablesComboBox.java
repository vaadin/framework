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
import com.vaadin.tests.util.ItemDataProvider;
import com.vaadin.ui.ComboBox;

public class ComboBoxEnablesComboBox extends TestBase {

    private ComboBox cb2;

    @Override
    protected void setup() {
        ComboBox<String> cb = new ComboBox<>("Always enabled");
        cb.setDataProvider(new ItemDataProvider(10));
        cb.addValueChangeListener(event -> cb2.setEnabled(true));
        cb2 = new ComboBox<String>("Initially disabled");
        cb.setDataProvider(new ItemDataProvider(10));
        cb2.setEnabled(false);

        addComponent(cb);
        addComponent(cb2);
    }

    @Override
    protected String getDescription() {
        return "Selecting an item in the first combobox enables the second.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 4632;
    }

}
