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

public class ComboBoxNavigation extends TestBase {

    @Override
    protected String getDescription() {
        return "Entering e in the field and scrolling down with the arrow keys should always select the next item, also when the page changes. Scrolling back up should always select the previous item, also when changing pages.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2214;
    }

    @Override
    protected void setup() {
        ComboBox<String> cb = new ComboBox<>();
        cb.setDataProvider(new ItemDataProvider(100));

        addComponent(cb);

    }

}
