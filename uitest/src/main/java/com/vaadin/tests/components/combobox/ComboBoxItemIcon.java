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

import com.vaadin.server.ThemeResource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.ComboBox;

public class ComboBoxItemIcon extends TestBase {

    @Override
    protected Integer getTicketNumber() {
        return 2455;
    }

    @Override
    protected String getDescription() {
        return "All items in the ComboBoxes should have icons.";
    }

    @Override
    protected void setup() {
        {
            ComboBox<String> cb = new ComboBox<>();
            cb.setItems("FI", "SE");
            cb.setItemIconGenerator(item -> new ThemeResource(
                    "../tests-tickets/icons/" + item.toLowerCase() + ".gif"));

            addComponent(cb);
        }
        {
            ComboBox<String> cb = new ComboBox<>();
            cb.setItems("Finland", "Australia", "Hungary");
            cb.setItemIconGenerator(
                    item -> new ThemeResource("../tests-tickets/icons/"
                            + item.substring(0, 2).toLowerCase() + ".gif"));

            cb.setValue("Hungary");
            addComponent(cb);
        }
    }

}
