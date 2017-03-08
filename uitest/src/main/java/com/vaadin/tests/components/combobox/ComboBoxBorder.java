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

import com.vaadin.server.UserError;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.ComboBox;

public class ComboBoxBorder extends TestBase {

    @Override
    protected void setup() {
        setTheme("tests-tickets");

        final ComboBox<String> cb = new ComboBox<>("All errors",
                Arrays.asList("Error", "Error 2"));
        cb.setStyleName("ComboBoxBorder");
        cb.setWidth("200px"); // must have with to reproduce

        cb.addValueChangeListener(
                event -> cb.setComponentError(new UserError("Error")));

        addComponent(cb);

    }

    @Override
    protected String getDescription() {
        return "Adding a border as a result of styleName change should not break the ComboBox";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11267;
    }

}
