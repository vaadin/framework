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

import java.util.ArrayList;
import java.util.List;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;

public class ComboBoxValueUpdate extends TestBase {

    @Override
    protected Integer getTicketNumber() {
        return 2451;
    }

    @Override
    protected String getDescription() {
        return "Testcase for ComboBox. Test especially edge values(of page changes) when selecting items with keyboard only.";
    }

    @Override
    protected void setup() {
        List<String> items = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            items.add("item " + i);
        }
        ComboBox<String> select = new ComboBox<>("", items);

        final Label value = new Label();

        select.addValueChangeListener(event -> {
            System.err.println("Selected " + event.getValue());
            value.setValue("Selected " + event.getValue());
        });

        getLayout().addComponent(select);
        getLayout().addComponent(value);

    }

}
