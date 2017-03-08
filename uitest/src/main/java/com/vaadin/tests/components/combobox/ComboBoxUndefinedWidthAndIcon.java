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
import com.vaadin.tests.util.ItemDataProvider;
import com.vaadin.ui.ComboBox;

public class ComboBoxUndefinedWidthAndIcon extends TestBase {
    @Override
    protected void setup() {
        ComboBox<String> cb = new ComboBox<>();
        cb.setDataProvider(new ItemDataProvider(200));
        cb.setItemIconGenerator(
                item -> new ThemeResource("../runo/icons/16/users.png"));

        addComponent(cb);
    }

    @Override
    protected String getDescription() {
        return "The width of the ComboBox should be fixed even though it is set to undefined width. The width should not change when changing pages in the dropdown.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 7013;
    }
}
