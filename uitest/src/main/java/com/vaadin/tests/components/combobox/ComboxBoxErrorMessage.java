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
import com.vaadin.v7.ui.ComboBox;

public class ComboxBoxErrorMessage extends TestBase {

    @Override
    protected void setup() {
        ComboBox cb = new ComboBox("");
        cb.setRequired(true);
        cb.setRequiredError("You must select something");
        addComponent(cb);
    }

    @Override
    protected String getDescription() {
        return "The ComboBox should show an \"You must select something\" tooltip when the cursor is hovering it. Both when hovering the textfield and the dropdown button.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3345;
    }

}
